import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  error = '';
  loading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  // ===== EXISTING LOGIN FORM =====
  loginForm = this.fb.group({
    usernameOrEmail: ['', Validators.required],
    password: ['', [Validators.required, Validators.minLength(8)]]
  });

  get f() {
    return this.loginForm.controls;
  }

  onSubmit() {
    if (this.loginForm.invalid) return;

    this.loading = true;
    this.error = '';

    this.authService.login(this.loginForm.getRawValue() as any).subscribe({
      next: () => {
        this.router.navigate(['/feed']);
      },
      error: (err) => {
        this.error = err.error?.message || 'Invalid username/email or password';
        this.loading = false;
      }
    });
  }

  // ===== FORGOT PASSWORD =====

  showForgotPassword = false;
  forgotMessage = '';
  forgotError = '';
  forgotLoading = false;

  forgotForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });

  resetPassword() {

    if (this.forgotForm.invalid) return;

    this.forgotLoading = true;
    this.forgotMessage = '';
    this.forgotError = '';

    const email = this.forgotForm.get('email')?.value?.trim().toLowerCase() || '';
    const password = this.forgotForm.get('password')?.value || '';

    this.authService.forgotPassword({
      email: this.forgotForm.value.email?.trim().toLowerCase(),
      password: this.forgotForm.value.password
    }).subscribe({
      next: () => {
        this.forgotMessage = 'Password reset successful. Please login.';
        this.forgotLoading = false;

        setTimeout(() => {
          this.showForgotPassword = false;
          this.forgotForm.reset();
        }, 1500);
      },
      error: () => {
        this.forgotError = 'User not found';
        this.forgotLoading = false;
      }
    });
  }

  // ✅ OPTIONAL: call this from HTML instead of directly toggling variable
  openForgotPassword() {
    this.showForgotPassword = true;
    this.error = ''; // clear login error
  }

  backToLogin() {
    this.showForgotPassword = false;
    this.forgotError = '';
    this.forgotMessage = '';
  }

}
