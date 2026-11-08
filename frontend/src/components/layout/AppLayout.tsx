import { Outlet } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth";
import Navbar from "./Navbar";

export default function AppLayout() {
  const { user } = useAuth();

  return (
    <div className="min-h-screen lg:pl-64">
      <Navbar />
      <main className="mx-auto w-full max-w-[1440px] px-4 py-6 sm:px-6 lg:px-10 lg:py-10">
        {user && !user.teamId && (
          <div
            role="status"
            className="mb-6 flex items-start gap-3 rounded-xl border border-amber-400/25 bg-amber-400/10 px-4 py-3 text-sm leading-6 text-amber-200"
          >
            <span aria-hidden="true" className="mt-2 size-1.5 shrink-0 rounded-full bg-amber-300" />
            <p>You are not in a team yet. Ask an admin to add you using your email address.</p>
          </div>
        )}
        <Outlet />
      </main>
    </div>
  );
}
