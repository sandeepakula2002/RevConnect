import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private storageKey = 'revconnect-theme';

  constructor() {
    const saved = localStorage.getItem(this.storageKey);
    if (saved === 'light') {
      document.body.classList.add('light-theme');
    }
  }

  toggle() {
    if (document.body.classList.contains('light-theme')) {
      document.body.classList.remove('light-theme');
      localStorage.setItem(this.storageKey, 'dark');
    } else {
      document.body.classList.add('light-theme');
      localStorage.setItem(this.storageKey, 'light');
    }
  }

  isLight() {
    return document.body.classList.contains('light-theme');
  }
}
