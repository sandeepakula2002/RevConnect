import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { ApiResponse, AuthResponse, LoginRequest, RegisterRequest, User } from '../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly API = `${environment.apiUrl}/auth`;
  private readonly TOKEN_KEY = 'revconnect_token';
  private readonly USER_KEY  = 'revconnect_user';

  private currentUserSubject = new BehaviorSubject<AuthResponse | null>(this.loadUser());
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {}

  // ─── Auth API Calls ───────────────────────────────────────────────

  register(request: RegisterRequest): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.API}/register`, request).pipe(
    );
  }

  login(request: LoginRequest): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.API}/login`, request).pipe(
      tap(res => this.storeSession(res.data))
    );
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  // ─── Session Helpers ──────────────────────────────────────────────

  private storeSession(authData: AuthResponse): void {
    localStorage.setItem(this.TOKEN_KEY, authData.token);
    localStorage.setItem(this.USER_KEY, JSON.stringify(authData));
    this.currentUserSubject.next(authData);
  }

  private loadUser(): AuthResponse | null {
    const stored = localStorage.getItem(this.USER_KEY);
    return stored ? JSON.parse(stored) : null;
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    if (!token) return false;
    // Check expiry
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.exp * 1000 > Date.now();
    } catch {
      return false;
    }
  }

  getCurrentUser(): AuthResponse | null {
    return this.currentUserSubject.value;
  }

  getCurrentUserId(): number | null {
    return this.currentUserSubject.value?.userId ?? null;
  }

  getCurrentUsername(): string | null {
    return this.currentUserSubject.value?.username ?? null;
  }

  getUserRole(): string | null {
    return this.currentUserSubject.value?.role ?? null;
  }

  isBusinessOrCreator(): boolean {
    const role = this.getUserRole();
    return role === 'BUSINESS' || role === 'CREATOR';
  }
forgotPassword(data: any) {
  return this.http.post(
    'http://localhost:8080/api/auth/forgot-password',
    data
  );
}
}
