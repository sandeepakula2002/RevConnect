import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProfileService } from '../../shared/services/profile.service';
import { ProfileEditComponent } from '../profile-edit/profile-edit.component';

@Component({
  selector: 'app-profile-view',
  standalone: true, // ✅ REQUIRED (Standalone Angular)
  imports: [
    CommonModule,
    ProfileEditComponent   // allows <app-profile-edit>

  templateUrl: './profile-view.component.html',
  styleUrls: ['./profile-view.component.css']
})
export class ProfileViewComponent implements OnInit {

  profile: any = null;
  loading = true;
  errorMessage = '';

  constructor(private profileService: ProfileService) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  // ✅ GET /api/profile/me
  loadProfile() {
    this.profileService.getMyProfile().subscribe({
      next: (res) => {
        this.profile = res;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;

        // profile not created yet
        if (err.status === 404) {
          this.profile = null;
        } else {
          this.errorMessage = 'Failed to load profile';
          console.error(err);
        }
      }
    });
  }

  // called after create/update
  refreshProfile() {
    this.loading = true;
    this.loadProfile();
  }
}
