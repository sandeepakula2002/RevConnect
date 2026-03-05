import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';

import { LoginComponent } from './login.component';
import { AuthService } from '../../../core/services/auth.service';

describe('LoginComponent', () => {

  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  const mockApi = (data: any) => ({
    success: true,
    data,
    statusCode: 200,
    timestamp: new Date().toISOString()
  });

  beforeEach(async () => {

    authServiceSpy = jasmine.createSpyObj('AuthService', [
      'login',
      'forgotPassword'
    ]);

    await TestBed.configureTestingModule({

      imports: [
        ReactiveFormsModule,
        RouterTestingModule.withRoutes([
          { path: 'feed', component: LoginComponent }
        ])
      ],

      declarations: [
        LoginComponent
      ],

      providers: [
        { provide: AuthService, useValue: authServiceSpy }
      ]

    }).compileComponents();

  });

  beforeEach(() => {

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();

  });

  it('should login successfully', () => {

    authServiceSpy.login.and.returnValue(
      of(mockApi({
        token: 'test-token',
        userId: 1,
        username: 'john',
        role: 'PERSONAL'
      }))
    );

    component.loginForm.setValue({
      usernameOrEmail: 'test',
      password: 'password123'
    });

    component.onSubmit();

    expect(authServiceSpy.login).toHaveBeenCalled();

  });

  it('should show login error', () => {

    authServiceSpy.login.and.returnValue(
      throwError(() => ({ error: { message: 'Invalid login' } }))
    );

    component.loginForm.setValue({
      usernameOrEmail: 'test',
      password: 'password123'
    });

    component.onSubmit();

    expect(component.error).toBeTruthy();

  });

});
