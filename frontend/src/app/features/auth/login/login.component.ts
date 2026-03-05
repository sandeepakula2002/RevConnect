import { Component } from '@angular/core';
import { FormBuilder, Validators, AbstractControl } from '@angular/forms';
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

  showForgotPassword = false;
  forgotMessage = '';
  forgotError = '';
  forgotLoading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  // ================= LOGIN FORM =================

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
        this.router.navigate(['/feed'], { replaceUrl: true });
      },
      error: (err) => {
        this.error = err.error?.message || 'Invalid username/email or password';
        this.loading = false;
      }
    });
  }

  // ================= FORGOT PASSWORD =================

  forgotForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    confirmPassword: ['', Validators.required]
  }, {
    validators: this.passwordMatchValidator
  });

  passwordMatchValidator(control: AbstractControl) {
    const password = control.get('password')?.value;
    const confirm = control.get('confirmPassword')?.value;
    return password === confirm ? null : { passwordMismatch: true };
  }

  resetPassword() {

    if (this.forgotForm.invalid) return;

    this.forgotLoading = true;
    this.forgotError = '';
    this.forgotMessage = '';

    const payload = {
      email: this.forgotForm.value.email?.trim().toLowerCase(),
      password: this.forgotForm.value.password
    };

    this.authService.forgotPassword(payload).subscribe({
      next: () => {
        this.forgotMessage = 'Password reset successful. Please login.';
        this.forgotLoading = false;

        setTimeout(() => {
          this.showForgotPassword = false;
          this.forgotForm.reset();
        }, 2000);
      },
      error: (err) => {
        this.forgotError = err.error?.message || 'User not found';
        this.forgotLoading = false;
      }
    });
  }

  openForgotPassword() {
    this.showForgotPassword = true;
    this.error = '';
  }

  backToLogin() {
    this.showForgotPassword = false;
    this.forgotError = '';
    this.forgotMessage = '';
  }

}
