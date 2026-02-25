import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from './shared/services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet
  ],
  templateUrl: './app.component.html'
})
export class AppComponent {

  constructor(public authService: AuthService) {}

  logout() {
    this.authService.logout();
    window.location.reload();
  }
}
