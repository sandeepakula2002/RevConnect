import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { ApiResponse, PageResponse, Post } from '../../../shared/models/models';
import { PostService } from '../../../core/services/post.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-feed-page',
  templateUrl: './feed-page.component.html'
})
export class FeedPageComponent implements OnInit {

  posts: Post[] = [];
  loading = true;
  page = 0;
  totalPages = 0;//all pages
  currentUserId: number | null;

  newPostContent = '';
  newPostHashtags = '';
  creatingPost = false;

  constructor(
    private http: HttpClient,
    private postService: PostService,
    private authService: AuthService
  ) {
    this.currentUserId = authService.getCurrentUserId();
  }

  ngOnInit() {
    this.loadFeed();
  }

  loadFeed() {
    this.loading = true;
    this.http.get<ApiResponse<PageResponse<Post>>>(
      `${environment.apiUrl}/feed?page=${this.page}&size=10`
    ).subscribe({
      next: (res) => {
        this.posts = res.data.content;
        this.totalPages = res.data.totalPages;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  createPost() {
    if (!this.newPostContent.trim()) return;
    this.creatingPost = true;

    this.postService.createPost({
      content: this.newPostContent,
      hashtags: this.newPostHashtags
    }).subscribe({
      next: (res) => {
        this.posts.unshift(res.data);
        this.newPostContent = '';
        this.newPostHashtags = '';
        this.creatingPost = false;
      },
      error: () => this.creatingPost = false
    });
  }

  toggleLike(post: Post) {
    if (post.likedByCurrentUser) {
      this.postService.unlikePost(post.id).subscribe(res => {
        post.likedByCurrentUser = false;
        post.likeCount = res.data.likeCount;
      });
    } else {
      this.postService.likePost(post.id).subscribe(res => {
        post.likedByCurrentUser = true;
        post.likeCount = res.data.likeCount;
      });
    }
  }

  deletePost(post: Post, index: number) {
    if (confirm('Delete this post?')) {
      this.postService.deletePost(post.id).subscribe(() => {
        this.posts.splice(index, 1);
      });
    }
  }

  loadMore() {
    if (this.page < this.totalPages - 1) {
      this.page++;
      this.http.get<ApiResponse<PageResponse<Post>>>(
        `${environment.apiUrl}/feed?page=${this.page}&size=10`
      ).subscribe(res => {
        this.posts = [...this.posts, ...res.data.content];
      });
    }
  }
}
