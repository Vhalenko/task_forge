import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import AuthProvider from "./context/AuthProvider";
import AdminRoute from "./components/routing/AdminRoute";
import ProtectedRoute from "./components/routing/ProtectedRoute";
import PublicOnlyRoute from "./components/routing/PublicOnlyRoute";
import AppLayout from "./components/layout/AppLayout";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import LandingPage from "./pages/LandingPage";
import TasksPage from "./pages/TasksPage";
import NewTaskPage from "./pages/NewTaskPage";
import TaskDetailsPage from "./pages/TaskDetailsPage";
import EditTaskPage from "./pages/EditTaskPage";
import ProjectsPage from "./pages/ProjectsPage";
import ProjectPage from "./pages/ProjectPage";
import AdminTeamsPage from "./pages/admin/AdminTeamsPage";
import AdminTeamPage from "./pages/admin/AdminTeamPage";
import ProfilePage from "./pages/ProfilePage";

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/" element={<LandingPage />} />

          <Route element={<PublicOnlyRoute />}>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
          </Route>

          <Route element={<ProtectedRoute />}>
            <Route element={<AppLayout />}>
              <Route path="/tasks" element={<TasksPage />} />
              <Route path="/tasks/new" element={<NewTaskPage />} />
              <Route path="/tasks/:taskId" element={<TaskDetailsPage />} />
              <Route path="/tasks/:taskId/edit" element={<EditTaskPage />} />
              <Route path="/projects" element={<ProjectsPage />} />
              <Route path="/projects/:projectId" element={<ProjectPage />} />
              <Route path="/profile" element={<ProfilePage />} />

              <Route element={<AdminRoute />}>
                <Route path="/admin/teams" element={<AdminTeamsPage />} />
                <Route path="/admin/teams/:teamId" element={<AdminTeamPage />} />
              </Route>
            </Route>
          </Route>

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}
