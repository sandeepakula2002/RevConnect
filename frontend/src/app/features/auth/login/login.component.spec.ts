import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { AuthService } from '../../../core/services/auth.service';
import { ApiResponse } from '../../../core/models/api-response.model'; // adjust path if needed

describe('LoginComponent', () => {

  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  let mockAuthService: jasmine.SpyObj<AuthService>;
  let mockRouter: jasmine.SpyObj<Router>;

  beforeEach(async () => {

    mockAuthService = jasmine.createSpyObj('AuthService', [
      'login',
      'forgotPassword'
    ]);

    mockRouter = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      imports: [ReactiveFormsModule],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // ================= BASIC =================

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // ================= LOGIN SUCCESS =================

  it('should login successfully and navigate to feed', () => {

    const mockResponse: ApiResponse<any> = {
      success: true,
      statusCode: 200,
      data: {},
      timestamp: new Date()
    };

    mockAuthService.login.and.returnValue(of(mockResponse));

    component.loginForm.setValue({
      usernameOrEmail: 'demo',
      password: 'password123'
    });

    component.onSubmit();

    expect(mockAuthService.login).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/feed'], { replaceUrl: true });
  });

  // ================= LOGIN FAILURE =================

  it('should show error on login failure', () => {

    mockAuthService.login.and.returnValue(
      throwError(() => ({
        error: { message: 'Invalid credentials' }
      }))
    );

    component.loginForm.setValue({
      usernameOrEmail: 'demo',
      password: 'wrongpass'
    });

    component.onSubmit();

    expect(component.error).toBe('Invalid credentials');
    expect(component.loading).toBeFalse();
  });

  // ================= FORGOT PASSWORD SUCCESS =================

  it('should reset password successfully', () => {

    const mockResponse: ApiResponse<any> = {
      success: true,
      statusCode: 200,
      data: null,
      timestamp: new Date()
    };

    mockAuthService.forgotPassword.and.returnValue(of(mockResponse));

    component.forgotForm.setValue({
      email: 'test@mail.com',
      password: 'password123',
      confirmPassword: 'password123'
    });

    component.resetPassword();

    expect(mockAuthService.forgotPassword).toHaveBeenCalled();
    expect(component.forgotMessage).toContain('Password reset successful');
  });

  // ================= PASSWORD MISMATCH =================

  it('should not submit if passwords do not match', () => {

    component.forgotForm.setValue({
      email: 'test@mail.com',
      password: 'password123',
      confirmPassword: 'different'
    });

    component.resetPassword();

    expect(mockAuthService.forgotPassword).not.toHaveBeenCalled();
  });

});
