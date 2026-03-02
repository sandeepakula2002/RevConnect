import { Component, OnInit, HostListener } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subject, debounceTime, distinctUntilChanged, switchMap } from 'rxjs';

import { PostService } from '../../core/services/post.service';
import { UserService } from '../../core/services/user.service';
import { AuthService } from '../../core/services/auth.service';
import { Post, PageResponse, ApiResponse } from '../../shared/models/models';

@Component({
  selector: 'app-feed-page',
  templateUrl: './feed-page.component.html'
})
export class FeedPageComponent implements OnInit {

  posts: (Post & {
    showComments?: boolean;
    newComment?: string;
    comments?: any[];
  })[] = [];

  loading = true;
  loadingMore = false;
  page = 0;
  lastPage = false;

  currentUserId: number | null = null;

  newPostContent = '';
  newPostHashtags = '';
  creatingPost = false;

  private searchSubject = new Subject<string>();
  users: any[] = [];

  constructor(
    private postService: PostService,
    private userService: UserService,
    private authService: AuthService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {

    this.currentUserId = this.authService.getCurrentUserId();

    this.loadFeed();

    // 🔥 Open post from notification
    this.route.params.subscribe(params => {

      const postId = params['id'];

      if (postId) {

        this.postService.getPost(postId).subscribe(res => {

          const post = {
            ...res.data,
            showComments: true,
            newComment: '',
            comments: []
          };

          if (!this.posts.find(p => p.id === post.id)) {
            this.posts.unshift(post);
          }

          setTimeout(() => {
            const element = document.getElementById('post-' + postId);
            element?.scrollIntoView({ behavior: 'smooth' });
          }, 500);
        });
      }
    });

    // 🔎 Smart search
    this.searchSubject.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      switchMap(query => this.userService.searchUsers(query))
    ).subscribe(res => {
      this.users = res.data;
    });
  }

  // ================= INFINITE SCROLL =================
  @HostListener('window:scroll', [])
  onScroll(): void {

    if (this.loadingMore || this.lastPage) return;

    if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight - 300) {
      this.loadMore();
    }
  }

  loadMore(): void {

    this.loadingMore = true;
    this.page++;

    this.postService.getUserPosts(this.currentUserId!, this.page, 10)
      .subscribe({
        next: (res: ApiResponse<PageResponse<Post>>) => {

          const newPosts = res.data.content.map(post => ({
            ...post,
            showComments: false,
            newComment: '',
            comments: []
          }));

          this.posts = [...this.posts, ...newPosts];
          this.lastPage = res.data.last;
          this.loadingMore = false;
        },
        error: () => this.loadingMore = false
      });
  }

  loadFeed(): void {

    this.loading = true;
    this.page = 0;

    this.postService.getUserPosts(this.currentUserId!, 0, 10)
      .subscribe({
        next: (res) => {

          this.posts = res.data.content.map(post => ({
            ...post,
            showComments: false,
            newComment: '',
            comments: []
          }));

          this.lastPage = res.data.last;
          this.loading = false;
        },
        error: () => this.loading = false
      });
  }

  // ================= CREATE POST =================
  createPost(): void {

    if (!this.newPostContent.trim()) return;

    this.creatingPost = true;

    this.postService.createPost({
      content: this.newPostContent,
      hashtags: this.newPostHashtags
    }).subscribe({
      next: (res) => {

        this.posts.unshift({
          ...res.data,
          showComments: false,
          newComment: '',
          comments: []
        });

        this.newPostContent = '';
        this.newPostHashtags = '';
        this.creatingPost = false;
      },
      error: () => this.creatingPost = false
    });
  }

  // ================= LIKE =================
  toggleLike(post: any): void {

    if (post.likedByCurrentUser) {

      this.postService.unlikePost(post.id).subscribe(() => {
        post.likedByCurrentUser = false;
        post.likeCount--;
      });

    } else {

      this.postService.likePost(post.id).subscribe(() => {
        post.likedByCurrentUser = true;
        post.likeCount++;
      });
    }
  }

  // ================= COMMENTS =================
  loadComments(post: any): void {

    this.postService.getComments(post.id, 0)
      .subscribe(res => {
        post.comments = res.data.content;
      });
  }

  addComment(post: any): void {

    if (!post.newComment?.trim()) return;

    this.postService.addComment(post.id, post.newComment)
      .subscribe(() => {

        post.comments.push({
          user: { username: 'You' },
          content: post.newComment
        });

        post.commentCount++;
        post.newComment = '';
      });
  }

  // ================= SEARCH =================
  onSearchInput(value: string): void {

    if (!value.trim()) {
      this.users = [];
      return;
    }

    this.searchSubject.next(value);
  }
}
