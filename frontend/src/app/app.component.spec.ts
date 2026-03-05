import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { RouterTestingModule } from '@angular/router/testing';
import { of, BehaviorSubject } from 'rxjs';
import { AuthService } from './core/services/auth.service';
import { NotificationService } from './core/services/notification.service';
import { Component } from '@angular/core';


// Dummy Navbar Component (because template uses <app-navbar>)
@Component({
  selector: 'app-navbar',
  template: '<div>Navbar</div>'
})
class MockNavbarComponent {}

describe('AppComponent', () => {

  let fixture: ComponentFixture<AppComponent>;
  let component: AppComponent;

  let mockAuthService: any;
  let mockNotificationService: any;

  const userSubject = new BehaviorSubject<any>(null);

  beforeEach(async () => {

    mockAuthService = {
      currentUser$: userSubject.asObservable()
    };

    mockNotificationService = {
      startPolling: jasmine.createSpy('startPolling')
    };

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [AppComponent, MockNavbarComponent],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: NotificationService, useValue: mockNotificationService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
  });

  // ================= BASIC =================

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  // ================= NOT LOGGED IN =================

  it('should not show navbar when user is not logged in', () => {

    userSubject.next(null);
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('app-navbar')).toBeNull();
  });

  // ================= LOGGED IN =================

  it('should show navbar and start polling when user logs in', () => {

    userSubject.next({ username: 'demoUser' });
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;

    expect(component.isLoggedIn).toBeTrue();
    expect(mockNotificationService.startPolling).toHaveBeenCalled();
    expect(compiled.querySelector('app-navbar')).not.toBeNull();
  });

});
