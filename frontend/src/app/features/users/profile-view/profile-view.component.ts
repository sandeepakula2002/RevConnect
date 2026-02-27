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

  // form submit
  onSubmit() {
    console.log(this.profileForm.value);
    this.saved = true;
    this.error = null;
  }
}
