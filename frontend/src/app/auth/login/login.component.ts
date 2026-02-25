import { Component } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../shared/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  errorMessage = '';
  successMessage = '';
  showForgotPassword = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  loginForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });

  forgotForm = this.fb.group({
    email: [''],
    answer: [''],
    newPassword: ['']
  });

  // ✅ LOGIN
  onSubmit() {

    if (this.loginForm.invalid) return;

    this.authService.login(this.loginForm.value as any)
      .subscribe({
        next: () => {
          alert("Login successful");

          // reload app → dashboard automatically shown
          window.location.reload();
        },
        error: (err) => {
          this.errorMessage =
            err.error?.message || "Invalid credentials";
        }
      });
  }

  // ✅ RESET PASSWORD
  resetPassword() {

    this.authService.forgotPassword(this.forgotForm.value)
      .subscribe({
        next: (res:any) => {
          this.successMessage = res.message;
          this.showForgotPassword = false;
        },
        error: (err) => {
          this.errorMessage =
            err.error?.message || "Reset failed";
        }
      });
  }
}
