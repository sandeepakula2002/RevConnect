import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService } from '../../../core/services/user.service';
import { PostService } from '../../../core/services/post.service';
import { NetworkService } from '../../../core/services/network.service';
import { AuthService } from '../../../core/services/auth.service';
import { User, Post, PageResponse } from '../../../shared/models/models';

@Component({
  selector: 'app-profile-view',
  templateUrl: './profile-view.component.html'
})
export class ProfileViewComponent implements OnInit {

  user: User | null = null;
  posts: Post[] = [];
  loading = true;
  postsLoading = false;
  isOwnProfile = false;
  activeTab = 'posts';

  constructor(
    private route: ActivatedRoute,
    private userService: UserService,
    private postService: PostService,
    private networkService: NetworkService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.route.params.subscribe(params => {
      const userId = +params['id'];
      this.isOwnProfile = userId === this.authService.getCurrentUserId();
      this.loadProfile(userId);
      this.loadPosts(userId);
    });
  }

  loadProfile(userId: number) {
    this.loading = true;
    this.userService.getUserById(userId).subscribe({
      next: (res) => { this.user = res.data; this.loading = false; },
      error: () => { this.loading = false; this.router.navigate(['/feed']); }
    });
  }

  loadPosts(userId: number) {
    this.postsLoading = true;
    this.postService.getUserPosts(userId).subscribe({
      next: (res) => { this.posts = res.data.content; this.postsLoading = false; },
      error: () => this.postsLoading = false
    });
  }

  toggleFollow() {
    if (!this.user) return;
    if (this.user.isFollowing) {
      this.networkService.unfollow(this.user.id).subscribe(() => {
        this.user!.isFollowing = false;
        this.user!.followerCount--;
      });
    } else {
      this.networkService.follow(this.user.id).subscribe(() => {
        this.user!.isFollowing = true;
        this.user!.followerCount++;
      });
    }
  }

  sendConnectionRequest() {
    if (!this.user) return;
    this.networkService.sendRequest(this.user.id).subscribe({
      next: () => alert('Connection request sent!'),
      error: (err) => alert(err.error?.message || 'Already connected or pending')
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
}
