export type UserRole = 'USER' | 'ADMIN';

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  userId: number;
  organizationId: number;
  organizationName: string;
  organizationSlug: string;
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
  organizationId: number;
  organizationName: string;
  organizationSlug: string;
  email: string;
  fullName: string;
  role: UserRole;
}
