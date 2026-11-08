import { useCallback } from "react";
import { useParams } from "react-router-dom";
import * as taskApi from "../api/taskApi";
import CommentSection from "../components/comments/CommentSection";
import TaskActions from "../components/tasks/TaskActions";
import TaskDetails from "../components/tasks/TaskDetails";
import BackLink from "../components/ui/BackLink";
import EmptyState from "../components/ui/EmptyState";
import ErrorState from "../components/ui/ErrorState";
import Spinner from "../components/ui/Spinner";
import { useAsyncData } from "../hooks/useAsyncData";

function TaskDetailsView({ taskId }: { taskId: number }) {
  const fetchTask = useCallback(() => taskApi.getTask(taskId), [taskId]);
  const { data: task, setData, loading, error, reload } = useAsyncData(fetchTask);

  function renderContent() {
    if (loading) return <Spinner />;

    if (error || !task) {
      return <ErrorState message={error || "Task not found."} onRetry={reload} />;
    }

    return (
      <>
        <TaskDetails
          task={task}
          actions={<TaskActions task={task} onTaskChange={setData} />}
        />
        <div className="mt-12">
          <CommentSection taskId={task.id} />
        </div>
      </>
    );
  }

  return (
    <div className="mx-auto max-w-4xl">
      <div className="mb-6">
        <BackLink to="/tasks">My tasks</BackLink>
      </div>
      {renderContent()}
    </div>
  );
}

export default function TaskDetailsPage() {
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

  return <TaskDetailsView key={id} taskId={id} />;
}
