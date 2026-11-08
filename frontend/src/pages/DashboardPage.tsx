import { useAuth } from "../hooks/useAuth";

export default function DashboardPage() {
  const { user, logout } = useAuth();

  return (
    <div className="min-h-screen px-4 py-6 sm:px-6 lg:px-10 lg:py-10">
      <header className="mx-auto flex max-w-4xl items-center justify-between gap-4 rounded-2xl border border-white/10 bg-ink-900/70 px-5 py-4">
        <span className="flex items-center gap-2.5 text-sm font-semibold tracking-tight text-white">
          <span className="grid size-7 place-items-center rounded-lg bg-accent text-xs font-bold text-ink-950">
            ✦
          </span>
          TaskForge
        </span>
        <div className="flex items-center gap-4 text-sm text-ink-400">
          <span className="hidden sm:inline">
            {user?.firstName} {user?.lastName}
          </span>
          <button
            onClick={logout}
            className="rounded-md font-medium text-ink-300 underline underline-offset-4 transition hover:text-white focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-accent"
          >
            Log out
          </button>
        </div>
      </header>

      <main className="mx-auto mt-10 max-w-4xl">
        <h1 className="text-3xl font-semibold tracking-tight text-white">
          Welcome, {user?.firstName}
        </h1>
        <p className="mt-2 text-sm text-ink-400">Your tasks will appear here.</p>
      </main>
    </div>
  );
}
