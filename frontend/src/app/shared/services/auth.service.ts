import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private API = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  // ✅ REGISTER
  register(data: any) {
    return this.http.post(`${this.API}/register`, data);
  }

  // ✅ LOGIN
  login(data: any) {
    return this.http.post<any>(`${this.API}/login`, data)
      .pipe(
        tap(res => {
          // save JWT token
          localStorage.setItem('token', res.token);
        })
      );
  }

  // ✅ FORGOT PASSWORD
  forgotPassword(data: any) {
    return this.http.post(
      `${this.API}/forgot-password`,
      data
    );
  }

  // ✅ CHECK LOGIN
  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  // ✅ LOGOUT
  logout() {
    localStorage.removeItem('token');
  }
}
