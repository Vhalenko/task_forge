import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import * as taskApi from "../../api/taskApi";
import { useAuth } from "../../hooks/useAuth";
import type { Task } from "../../types/task";
import { parseApiError } from "../../utils/apiError";
import { canManageTask } from "../../utils/permissions";

interface TaskActionsProps {
  task: Task;
  onTaskChange: (task: Task) => void;
}

const actionClass =
  "rounded-md text-ink-300 transition hover:text-white focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-accent";

export default function TaskActions({ task, onTaskChange }: TaskActionsProps) {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [assigning, setAssigning] = useState(false);
  const [confirmingDelete, setConfirmingDelete] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const [error, setError] = useState("");

  if (!user) return null;

  const isAssigned = task.assignees.some((assignee) => assignee.id === user.id);
  const canManage = canManageTask(task, user);

  const handleAssign = async () => {
    setAssigning(true);
    setError("");
    try {
      await taskApi.assignUser(task.id, user.id);
      onTaskChange(await taskApi.getTask(task.id));
    } catch (err) {
      setError(parseApiError(err).message);
    } finally {
      setAssigning(false);
    }
  };

  const handleDelete = async () => {
    setDeleting(true);
    setError("");
    try {
      await taskApi.deleteTask(task.id);
      navigate("/tasks", { replace: true });
    } catch (err) {
      setError(parseApiError(err).message);
      setDeleting(false);
    }
  };

  return (
    <div className="flex flex-col items-start gap-2 sm:items-end">
      <div className="flex flex-wrap items-center gap-x-4 gap-y-2 text-sm">
        {!isAssigned && (
          <button
            type="button"
            onClick={handleAssign}
            disabled={assigning}
            className="rounded-md font-semibold text-accent underline underline-offset-4 transition hover:text-accent-strong focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-accent disabled:opacity-50"
          >
            {assigning ? "Assigning…" : "Assign to me"}
          </button>
        )}

        {canManage && !confirmingDelete && (
          <>
            <Link to={`/tasks/${task.id}/edit`} className={actionClass}>
              Edit
            </Link>
            <button type="button" onClick={() => setConfirmingDelete(true)} className={actionClass}>
              Delete
            </button>
          </>
        )}

        {canManage && confirmingDelete && (
          <>
            <span className="text-ink-300">Delete this task?</span>
            <button
              type="button"
              onClick={handleDelete}
              disabled={deleting}
              className="rounded-md font-semibold text-rose-300 transition hover:text-rose-200 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-accent disabled:opacity-50"
            >
              {deleting ? "Deleting…" : "Yes, delete"}
            </button>
            <button
              type="button"
              onClick={() => setConfirmingDelete(false)}
              disabled={deleting}
              className={actionClass}
            >
              Cancel
            </button>
          </>
        )}
      </div>

      {error && <p className="text-xs font-medium text-rose-300">{error}</p>}
    </div>
  );
}
