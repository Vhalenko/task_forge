import { useCallback } from "react";
import { useNavigate, useParams } from "react-router-dom";
import * as taskApi from "../api/taskApi";
import TaskForm from "../components/tasks/TaskForm";
import BackLink from "../components/ui/BackLink";
import EmptyState from "../components/ui/EmptyState";
import ErrorState from "../components/ui/ErrorState";
import Spinner from "../components/ui/Spinner";
import { useAsyncData } from "../hooks/useAsyncData";
import { useAuth } from "../hooks/useAuth";
import { canManageTask } from "../utils/permissions";
import { taskToFormValues, toUpdateRequest } from "../utils/taskForm";

function EditTaskView({ taskId }: { taskId: number }) {
  const navigate = useNavigate();
  const { user } = useAuth();
  const fetchTask = useCallback(() => taskApi.getTask(taskId), [taskId]);
  const { data: task, loading, error, reload } = useAsyncData(fetchTask);

  function renderContent() {
    if (loading) return <Spinner />;

    if (error || !task) {
      return <ErrorState message={error || "Task not found."} onRetry={reload} />;
    }

    if (!canManageTask(task, user)) {
      return (
        <EmptyState
          title="You cannot edit this task"
          description="Only the creator or an admin can change a task."
        />
      );
    }

    return (
      <TaskForm
        mode="edit"
        initialValues={taskToFormValues(task)}
        submitLabel="Save changes"
        onSubmit={async (values) => {
          await taskApi.updateTask(task.id, toUpdateRequest(values));
          navigate(`/tasks/${task.id}`);
        }}
        onCancel={() => navigate(`/tasks/${task.id}`)}
      />
    );
  }

  return (
    <div className="mx-auto max-w-3xl">
      <div className="mb-6">
        <BackLink to={`/tasks/${taskId}`}>Task</BackLink>
      </div>
      <h1 className="mb-6 text-2xl font-semibold tracking-tight text-white sm:text-3xl">Edit task</h1>
      <div className="rounded-2xl border border-white/10 bg-ink-900/70 p-5 shadow-[0_24px_60px_-40px_rgb(0_0_0/0.9)] sm:p-7">
        {renderContent()}
      </div>
    </div>
  );
}

export default function EditTaskPage() {
  const { taskId } = useParams();
  const id = Number(taskId);

  if (!Number.isInteger(id) || id <= 0) {
    return (
      <EmptyState
        title="Task not found"
        description="This link does not point to a task."
        action={<BackLink to="/tasks">My tasks</BackLink>}
      />
    );
  }

  return <EditTaskView key={id} taskId={id} />;
}
