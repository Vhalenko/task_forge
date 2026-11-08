export type UserRole = "USER" | "ADMIN";

export interface User {
  id: number;
  firstName: string;
  lastName: string;
  username: string;
  email: string;
  role: UserRole;
  teamId: number | null;
  teamName: string | null;
  blocked: boolean;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  username: string;
  email: string;
  password: string;
}

// Assumes AuthDTO is { token, user }. Adjust if your fields differ.
export interface AuthResponse {
  token: string;
  user: User;
}

export interface AuthLocationState {
  from?: { pathname: string };
  registered?: boolean;
}
