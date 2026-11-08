import type { User } from "../types/auth";

const TOKEN_KEY = "token";
const USER_KEY = "user";

export const authStorage = {
  getToken: (): string | null => localStorage.getItem(TOKEN_KEY),

  getUser: (): User | null => {
    const raw = localStorage.getItem(USER_KEY);
    if (!raw) return null;
    try {
      return JSON.parse(raw) as User;
    } catch {
      return null;
    }
  },

  save: (token: string, user: User): void => {
    localStorage.setItem(TOKEN_KEY, token);
    localStorage.setItem(USER_KEY, JSON.stringify(user));
  },

  clear: (): void => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
  },
};
