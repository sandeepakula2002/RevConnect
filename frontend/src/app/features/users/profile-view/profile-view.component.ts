import { Component, OnInit } from '@angular/core';
<<<<<<< HEAD
import { Router } from '@angular/router';
import { FormBuilder, FormGroup } from '@angular/forms';
=======
import { ActivatedRoute } from '@angular/router';

import { UserService } from '../../../core/services/user.service';
import { PostService } from '../../../core/services/post.service';
import { NetworkService } from '../../../core/services/network.service';
import { AuthService } from '../../../core/services/auth.service';

import { User, Post, Comment } from '../../../shared/models/models';
>>>>>>> 357a03b3b2746b616d126e6ba6913e7a1053d827

@Component({
  selector: 'app-profile-view',
  templateUrl: './profile-view.component.html',
  styleUrls: ['./profile-view.component.css']
})
export class ProfileViewComponent implements OnInit {

<<<<<<< HEAD
  // ================= PROFILE DATA =================
  profile: any = {
    id: 1,
    name: 'Gopala',
    bio: 'Java Developer',
    role: 'CREATOR'
  };

  user: any = this.profile;

  // ================= REQUIRED PROPERTIES =================
  userId!: number;
  saved: boolean = false;
  error: string | null = null;
  isBusinessOrCreator: boolean = false;

  // ================= POSTS =================
  posts: any[] = [];
  postsLoading: boolean = false;

  // ================= FORM =================
  profileForm!: FormGroup;

  constructor(
    private router: Router,
    private fb: FormBuilder
=======
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
>>>>>>> 357a03b3b2746b616d126e6ba6913e7a1053d827
  ) {}

  ngOnInit(): void {

<<<<<<< HEAD
    // set userId
    this.userId = this.profile.id;

    // role check
    this.isBusinessOrCreator =
      this.profile.role === 'BUSINESS' || this.profile.role === 'CREATOR';

    // form init
    this.profileForm = this.fb.group({
      name: [this.profile.name],
      bio: [this.profile.bio]
    });
  }

  // ================= NAVIGATION =================
  goToProfile(id: number) {
    this.router.navigate(['/profile', id]);
  }

  // ================= FORM SUBMIT =================
  onSubmit() {
    console.log(this.profileForm.value);
    this.saved = true;
    this.error = null;
  }

  // ================= POSTS =================
  loadPosts(userId: number) {
    this.postsLoading = true;

    // placeholder (service call removed during merge)
    setTimeout(() => {
      this.postsLoading = false;
    }, 500);
  }

  // ================= FOLLOW =================
  toggleFollow() {
    if (!this.user) return;

    this.user.isFollowing = !this.user.isFollowing;
    this.user.followerCount =
      (this.user.followerCount || 0) + (this.user.isFollowing ? 1 : -1);
  }

  // ================= CONNECTION =================
  sendConnectionRequest() {
    alert('Connection request sent!');
  }

  // ================= LIKE =================
  toggleLike(post: any) {
    post.likedByCurrentUser = !post.likedByCurrentUser;
    post.likeCount =
      (post.likeCount || 0) + (post.likedByCurrentUser ? 1 : -1);
=======
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
>>>>>>> 357a03b3b2746b616d126e6ba6913e7a1053d827
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
