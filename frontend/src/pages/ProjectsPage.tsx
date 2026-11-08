import { useCallback } from "react";
import { Link } from "react-router-dom";
import * as teamApi from "../api/teamApi";
import PageHeader from "../components/layout/PageHeader";
import EmptyState from "../components/ui/EmptyState";
import ErrorState from "../components/ui/ErrorState";
import Spinner from "../components/ui/Spinner";
import { useAsyncData } from "../hooks/useAsyncData";
import { useAuth } from "../hooks/useAuth";

function ProjectsView({ teamId, teamName }: { teamId: number; teamName: string | null }) {
  const fetchProjects = useCallback(() => teamApi.getTeamProjects(teamId), [teamId]);
  const { data: projects, loading, error, reload } = useAsyncData(fetchProjects);

  function renderContent() {
    if (loading) return <Spinner />;
    if (error) return <ErrorState message={error} onRetry={reload} />;

    if (!projects || projects.length === 0) {
      return (
        <EmptyState
          title="No projects yet"
          description="An admin can create projects for your team."
        />
      );
    }

    return (
      <ul className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
        {projects.map((project) => (
          <li key={project.id}>
            <Link
              to={`/projects/${project.id}`}
              className="group flex h-full flex-col justify-between rounded-2xl border border-white/10 bg-ink-900/70 p-5 transition hover:-translate-y-0.5 hover:border-accent/35 hover:bg-ink-850 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-accent"
            >
              <span className="flex items-center gap-3">
                <span
                  aria-hidden="true"
                  className="grid size-9 shrink-0 place-items-center rounded-xl border border-white/10 bg-white/5 text-accent"
                >
                  <svg
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth={1.7}
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    className="size-4"
                  >
                    <path d="M3.5 7.5A2.5 2.5 0 0 1 6 5h3l2 2.5h7A2.5 2.5 0 0 1 20.5 10v6.5A2.5 2.5 0 0 1 18 19H6a2.5 2.5 0 0 1-2.5-2.5z" />
                  </svg>
                </span>
                <span className="min-w-0 truncate text-sm font-medium text-white">{project.name}</span>
              </span>
              <span className="mt-6 flex items-center gap-1.5 text-xs font-medium text-ink-400 transition group-hover:text-accent">
                Open board
                <svg
                  aria-hidden="true"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth={1.8}
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  className="size-3.5"
                >
                  <path d="M5 12h14M13 6l6 6-6 6" />
                </svg>
              </span>
            </Link>
          </li>
        ))}
      </ul>
    );
  }

  return (
    <div>
      <PageHeader title="Projects" subtitle={teamName ?? undefined} />
      {renderContent()}
    </div>
  );
}

export default function ProjectsPage() {
  const { user } = useAuth();

  if (!user?.teamId) {
    return (
      <EmptyState
        title="Join a team first"
        description="Projects belong to a team. Ask an admin to add you."
      />
    );
  }

  return <ProjectsView teamId={user.teamId} teamName={user.teamName} />;
}
