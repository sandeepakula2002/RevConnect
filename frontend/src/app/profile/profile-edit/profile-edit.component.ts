import { Component, EventEmitter, Input, Output, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { ProfileService } from '../../shared/services/profile.service';

@Component({
  selector: 'app-profile-edit',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './profile-edit.component.html',
  styleUrls: ['./profile-edit.component.css']
})
export class ProfileEditComponent implements OnInit {

  @Input() profile: any;
  @Output() saved = new EventEmitter<void>();

  message = '';

  form = this.fb.group({
    name: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    bio: ['']
  });

  constructor(
    private fb: FormBuilder,
    private profileService: ProfileService
  ) {}

  ngOnInit(): void {
    if (this.profile) {
      this.form.patchValue(this.profile);
    }
  }

  save() {

    if (this.form.invalid) return;

    const request = this.profile
      ? this.profileService.updateProfile(this.form.value)
      : this.profileService.createProfile(this.form.value);

    request.subscribe({
      next: () => {
        this.message = 'Profile saved successfully';
        this.saved.emit(); // notify parent
      },
      error: () => {
        this.message = 'Failed to save profile';
      }
    });
  }
}
