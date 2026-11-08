import api from "./client";
import type { User, UserRole } from "../types/auth";

export const getMe = () => api.get<User>("/users/me").then((res) => res.data);

export const updateMe = (payload: Pick<User, "firstName" | "lastName" | "username" | "email">) =>
  api.put<User>("/users/me", payload).then((res) => res.data);

export const setUserBlocked = (id: number, blocked: boolean) =>
  api.patch(`/users/${id}/blocked`, { blocked }).then(() => undefined);

export const setUserRole = (id: number, role: UserRole) =>
  api.patch(`/users/${id}/role`, { role }).then(() => undefined);
