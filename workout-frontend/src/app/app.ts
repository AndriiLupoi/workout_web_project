import { Component, inject, signal } from '@angular/core';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { NavbarComponent } from './core/layout/navbar/navbar.component';
import { AuthService } from './core/services/auth';
import { AsyncPipe } from '@angular/common';
import { filter } from 'rxjs';

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
  private router = inject(Router);

  isAuthenticated$ = this.authService.isAuthenticated$;
  isAuthPage = signal(false);

  private readonly AUTH_PATHS = ['/auth/'];

  ngOnInit(): void {
    this.loadThemePreference();

    this.checkAuthPage(this.router.url);

    this.router.events.pipe(
      filter(e => e instanceof NavigationEnd)
    ).subscribe((e: any) => {
      this.checkAuthPage(e.urlAfterRedirects);
    });
  }

  private checkAuthPage(url: string): void {
    this.isAuthPage.set(
      this.AUTH_PATHS.some(path => url.startsWith(path))
    );
  }

  private loadThemePreference(): void {
    const savedTheme = localStorage.getItem('theme') || 'light';
    document.documentElement.setAttribute('data-theme', savedTheme);
  }
}
