import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-profile-view',
  templateUrl: './profile-view.component.html',
  styleUrls: ['./profile-view.component.css']
})
export class ProfileViewComponent implements OnInit {

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
  }
}
