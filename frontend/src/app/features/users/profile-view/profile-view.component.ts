import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-profile-view',
  templateUrl: './profile-view.component.html',
  styleUrls: ['./profile-view.component.css']
})
export class ProfileViewComponent implements OnInit {

  // Profile data
  profile: any = {
    id: 1,
    name: 'Gopala',
    bio: 'Java Developer',
    role: 'CREATOR'
  };

  // Required properties
  userId!: number;
  saved: boolean = false;
  error: string | null = null;
  isBusinessOrCreator: boolean = false;

  // Reactive form
  profileForm!: FormGroup;

  constructor(
    private router: Router,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
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

  // navigation method (used in HTML)
  goToProfile(id: number) {
    this.router.navigate(['/profile', id]);
  }

<<<<<<< HEAD
  // form submit
  onSubmit() {
    console.log(this.profileForm.value);
    this.saved = true;
    this.error = null;
=======
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
>>>>>>> 024a8670ed5f87238d612fc64a122d7d5c4893b4
  }
}
