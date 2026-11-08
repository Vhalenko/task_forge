import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth";

export default function AdminRoute() {
  const { user } = useAuth();

  if (user?.role !== "ADMIN") {
    return <Navigate to="/tasks" replace />;
  }
  return <Outlet />;
}
