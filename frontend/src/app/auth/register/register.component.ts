import { Component } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../shared/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,

  // ✅ REQUIRED IMPORTS
  imports: [CommonModule, ReactiveFormsModule, RouterLink],

  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {

  errorMessage = '';

  registerForm = this.fb.group({
    username: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
    securityQuestion: ['Your favorite color?', Validators.required],
    securityAnswer: ['', Validators.required],
    passwordHint: ['']
  });

  constructor(
    private fb: FormBuilder,
    private authService: AuthService
  ) {}

  onSubmit() {

    if (this.registerForm.invalid) {
      this.errorMessage = "Fill all fields correctly";
      return;
    }

    this.authService.register(this.registerForm.value)
      .subscribe({
        next: () => {
          alert("Registration successful. Please login.");
          window.location.href = '/login';
        },
        error: (err) => {
          this.errorMessage =
            err.error?.message || "Registration failed";
        }
      });
  }
}
