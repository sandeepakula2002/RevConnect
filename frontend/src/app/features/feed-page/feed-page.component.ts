import { Component, OnInit } from '@angular/core';
import { PostService } from '../../core/services/post.service';
import { AuthService } from '../../core/services/auth.service';
import { Post } from '../../shared/models/models';

@Component({
  selector: 'app-feed-page',
  templateUrl: './feed-page.component.html'
})
export class FeedPageComponent implements OnInit {

  posts: Post[] = [];
  loading = true;

  page = 0;
  totalPages = 0;

  currentUserId: number | null;

  newPostContent = '';
  newPostHashtags = '';
  creatingPost = false;

  constructor(
    private postService: PostService,
    private authService: AuthService
  ) {
    this.currentUserId = authService.getCurrentUserId();
  }

  ngOnInit(): void {
    this.loadFeed();
  }

  // =============================
  // LOAD FEED
  // =============================

  loadFeed(): void {
    this.loading = true;

    this.postService.getFeed(this.page, 10).subscribe({
      next: (res) => {
        this.posts = res.data.content;
        this.totalPages = res.data.totalPages;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  loadMore(): void {
    if (this.page >= this.totalPages - 1) return;

    this.page++;

    this.postService.getFeed(this.page, 10).subscribe(res => {
      this.posts = [...this.posts, ...res.data.content];
    });
  }

  // =============================
  // CREATE POST
  // =============================

  createPost(): void {
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

  // =============================
  // LIKE
  // =============================

  toggleLike(post: Post): void {

    const previousState = post.likedByCurrentUser;
    const previousCount = post.likeCount;

    // Optimistic update
    post.likedByCurrentUser = !previousState;
    post.likeCount += previousState ? -1 : 1;

    const request = previousState
      ? this.postService.unlikePost(post.id)
      : this.postService.likePost(post.id);

    request.subscribe({
      next: (res) => {
        post.likedByCurrentUser = res.data.liked;
        post.likeCount = res.data.likeCount;
      },
      error: () => {
        post.likedByCurrentUser = previousState;
        post.likeCount = previousCount;
      }
    });
  }

  // =============================
  // DELETE
  // =============================

  deletePost(post: Post, index: number): void {

    if (!confirm('Are you sure you want to delete this post?')) return;

    this.postService.deletePost(post.id).subscribe(() => {
      this.posts.splice(index, 1);
    });
  }
}
