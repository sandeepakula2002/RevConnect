import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { UserService } from '../../../core/services/user.service';
import { PostService } from '../../../core/services/post.service';
import { NetworkService } from '../../../core/services/network.service';
import { AuthService } from '../../../core/services/auth.service';

import { User, Post, Comment } from '../../../shared/models/models';

@Component({
  selector: 'app-profile-view',
  templateUrl: './profile-view.component.html',
  styleUrls: ['./profile-view.component.css']
})
export class ProfileViewComponent implements OnInit {

  profile!: User;
  posts: Post[] = [];

  userId!: number;
  currentUserId: number = 0;

  loading = true;
  postsLoading = true;

  isOwnProfile = false;

  constructor(
    private route: ActivatedRoute,
    private userService: UserService,
    private postService: PostService,
    private networkService: NetworkService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {

    this.currentUserId =
      this.authService.getCurrentUserId() ?? 0;

    this.route.params.subscribe(params => {
      this.userId = +params['id'];
      this.isOwnProfile = this.userId === this.currentUserId;

      this.loadProfile();
      this.loadPosts();
    });
  }

  // ================= PROFILE =================

  loadProfile(): void {

    this.loading = true;

    this.userService.getUserById(this.userId)
      .subscribe({
        next: (response) => {
          this.profile = response.data;
          this.loading = false;
        },
        error: () => {
          this.loading = false;
        }
      });
  }

  // ================= POSTS =================

  loadPosts(): void {

    this.postsLoading = true;

    this.postService.getUserPosts(this.userId, 0, 5)
      .subscribe({
        next: (response) => {

          this.posts = response.data.content;

          // Load comments for each post
          this.posts.forEach(post => {
            this.loadComments(post);
          });

          this.postsLoading = false;
        },
        error: () => {
          this.postsLoading = false;
        }
      });
  }

  loadComments(post: Post): void {

    this.postService.getComments(post.id, 0)
      .subscribe({
        next: (res) => {
          post.comments = res.data.content;
        },
        error: () => {
          post.comments = [];
        }
      });
  }

  // ================= FOLLOW =================

  toggleFollow(): void {

    if (this.profile.isFollowing) {

      this.networkService.unfollow(this.userId)
        .subscribe(() => {
          this.profile.isFollowing = false;
          if (this.profile.followerCount > 0) {
            this.profile.followerCount--;
          }
        });

    } else {

      this.networkService.follow(this.userId)
        .subscribe(() => {
          this.profile.isFollowing = true;
          this.profile.followerCount++;
        });
    }
  }

  // ================= LIKE =================

  toggleLike(post: Post): void {

    if (post.likedByCurrentUser) {

      this.postService.unlikePost(post.id).subscribe(() => {
        post.likedByCurrentUser = false;
        if (post.likeCount > 0) {
          post.likeCount--;
        }
      });

    } else {

      this.postService.likePost(post.id).subscribe(() => {
        post.likedByCurrentUser = true;
        post.likeCount++;
      });

    }
  }

  // ================= COMMENT =================

  addComment(post: Post): void {

    if (!post.newComment || !post.newComment.trim()) return;

    const commentText = post.newComment;

    this.postService.addComment(post.id, commentText)
      .subscribe(() => {

        post.newComment = '';

        // Reload only comments for this post
        this.loadComments(post);

      });
  }
}
