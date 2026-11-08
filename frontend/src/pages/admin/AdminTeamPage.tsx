import { useCallback, useState, type FormEvent } from "react";
import { Link, useParams } from "react-router-dom";
import * as projectApi from "../../api/projectApi";
import * as teamApi from "../../api/teamApi";
import MemberRow from "../../components/admin/MemberRow";
import NameForm from "../../components/admin/NameForm";
import PageHeader from "../../components/layout/PageHeader";
import BackLink from "../../components/ui/BackLink";
import Button from "../../components/ui/Button";
import ConfirmAction from "../../components/ui/ConfirmAction";
import EmptyState from "../../components/ui/EmptyState";
import ErrorState from "../../components/ui/ErrorState";
import Spinner from "../../components/ui/Spinner";
import { fieldClass } from "../../components/ui/fieldStyles";
import { useAsyncData } from "../../hooks/useAsyncData";
import { useAuth } from "../../hooks/useAuth";
import type { User } from "../../types/auth";

const tabClass = (active: boolean) =>
  `rounded-lg px-4 py-2 text-sm font-medium transition focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-accent ${
    active ? "bg-white/10 text-white shadow-sm" : "text-ink-400 hover:text-white"
  }`;

function AdminTeamView({ teamId }: { teamId: number }) {
  const { user: me } = useAuth();
  const [tab, setTab] = useState<"members" | "projects">("members");
  const [email, setEmail] = useState("");
  const [inviteMessage, setInviteMessage] = useState("");
  const [inviteError, setInviteError] = useState(false);

  const fetchTeam = useCallback(() => teamApi.getTeam(teamId), [teamId]);
  const fetchUsers = useCallback(() => teamApi.getTeamUsers(teamId), [teamId]);
  const fetchProjects = useCallback(() => teamApi.getTeamProjects(teamId), [teamId]);
  const teamState = useAsyncData(fetchTeam);
  const usersState = useAsyncData(fetchUsers);
  const projectsState = useAsyncData(fetchProjects);

  const replaceMember = (updated: User) =>
    usersState.setData((prev) =>
      (prev ?? []).map((member) => (member.id === updated.id ? updated : member))
    );

  const handleCreateProject = async (name: string) => {
    const created = await teamApi.createProject(teamId, { name });
    projectsState.setData((prev) => [...(prev ?? []), created]);
  };

  const handleDeleteProject = async (id: number) => {
    await projectApi.deleteProject(id);
    projectsState.setData((prev) => (prev ?? []).filter((project) => project.id !== id));
  };

  const handleInvite = async (event: FormEvent) => {
    event.preventDefault();
    setInviteMessage("");
    setInviteError(false);
    try {
      const member = await teamApi.inviteUser(teamId, email);
      usersState.setData((prev) => [...(prev ?? []).filter((item) => item.id !== member.id), member]);
      setEmail("");
      setInviteMessage("User added to the team.");
    } catch {
      setInviteError(true);
      setInviteMessage("We couldn't find or add this email address.");
    }
  };

  function renderMembers() {
    if (usersState.loading) return <Spinner />;
    if (usersState.error) {
      return <ErrorState message={usersState.error} onRetry={usersState.reload} />;
    }

    const members = usersState.data ?? [];
    if (members.length === 0) {
      return <EmptyState title="No members yet" description="This team has no members." />;
    }

    return (
      <div className="overflow-hidden rounded-2xl border border-white/10 bg-ink-900/70 px-5">
        <ul className="divide-y divide-white/10">
          {members.map((member) => (
            <MemberRow
              key={member.id}
              member={member}
              isSelf={member.id === me?.id}
              onChange={replaceMember}
            />
          ))}
        </ul>
      </div>
    );
  }

  function renderProjects() {
    if (projectsState.loading) return <Spinner />;
    if (projectsState.error) {
      return <ErrorState message={projectsState.error} onRetry={projectsState.reload} />;
    }

    const projects = projectsState.data ?? [];
    if (projects.length === 0) {
      return <EmptyState title="No projects yet" description="Add the first project above." />;
    }

    return (
      <div className="overflow-hidden rounded-2xl border border-white/10 bg-ink-900/70 px-5">
        <ul className="divide-y divide-white/10">
          {projects.map((project) => (
            <li key={project.id} className="flex items-center justify-between gap-4 py-4">
              <Link
                to={`/projects/${project.id}`}
                className="min-w-0 truncate rounded-md text-sm font-medium text-ink-100 underline-offset-4 transition hover:text-accent hover:underline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-accent"
              >
                {project.name}
              </Link>
              <ConfirmAction
                label="Delete"
                question="Delete this project?"
                confirmLabel="Yes, delete"
                onConfirm={() => handleDeleteProject(project.id)}
              />
            </li>
          ))}
        </ul>
      </div>
    );
  }

  if (teamState.error) {
    return (
      <div>
        <div className="mb-6">
          <BackLink to="/admin/teams">Teams</BackLink>
        </div>
        <ErrorState message={teamState.error} onRetry={teamState.reload} />
      </div>
    );
  }

  return (
    <div>
      <div className="mb-6">
        <BackLink to="/admin/teams">Teams</BackLink>
      </div>
      <PageHeader
        title={teamState.data?.name ?? "Team"}
        subtitle="Manage team members, roles, and projects."
      />

      <div
        role="group"
        aria-label="Team sections"
        className="mb-6 inline-flex gap-1 rounded-xl border border-white/10 bg-ink-900/70 p-1"
      >
        <button
          type="button"
          onClick={() => setTab("members")}
          aria-pressed={tab === "members"}
          className={tabClass(tab === "members")}
        >
          Members
        </button>
        <button
          type="button"
          onClick={() => setTab("projects")}
          aria-pressed={tab === "projects"}
          className={tabClass(tab === "projects")}
        >
          Projects
        </button>
      </div>

      {tab === "members" ? (
        <section>
          <div className="mb-5 rounded-2xl border border-accent/20 bg-accent/[0.05] p-4 sm:p-5">
            <div className="mb-3">
              <h2 className="text-sm font-semibold text-white">Invite a teammate</h2>
              <p className="mt-1 text-xs leading-5 text-ink-400">
                Add an existing TaskForge account by email.
              </p>
            </div>
            <form onSubmit={handleInvite} className="flex flex-col gap-2 sm:flex-row">
              <input
                required
                type="email"
                value={email}
                onChange={(event) => setEmail(event.target.value)}
                placeholder="teammate@company.com"
                aria-label="Teammate email"
                className={fieldClass(false, "min-w-0 flex-1")}
              />
              <Button type="submit" fullWidth={false} className="shrink-0">
                Add to team
              </Button>
            </form>
            {inviteMessage && (
              <p
                className={`mt-2.5 text-xs font-medium ${
                  inviteError ? "text-rose-300" : "text-accent"
                }`}
              >
                {inviteMessage}
              </p>
            )}
          </div>
          {renderMembers()}
        </section>
      ) : (
        <section>
          <h2 className="mb-3 text-lg font-semibold tracking-tight text-white">Projects</h2>
          <div className="mb-5 max-w-xl rounded-2xl border border-white/10 bg-ink-900/60 p-4 sm:p-5">
            <NameForm
              label="Project name"
              placeholder="New project name"
              submitLabel="Add project"
              onSubmit={handleCreateProject}
            />
          </div>
          {renderProjects()}
        </section>
      )}
    </div>
  );
}

export default function AdminTeamPage() {
  const { teamId } = useParams();
  const id = Number(teamId);

  if (!Number.isInteger(id) || id <= 0) {
    return (
      <EmptyState
        title="Team not found"
        description="This link does not point to a team."
        action={<BackLink to="/admin/teams">Teams</BackLink>}
      />
    );
  }

  return <AdminTeamView key={id} teamId={id} />;
}
