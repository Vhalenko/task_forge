import axios, { isAxiosError } from "axios";
import { authStorage } from "../utils/authStorage";

const api = axios.create({ baseURL: "/api/v1" });

api.interceptors.request.use((config) => {
  const token = authStorage.getToken();
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error: unknown) => {
    // 401 on login/register just means wrong credentials, so only handle other endpoints
    if (
      isAxiosError(error) &&
      error.response?.status === 401 &&
      !error.config?.url?.startsWith("/auth/")
    ) {
      authStorage.clear();
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);

export default api;
