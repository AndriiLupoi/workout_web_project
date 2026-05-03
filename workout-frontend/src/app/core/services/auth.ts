import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, map, BehaviorSubject } from 'rxjs';

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface JwtResponse {
  accessToken: string;
  tokenType: string;
}

export interface UserResponse {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
}

export type UserRole = 'USER' | 'ADMIN' | 'OWNER';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly tokenKey = 'jwt_token';
  private readonly apiUrl = '/api/v1/auth';

  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasToken());
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  // Роль береться одразу при старті з токена що вже є
  private roleSubject = new BehaviorSubject<UserRole>(this.getRoleFromToken());
  public role$ = this.roleSubject.asObservable();

  constructor(private http: HttpClient) {}

  private hasToken(): boolean {
    return !!localStorage.getItem(this.tokenKey);
  }

  // Декодує JWT payload без бібліотек (Base64)
  private decodeToken(token: string): any {
    try {
      const payload = token.split('.')[1];
      // Base64url → Base64 → decode
      const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
      const jsonStr = decodeURIComponent(
        atob(base64)
          .split('')
          .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      return JSON.parse(jsonStr);
    } catch {
      return null;
    }
  }

  private getRoleFromToken(): UserRole {
    const token = localStorage.getItem(this.tokenKey);
    if (!token) return 'USER';
    const decoded = this.decodeToken(token);
    return (decoded?.role as UserRole) ?? 'USER';
  }

  register(request: RegisterRequest): Observable<UserResponse> {
    return this.http.post<UserResponse>(`${this.apiUrl}/register`, request);
  }

  login(request: LoginRequest): Observable<void> {
    return this.http.post<JwtResponse>(`${this.apiUrl}/login`, request).pipe(
      tap(res => {
        localStorage.setItem(this.tokenKey, res.accessToken);
        this.isAuthenticatedSubject.next(true);
        // Оновлюємо роль одразу після логіну
        this.roleSubject.next(this.getRoleFromToken());
      }),
      map(() => void 0)
    );
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    this.isAuthenticatedSubject.next(false);
    this.roleSubject.next('USER');
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  isLoggedIn(): boolean {
    return this.isAuthenticatedSubject.value;
  }

  getRole(): UserRole {
    return this.roleSubject.value;
  }

  isAdmin(): boolean {
    return this.roleSubject.value === 'ADMIN' || this.roleSubject.value === 'OWNER';
  }

  isOwner(): boolean {
    return this.roleSubject.value === 'OWNER';
  }
}
