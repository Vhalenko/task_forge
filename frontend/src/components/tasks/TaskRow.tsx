import { Link } from "react-router-dom";
import type { Task } from "../../types/task";
import { formatDate, isOverdue } from "../../utils/date";
import PriorityBadge from "./PriorityBadge";
import StatusBadge from "./StatusBadge";

export default function TaskRow({ task }: { task: Task }) {
  const overdue = isOverdue(task);
  const done = task.status === "DONE";

  return (
    <li>
      <Link
        to={`/tasks/${task.id}`}
        className="group relative flex flex-col gap-3 overflow-hidden rounded-2xl border border-white/10 bg-ink-900/80 px-4 py-4 transition hover:border-accent/35 hover:bg-ink-850 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-accent sm:flex-row sm:items-center sm:justify-between sm:gap-6 sm:px-5"
      >
        <span
          aria-hidden="true"
          className="absolute inset-y-4 left-0 w-0.5 rounded-full bg-accent opacity-0 transition group-hover:opacity-100"
        />
        <div className="min-w-0 pl-1">
          <p className={`truncate text-sm font-medium ${done ? "text-ink-400 line-through" : "text-white"}`}>
            {task.title}
          </p>
          {task.description && <p className="mt-1 truncate text-sm text-ink-400">{task.description}</p>}
        </div>

        <div className="flex flex-wrap items-center gap-x-4 gap-y-2 sm:shrink-0">
          <PriorityBadge priority={task.priority} />
          <StatusBadge status={task.status} />
          <span
            className={`inline-flex w-24 items-center justify-end gap-1.5 whitespace-nowrap text-xs ${
              overdue ? "font-medium text-rose-300" : "text-ink-400"
            }`}
          >
            {overdue && (
              <>
                <span className="sr-only">Overdue: </span>
                <span aria-hidden="true" className="size-1.5 rounded-full bg-rose-400" />
              </>
            )}
            {task.dueDate ? formatDate(task.dueDate) : "No due date"}
          </span>
        </div>
      </Link>
    </li>
  );
}
