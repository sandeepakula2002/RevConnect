import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { RegisterComponent } from './register.component';
import { AuthService } from '../../../core/services/auth.service';

describe('RegisterComponent', () => {

  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  const mockApi = (data: any) => ({
    success: true,
    data,
    statusCode: 200,
    timestamp: new Date().toISOString()
  });

  beforeEach(async () => {

    authServiceSpy = jasmine.createSpyObj('AuthService', ['register']);

    await TestBed.configureTestingModule({

      imports: [
        ReactiveFormsModule,
        RouterTestingModule
      ],

      declarations: [
        RegisterComponent
      ],

      providers: [
        { provide: AuthService, useValue: authServiceSpy }
      ]

    }).compileComponents();

  });

  beforeEach(() => {

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();

  });

  it('should register user', () => {

    authServiceSpy.register.and.returnValue(
      of(mockApi({
        token: 'test-token',
        userId: 1,
        username: 'test',
        role: 'PERSONAL'
      }))
    );

    component.registerForm.setValue({
      username: 'test',
      email: 'test@test.com',
      password: 'password123',
      firstName: '',
      lastName: '',
      role: 'PERSONAL'
    });

    component.onSubmit();

    expect(authServiceSpy.register).toHaveBeenCalled();

  });

});
