import { Injectable, computed, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { apiUrl } from '../api/api-url';
import { AuthResponse, AuthUser, LoginRequest, RegisterRequest, UserRole } from './auth.models';

const TOKEN_KEY = 'booking.accessToken';
const USER_KEY = 'booking.user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly tokenSignal = signal<string | null>(null);
  private readonly userSignal = signal<AuthUser | null>(null);

  readonly token = this.tokenSignal.asReadonly();
  readonly currentUser = this.userSignal.asReadonly();
  readonly isAuthenticated = computed(() => !!this.tokenSignal());
  readonly isAdmin = computed(() => this.userSignal()?.role === 'ADMIN');

  constructor(private readonly http: HttpClient) {
    const user = this.readUser();
    const token = this.readToken();
    if (user && token) {
      this.tokenSignal.set(token);
      this.userSignal.set(user);
    } else {
      localStorage.removeItem(TOKEN_KEY);
      localStorage.removeItem(USER_KEY);
    }
  }

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
      organizationId: response.organizationId,
      organizationName: response.organizationName,
      organizationSlug: response.organizationSlug,
      email: response.email,
      fullName: response.fullName,
      role: response.role ?? 'USER',
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
      const parsed = JSON.parse(raw) as AuthUser & { role?: UserRole };
      if (parsed.organizationId == null || !parsed.organizationName) {
        return null;
      }
      return {
        userId: parsed.userId,
        organizationId: parsed.organizationId,
        organizationName: parsed.organizationName,
        organizationSlug: parsed.organizationSlug,
        email: parsed.email,
        fullName: parsed.fullName,
        role: parsed.role ?? 'USER',
      };
    } catch {
      return null;
    }
  }
}
