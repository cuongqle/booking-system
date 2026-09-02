export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  userId: number;
  email: string;
  fullName: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  fullName: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthUser {
  userId: number;
  email: string;
  fullName: string;
}
