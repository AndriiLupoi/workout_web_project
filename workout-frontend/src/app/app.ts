import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './core/layout/navbar/navbar.component';
import { AuthService } from './core/services/auth';
import { AsyncPipe } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    NavbarComponent,
    AsyncPipe
  ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  private authService = inject(AuthService);

  isAuthenticated$ = this.authService.isAuthenticated$;   // повинен бути BehaviorSubject або Signal в AuthService

  ngOnInit(): void {
    this.loadThemePreference();
  }

  private loadThemePreference(): void {
    const savedTheme = localStorage.getItem('theme') || 'light';
    document.documentElement.setAttribute('data-theme', savedTheme);
  }
}
