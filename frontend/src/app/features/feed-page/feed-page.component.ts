import { Component, OnInit, HostListener } from '@angular/core';
import { Subject, debounceTime, distinctUntilChanged, switchMap } from 'rxjs';

import { PostService } from '../../core/services/post.service';
import { UserService } from '../../core/services/user.service';
import { AuthService } from '../../core/services/auth.service';

import { Post, ApiResponse, PageResponse, User, Comment } from '../../shared/models/models';

@Component({
  selector: 'app-feed-page',
  templateUrl: './feed-page.component.html'
})
export class FeedPageComponent implements OnInit {

  posts: any[] = [];

  page = 0;
  lastPage = false;
  loading = true;
  loadingMore = false;

  currentUserId = 0;

  newPostContent = '';
  newPostHashtags = '';
  creatingPost = false;

  users: User[] = [];
  private searchSubject = new Subject<string>();

  constructor(
    private postService: PostService,
    private userService: UserService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {

    this.currentUserId = this.authService.getCurrentUserId() ?? 0;

    this.loadFeed();
    this.setupSearch();

  }

  // ================= SEARCH =================

  setupSearch(): void {

    this.searchSubject.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      switchMap(q => this.userService.searchUsers(q))
    ).subscribe(res => {
      this.users = res.data ?? [];
    });

  }

  onSearchInput(value: string): void {

    if (!value.trim()) {
      this.users = [];
      return;
    }

    this.searchSubject.next(value);

  }

  // ================= FEED =================

  loadFeed(): void {

    this.loading = true;

    this.postService.getUserPosts(this.currentUserId, 0, 10)
      .subscribe((res: ApiResponse<PageResponse<Post>>) => {

        this.posts = res.data.content.map(p => ({
          ...p,
          comments: [],
          showComments: false,
          newComment: ''
        }));

        this.lastPage = res.data.last;
        this.loading = false;

      });

  }

  @HostListener('window:scroll')
  onScroll(): void {

    if (this.loadingMore || this.lastPage) return;

    if (window.innerHeight + window.scrollY >= document.body.offsetHeight - 200) {
      this.loadMore();
    }

  }

  loadMore(): void {

    this.loadingMore = true;
    this.page++;

    this.postService.getUserPosts(this.currentUserId, this.page, 10)
      .subscribe((res: ApiResponse<PageResponse<Post>>) => {

        const newPosts = res.data.content.map(p => ({
          ...p,
          comments: [],
          showComments: false,
          newComment: ''
        }));

        this.posts = [...this.posts, ...newPosts];

        this.lastPage = res.data.last;
        this.loadingMore = false;

      });

  }

  // ================= POST =================

  createPost(): void {

    if (!this.newPostContent.trim()) return;

    this.creatingPost = true;

    this.postService.createPost({
      content: this.newPostContent,
      hashtags: this.newPostHashtags
    }).subscribe(res => {

      this.posts.unshift({
        ...res.data,
        comments: [],
        showComments: false,
        newComment: ''
      });

      this.newPostContent = '';
      this.newPostHashtags = '';
      this.creatingPost = false;

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

  toggleComments(post: any): void {

    post.showComments = !post.showComments;

    if (post.showComments) {
      this.loadComments(post);
    }

  }

 loadComments(post: any): void {

   this.postService.getComments(post.id, 0)
     .subscribe(res => {

       const comments = res.data?.content ?? [];

       comments.forEach((c: any) => {
         c.replies = c.replies ?? [];
         c.replyText = '';
       });

       // newest comment first
       post.comments = comments.reverse();

     });

 }

  addComment(post: any): void {

    if (!post.newComment?.trim()) return;

    this.postService.addComment(post.id, post.newComment)
      .subscribe(res => {

       post.comments.unshift(res.data);
        post.newComment = '';

      });

  }

  // ================= COMMENT LIKE =================

  likeComment(comment: any): void {

    if (comment.likedByCurrentUser) {

      comment.likedByCurrentUser = false;
      comment.likeCount--;

    } else {

      comment.likedByCurrentUser = true;
      comment.likeCount++;

    }

    this.postService.likeComment(comment.id).subscribe();

  }

  // ================= DELETE COMMENT =================

  deleteComment(comment: any, post: any): void {

    this.postService.deleteComment(comment.id)
      .subscribe(() => {

        post.comments = post.comments.filter(
          (c: any) => c.id !== comment.id
        );

      });

  }

  // ================= REPLY =================

  reply(post: any, parent: any): void {

    const content = parent.replyText;

    if (!content?.trim()) return;

    this.postService.replyToComment(post.id, parent.id, content)
      .subscribe(res => {

        if (!parent.replies) {
          parent.replies = [];
        }

        parent.replies.push(res.data);
        parent.replyText = '';

      });

  }

  // ================= FOLLOW =================

  follow(user: User): void {

    console.log("Follow user", user.username);

  }

  // ================= HASHTAG =================

  formatContent(text: string): string[] {

    if (!text) return [];

    return text.split(' ');

  }

  searchHashtag(tag: string): void {

    const clean = tag.replace('#', '');

    this.onSearchInput(clean);

  }

}
