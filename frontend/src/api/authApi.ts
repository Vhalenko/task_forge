import api from "./client";
import type { AuthResponse, LoginRequest, RegisterRequest, User } from "../types/auth";

export const login = (credentials: LoginRequest) =>
  api.post<AuthResponse>("/auth/login", credentials).then((res) => res.data);

export const register = (payload: RegisterRequest) =>
  api.post<User>("/auth/register", payload).then((res) => res.data);
