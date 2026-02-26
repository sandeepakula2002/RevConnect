import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService } from '../../../core/services/user.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-profile-edit',
  template: `
    <div class="container py-4" style="max-width:700px;">
      <div class="d-flex align-items-center mb-4">
        <button class="btn btn-outline-secondary me-3" (click)="router.navigate(['/profile', userId])">
          ← Back
        </button>
        <h4 class="fw-bold mb-0">Edit Profile</h4>
      </div>

      <div class="alert alert-success" *ngIf="saved">Profile updated successfully!</div>
      <div class="alert alert-danger" *ngIf="error">{{ error }}</div>

      <div class="card shadow-sm">
        <div class="card-body p-4">
          <form [formGroup]="profileForm" (ngSubmit)="onSubmit()">

            <div class="row g-3 mb-3">
              <div class="col">
                <label class="form-label fw-semibold">First Name</label>
                <input type="text" class="form-control" formControlName="firstName" />
              </div>
              <div class="col">
                <label class="form-label fw-semibold">Last Name</label>
                <input type="text" class="form-control" formControlName="lastName" />
              </div>
            </div>

            <div class="mb-3">
              <label class="form-label fw-semibold">Bio</label>
              <textarea class="form-control" rows="3" formControlName="bio"
                        placeholder="Tell us about yourself"></textarea>
            </div>

            <div class="mb-3">
              <label class="form-label fw-semibold">Location</label>
              <input type="text" class="form-control" formControlName="location" placeholder="City, Country" />
            </div>

            <div class="mb-3">
              <label class="form-label fw-semibold">Website</label>
              <input type="url" class="form-control" formControlName="website" placeholder="https://..." />
            </div>

            <div class="mb-3">
              <label class="form-label fw-semibold">Privacy</label>
              <select class="form-select" formControlName="privacy">
                <option value="PUBLIC">Public - Anyone can see my profile</option>
                <option value="PRIVATE">Private - Only connections can see</option>
              </select>
            </div>

            <!-- Business/Creator extra fields -->
            <ng-container *ngIf="isBusinessOrCreator">
              <hr>
              <h6 class="fw-bold mb-3">Business/Creator Info</h6>
              <div class="mb-3">
                <label class="form-label fw-semibold">Business/Creator Name</label>
                <input type="text" class="form-control" formControlName="businessName" />
              </div>
              <div class="mb-3">
                <label class="form-label fw-semibold">Category / Industry</label>
                <input type="text" class="form-control" formControlName="category"
                       placeholder="e.g. Technology, Fashion, Food" />
              </div>
              <div class="row g-3 mb-3">
                <div class="col">
                  <label class="form-label fw-semibold">Contact Email</label>
                  <input type="email" class="form-control" formControlName="contactEmail" />
                </div>
                <div class="col">
                  <label class="form-label fw-semibold">Contact Phone</label>
                  <input type="tel" class="form-control" formControlName="contactPhone" />
                </div>
              </div>
              <div class="mb-3">
                <label class="form-label fw-semibold">Business Address</label>
                <input type="text" class="form-control" formControlName="businessAddress" />
              </div>
              <div class="mb-3">
                <label class="form-label fw-semibold">Business Hours</label>
                <input type="text" class="form-control" formControlName="businessHours"
                       placeholder="e.g. Mon-Fri 9AM-5PM" />
              </div>
            </ng-container>

            <div class="d-flex gap-3 justify-content-end mt-4">
              <button type="button" class="btn btn-outline-secondary"
                      (click)="router.navigate(['/profile', userId])">Cancel</button>
              <button type="submit" class="btn btn-primary" [disabled]="loading">
                <span *ngIf="loading" class="spinner-border spinner-border-sm me-2"></span>
                Save Changes
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  `
})
export class ProfileEditComponent implements OnInit {

  profileForm!: FormGroup;
  userId!: number;
  loading = false;
  saved = false;
  error = '';
  isBusinessOrCreator = false;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    public router: Router,
    private userService: UserService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.userId = +this.route.snapshot.params['id'];
    this.isBusinessOrCreator = this.authService.isBusinessOrCreator();

    this.profileForm = this.fb.group({
      firstName: [''], lastName: [''], bio: [''],
      location: [''], website: [''], privacy: ['PUBLIC'],
      businessName: [''], category: [''],
      contactEmail: [''], contactPhone: [''],
      businessAddress: [''], businessHours: ['']
    });

    this.userService.getUserById(this.userId).subscribe(res => {
      this.profileForm.patchValue(res.data);
    });
  }

  onSubmit() {
    this.loading = true;
    this.saved = false;
    this.userService.updateProfile(this.userId, this.profileForm.value).subscribe({
      next: () => { this.saved = true; this.loading = false; },
      error: (err) => { this.error = err.error?.message || 'Update failed'; this.loading = false; }
    });
  }
}
