<<<<<<< HEAD
import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { environment } from '../../../environments/environment';
import { ApiResponse, PageResponse, Post } from '../../shared/models/models';
import { PostService } from '../../core/services/post.service';
import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';
=======
import { Component, OnInit, HostListener } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subject, debounceTime, distinctUntilChanged, switchMap } from 'rxjs';

import { PostService } from '../../core/services/post.service';
import { UserService } from '../../core/services/user.service';
import { AuthService } from '../../core/services/auth.service';
import { Post, PageResponse, ApiResponse } from '../../shared/models/models';
>>>>>>> 357a03b3b2746b616d126e6ba6913e7a1053d827

@Component({
  selector: 'app-feed-page',
  templateUrl: './feed-page.component.html'
})
export class FeedPageComponent implements OnInit {

<<<<<<< HEAD
  //  use any[] to allow UI properties (important fix)
  posts: any[] = [];

  loading = true;
  page = 0;
  totalPages = 0;
  currentUserId: number | null;
=======
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
>>>>>>> 357a03b3b2746b616d126e6ba6913e7a1053d827

  newPostContent = '';
  newPostHashtags = '';
  creatingPost = false;

<<<<<<< HEAD
  // SEARCH
  searchQuery: string = '';
  users: any[] = [];

  constructor(
    private http: HttpClient,
    private postService: PostService,
    private authService: AuthService,
    private userService: UserService
  ) {
    this.currentUserId = this.authService.getCurrentUserId();
  }

  ngOnInit() {
    this.loadFeed();
  }

  // ================= LOAD FEED =================
  loadFeed() {
    this.loading = true;

    this.http.get<ApiResponse<PageResponse<Post>>>(
      `${environment.apiUrl}/feed?page=${this.page}&size=10`
    ).subscribe({
      next: (res) => {

        // add UI fields safely
        this.posts = res.data.content.map((post: any) => ({
          ...post,
          showComments: false,
          newComment: '',
          comments: post.comments || []
        }));

        this.totalPages = res.data.totalPages;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  // ================= CREATE POST =================
  createPost() {
=======
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
>>>>>>> 357a03b3b2746b616d126e6ba6913e7a1053d827

    if (!this.newPostContent.trim()) return;

    this.creatingPost = true;

    this.postService.createPost({
      content: this.newPostContent,
      hashtags: this.newPostHashtags
    }).subscribe({
      next: (res) => {

<<<<<<< HEAD
        const newPost = {
=======
        this.posts.unshift({
>>>>>>> 357a03b3b2746b616d126e6ba6913e7a1053d827
          ...res.data,
          showComments: false,
          newComment: '',
          comments: []
<<<<<<< HEAD
        };

        this.posts.unshift(newPost);
=======
        });
>>>>>>> 357a03b3b2746b616d126e6ba6913e7a1053d827

        this.newPostContent = '';
        this.newPostHashtags = '';
        this.creatingPost = false;
      },
      error: () => this.creatingPost = false
    });
  }

  // ================= LIKE =================
<<<<<<< HEAD
  toggleLike(post: any) {

    if (post.likedByCurrentUser) {
      this.postService.unlikePost(post.id).subscribe(res => {
        post.likedByCurrentUser = false;
        post.likeCount = res.data.likeCount;
      });
    } else {
      this.postService.likePost(post.id).subscribe(res => {
        post.likedByCurrentUser = true;
        post.likeCount = res.data.likeCount;
=======
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
>>>>>>> 357a03b3b2746b616d126e6ba6913e7a1053d827
      });
    }
  }

<<<<<<< HEAD
  // ================= ADD COMMENT (UI ONLY) =================
  addComment(post: any) {

    if (!post.newComment?.trim()) return;

    post.comments.push({
      username: 'You',
      text: post.newComment
    });

    post.newComment = '';
  }

  // ================= DELETE =================
  deletePost(post: any, index: number) {
    if (confirm('Delete this post?')) {
      this.postService.deletePost(post.id).subscribe(() => {
        this.posts.splice(index, 1);
      });
    }
  }

  // ================= LOAD MORE =================
  loadMore() {

    if (this.page < this.totalPages - 1) {

      this.page++;

      this.http.get<ApiResponse<PageResponse<Post>>>(
        `${environment.apiUrl}/feed?page=${this.page}&size=10`
      ).subscribe(res => {

        const newPosts = res.data.content.map((post: any) => ({
          ...post,
          showComments: false,
          newComment: '',
          comments: post.comments || []
        }));

        this.posts = [...this.posts, ...newPosts];
      });
    }
  }

  // ================= SEARCH =================
  searchUsers() {

    if (!this.searchQuery.trim()) {
=======
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
>>>>>>> 357a03b3b2746b616d126e6ba6913e7a1053d827
      this.users = [];
      return;
    }

<<<<<<< HEAD
    this.userService.searchUsers(this.searchQuery).subscribe({
      next: (res) => this.users = res.data,
      error: (err) => console.error('Search failed', err)
    });
=======
    this.searchSubject.next(value);
>>>>>>> 357a03b3b2746b616d126e6ba6913e7a1053d827
  }
}
