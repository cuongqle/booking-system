import { Injectable, computed, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { apiUrl } from '../api/api-url';
import { AuthResponse, AuthUser, LoginRequest, RegisterRequest } from './auth.models';

const TOKEN_KEY = 'booking.accessToken';
const USER_KEY = 'booking.user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly tokenSignal = signal<string | null>(this.readToken());
  private readonly userSignal = signal<AuthUser | null>(this.readUser());

  readonly token = this.tokenSignal.asReadonly();
  readonly currentUser = this.userSignal.asReadonly();
  readonly isAuthenticated = computed(() => !!this.tokenSignal());

  constructor(private readonly http: HttpClient) {}

  register(payload: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(apiUrl('/auth/register'), payload)
      .pipe(tap((response) => this.persistSession(response)));
  }

  login(payload: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(apiUrl('/auth/login'), payload)
      .pipe(tap((response) => this.persistSession(response)));
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this.tokenSignal.set(null);
    this.userSignal.set(null);
  }

  private persistSession(response: AuthResponse): void {
    const user: AuthUser = {
      userId: response.userId,
      email: response.email,
      fullName: response.fullName,
    };
    localStorage.setItem(TOKEN_KEY, response.accessToken);
    localStorage.setItem(USER_KEY, JSON.stringify(user));
    this.tokenSignal.set(response.accessToken);
    this.userSignal.set(user);
  }

  private readToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  private readUser(): AuthUser | null {
    const raw = localStorage.getItem(USER_KEY);
    if (!raw) {
      return null;
    }
    try {
      return JSON.parse(raw) as AuthUser;
    } catch {
      return null;
    }
  }
}
