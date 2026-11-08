import { useCallback } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import * as projectApi from "../api/projectApi";
import * as teamApi from "../api/teamApi";
import TaskForm from "../components/tasks/TaskForm";
import BackLink from "../components/ui/BackLink";
import EmptyState from "../components/ui/EmptyState";
import ErrorState from "../components/ui/ErrorState";
import Spinner from "../components/ui/Spinner";
import { useAsyncData } from "../hooks/useAsyncData";
import { useAuth } from "../hooks/useAuth";
import { EMPTY_TASK_FORM, toCreateRequest } from "../utils/taskForm";

function NewTaskView({ teamId }: { teamId: number }) {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const fetchProjects = useCallback(() => teamApi.getTeamProjects(teamId), [teamId]);
  const { data: projects, loading, error, reload } = useAsyncData(fetchProjects);

  function renderContent() {
    if (loading) return <Spinner />;
    if (error) return <ErrorState message={error} onRetry={reload} />;

    if (!projects || projects.length === 0) {
      return (
        <EmptyState
          title="No projects yet"
          description="Every task belongs to a project. Ask an admin to create one."
        />
      );
    }

    // /tasks/new?project=5 preselects that project; an unknown id is ignored
    const requested = searchParams.get("project");
    const preselected = projects.find((project) => String(project.id) === requested);

    return (
      <TaskForm
        mode="create"
        projects={projects}
        initialValues={{
          ...EMPTY_TASK_FORM,
          projectId: preselected ? String(preselected.id) : "",
        }}
        submitLabel="Create task"
        onSubmit={async (values) => {
          const created = await projectApi.createTask(
            Number(values.projectId),
            toCreateRequest(values)
          );
          navigate(`/tasks/${created.id}`);
        }}
        onCancel={() => navigate("/tasks")}
      />
    );
  }

  return (
    <div className="mx-auto max-w-3xl">
      <div className="mb-6">
        <BackLink to="/tasks">My tasks</BackLink>
      </div>
      <h1 className="mb-6 text-2xl font-semibold tracking-tight text-white sm:text-3xl">New task</h1>
      <div className="rounded-2xl border border-white/10 bg-ink-900/70 p-5 shadow-[0_24px_60px_-40px_rgb(0_0_0/0.9)] sm:p-7">
        {renderContent()}
      </div>
    </div>
  );
}

export default function NewTaskPage() {
  const { user } = useAuth();

  if (!user?.teamId) {
    return (
      <EmptyState
        title="Join a team first"
        description="You need to be in a team before you can create tasks. Ask an admin to add you."
        action={<BackLink to="/tasks">My tasks</BackLink>}
      />
    );
  }

  return <NewTaskView teamId={user.teamId} />;
}
