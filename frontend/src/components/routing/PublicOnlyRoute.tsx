import { Navigate, Outlet, useLocation } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth";
import type { AuthLocationState } from "../../types/auth";

export default function PublicOnlyRoute() {
  const { isAuthenticated } = useAuth();
  const location = useLocation();
  const state = location.state as AuthLocationState | null;

  if (isAuthenticated) {
    return <Navigate to={state?.from?.pathname ?? "/tasks"} replace />;
  }
  return <Outlet />;
}
