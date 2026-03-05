import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { UserService } from '../../../core/services/user.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-profile-edit',
  templateUrl: './profile-edit.component.html'
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

  ngOnInit(): void {

    this.userId = +this.route.snapshot.params['id'];

    this.isBusinessOrCreator =
      this.authService.isBusinessOrCreator();

    this.profileForm = this.fb.group({
      firstName: [''],
      lastName: [''],
      bio: [''],
      location: [''],
      website: [''],
      privacy: ['PUBLIC'],

      businessName: [''],
      category: [''],
      contactEmail: [''],
      contactPhone: [''],
      businessAddress: [''],
      businessHours: ['']
    });

    this.loadUser();
  }

  loadUser(): void {

    this.userService.getUserById(this.userId)
      .subscribe(res => {

        this.profileForm.patchValue(res.data);

      });

  }

  onSubmit(): void {

    if (!this.profileForm) return;

    this.loading = true;
    this.saved = false;
    this.error = '';

    this.userService
      .updateProfile(this.userId, this.profileForm.value)
      .subscribe({

        next: () => {

          this.saved = true;
          this.loading = false;

        },

        error: (err) => {

          this.error =
            err.error?.message || 'Update failed';

          this.loading = false;

        }

      });

  }

}
