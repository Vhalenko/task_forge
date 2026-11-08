import { Link, NavLink } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth";
import NavIcon from "./NavIcon";

const linkClass = ({ isActive }: { isActive: boolean }) =>
  `relative flex shrink-0 items-center gap-2.5 rounded-xl px-2.5 py-2.5 text-sm font-medium transition focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-accent sm:gap-3 sm:px-3 [&_svg]:transition-colors ${
    isActive
      ? "bg-white/[0.07] text-white after:absolute after:left-0 after:top-1/2 after:h-5 after:w-0.5 after:-translate-y-1/2 after:rounded-full after:bg-accent after:content-[''] [&_svg]:text-accent"
      : "text-ink-400 hover:bg-white/5 hover:text-ink-100"
  }`;

export default function Navbar() {
  const { user, logout } = useAuth();

  return (
    <header className="border-b border-white/10 bg-ink-900/80 backdrop-blur-xl lg:fixed lg:inset-y-0 lg:left-0 lg:z-20 lg:flex lg:w-64 lg:flex-col lg:border-b-0 lg:border-r">
      <div className="flex items-center justify-between gap-3 px-5 py-4 lg:block lg:px-5 lg:py-6">
        <Link
          to="/tasks"
          className="flex min-w-0 items-center gap-2.5 rounded-lg focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-accent"
        >
          <span className="grid size-8 shrink-0 place-items-center rounded-lg bg-accent text-sm font-bold text-ink-950">
            ✦
          </span>
          <span className="text-base font-semibold tracking-tight text-white">TaskForge</span>
        </Link>
        <p className="mt-2 hidden truncate pl-0.5 text-[11px] font-medium uppercase tracking-[0.18em] text-ink-400 lg:block">
          {user?.teamName ?? "Team workspace"}
        </p>
        <button
          onClick={logout}
          className="rounded-lg px-3 py-1.5 text-xs font-medium text-ink-400 transition hover:bg-white/5 hover:text-white focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-accent lg:hidden"
        >
          Log out
        </button>
      </div>

      <nav
        aria-label="Main"
        className="flex flex-wrap gap-1 px-3 pb-3 lg:flex-col lg:flex-nowrap lg:gap-1 lg:px-3 lg:py-6"
      >
        <NavLink to="/tasks" className={linkClass}>
          <NavIcon name="tasks" />
          My tasks
        </NavLink>
        <NavLink to="/projects" className={linkClass}>
          <NavIcon name="projects" />
          Projects
        </NavLink>
        <NavLink to="/profile" className={linkClass}>
          <NavIcon name="profile" />
          Profile
        </NavLink>
        {user?.role === "ADMIN" && (
          <NavLink to="/admin/teams" className={linkClass}>
            <NavIcon name="admin" />
            Admin
          </NavLink>
        )}
      </nav>

      <div className="mt-auto hidden border-t border-white/10 p-4 lg:block">
        <div className="flex items-center gap-3 rounded-2xl border border-white/10 bg-white/[0.03] p-3">
          <div className="grid size-9 shrink-0 place-items-center rounded-full bg-accent text-sm font-semibold text-ink-950">
            {user?.firstName?.[0] ?? "U"}
          </div>
          <div className="min-w-0">
            <p className="truncate text-sm font-medium text-white">
              {user?.firstName} {user?.lastName}
            </p>
            <p className="truncate text-xs text-ink-400">{user?.email}</p>
          </div>
        </div>
        <button
          onClick={logout}
          className="mt-2 flex w-full items-center gap-2 rounded-lg px-3 py-2 text-left text-sm text-ink-400 transition hover:bg-white/5 hover:text-white focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-accent"
        >
          <svg
            aria-hidden="true"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth={1.7}
            strokeLinecap="round"
            strokeLinejoin="round"
            className="size-4"
          >
            <path d="M15 17l5-5-5-5" />
            <path d="M20 12H9" />
            <path d="M12 4H6a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h6" />
          </svg>
          Log out
        </button>
      </div>
    </header>
  );
}
