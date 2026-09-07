export type UserRole = 'USER' | 'ADMIN' | 'SUPER_ADMIN';

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  userId: number;
  organizationId: number | null;
  organizationName: string | null;
  organizationSlug: string | null;
  email: string;
  fullName: string;
  role: UserRole;
}

export interface RegisterRequest {
  email: string;
  password: string;
  fullName: string;
  organizationName?: string;
  organizationSlug?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthUser {
  userId: number;
  organizationId: number | null;
  organizationName: string | null;
  organizationSlug: string | null;
  email: string;
  fullName: string;
  role: UserRole;
}
