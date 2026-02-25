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

  errorMessage: string = '';
  successMessage: string = '';
  showForgotPassword = false;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  // ✅ LOGIN FORM
  loginForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });

  // ✅ FORGOT PASSWORD FORM
  forgotForm = this.fb.group({
    email: [''],
    answer: [''],
    newPassword: ['']
  });

  // ================= LOGIN =================
  onSubmit() {

    // clear previous messages
    this.errorMessage = '';
    this.successMessage = '';

    if (this.loginForm.invalid) {
      this.errorMessage = "Please enter valid email and password";
      return;
    }

    this.loading = true;

    this.authService.login(this.loginForm.value as any)
      .subscribe({
        next: (res: any) => {

          this.loading = false;

          // save token (if backend sends token)
          if (res?.token) {
            localStorage.setItem('token', res.token);
          }

          // ✅ navigate instead of reload
          this.router.navigate(['/dashboard']);
        },

        error: (err) => {
          this.loading = false;

          console.log("Login Error:", err);

          if (err.status === 401) {
            this.errorMessage = "Invalid email or password";
          } else if (err.status === 404) {
            this.errorMessage = "User not found";
          } else if (err.error?.message) {
            this.errorMessage = err.error.message;
          } else {
            this.errorMessage = "Login failed. Please try again.";
          }
        }
      });
  }

  // ================= RESET PASSWORD =================
  resetPassword() {

    this.errorMessage = '';
    this.successMessage = '';

    this.authService.forgotPassword(this.forgotForm.value)
      .subscribe({
        next: (res:any) => {
          this.successMessage = res.message || "Password reset successful";
          this.showForgotPassword = false;
          this.forgotForm.reset();
        },
        error: (err) => {
          this.errorMessage =
            err.error?.message || "Reset failed. Try again.";
        }
      });
  }
}
