import { Component, OnInit } from '@angular/core';
import { environment } from '../../../../environments/environment';

import { ApiResponse, PageResponse, Post, Comment }
from '../../../shared/models/models';

import { PostService }
from '../../../core/services/post.service';

import { AuthService }
from '../../../core/services/auth.service';

@Component({
  selector: 'app-feed-page',
  templateUrl: './feed-page.component.html',
  styleUrls: ['./feed-page.component.css']
})
export class FeedPageComponent implements OnInit {

  posts: Post[] = [];
  page = 0;
  size = 10;
  loading = false;
  error = '';

  constructor(
    private postService: PostService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadFeed();
  }

  loadFeed(): void {
    this.loading = true;
    this.error = '';

    this.postService.getFeed(this.page, this.size).subscribe({
      next: (res: ApiResponse<PageResponse<Post>>) => {
        this.posts = res.data.content;
        this.loading = false;
      },
      error: (err: any) => {
        this.error = err?.error?.message || 'Failed to load feed';
        this.loading = false;
      }
    });
  }

  likePost(post: Post): void {
    this.postService.likePost(post.id).subscribe({
      next: () => {
        post.likeCount++;
        post.likedByCurrentUser = true;
      },
      error: (err: any) => {
        console.error(err);
      }
    });
  }

  unlikePost(post: Post): void {
    this.postService.unlikePost(post.id).subscribe({
      next: () => {
        post.likeCount--;
        post.likedByCurrentUser = false;
      },
      error: (err: any) => {
        console.error(err);
      }
    });
  }

  addComment(post: Post, commentText: string): void {
    if (!commentText.trim()) return;

    this.postService.addComment(post.id, commentText).subscribe({
      next: (res: ApiResponse<Comment>) => {
        if (!post.comments) post.comments = [];
        post.comments.push(res.data);
        post.commentCount++;
      },
      error: (err: any) => {
        console.error(err);
      }
    });
  }

  deletePost(post: Post): void {
    this.postService.deletePost(post.id).subscribe({
      next: () => {
        this.posts = this.posts.filter(p => p.id !== post.id);
      },
      error: (err: any) => {
        console.error(err);
      }
    });
  }
}
