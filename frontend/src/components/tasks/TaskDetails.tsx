import type { ReactNode } from "react";
import type { Task } from "../../types/task";
import { formatDate, isOverdue } from "../../utils/date";
import PriorityBadge from "./PriorityBadge";
import StatusBadge from "./StatusBadge";

function Fact({ label, children }: { label: string; children: ReactNode }) {
  return (
    <div>
      <dt className="text-[11px] font-semibold uppercase tracking-[0.14em] text-ink-400">{label}</dt>
      <dd className="mt-1.5 text-sm text-ink-100">{children}</dd>
    </div>
  );
}

export default function TaskDetails({ task, actions }: { task: Task; actions?: ReactNode }) {
  const overdue = isOverdue(task);

  return (
    <article className="overflow-hidden rounded-2xl border border-white/10 bg-ink-900/70 shadow-[0_24px_60px_-40px_rgb(0_0_0/0.9)]">
      <div className="border-b border-white/10 p-5 sm:p-7">
        <div className="flex flex-wrap items-start justify-between gap-4">
          <div className="flex flex-wrap items-center gap-2.5">
            <StatusBadge status={task.status} />
            <PriorityBadge priority={task.priority} />
          </div>
          {actions}
        </div>

        <h1 className="mt-4 text-2xl font-semibold leading-tight tracking-tight text-white sm:text-3xl">
          {task.title}
        </h1>

        {task.description ? (
          <p className="mt-4 whitespace-pre-wrap text-sm leading-7 text-ink-300">{task.description}</p>
        ) : (
          <p className="mt-4 text-sm italic text-ink-400">No description.</p>
        )}
      </div>

      <dl className="grid grid-cols-1 gap-x-10 gap-y-6 p-5 sm:grid-cols-2 sm:p-7">
        <Fact label="Project">{task.projectName}</Fact>
        <Fact label="Due date">
          <span className={overdue ? "font-medium text-rose-300" : undefined}>
            {task.dueDate ? formatDate(task.dueDate) : "No due date"}
          </span>
        </Fact>
        <Fact label="Created">{formatDate(task.createdAt)}</Fact>
        {task.completedAt && <Fact label="Completed">{formatDate(task.completedAt)}</Fact>}
        {task.createdByName && <Fact label="Created by">{task.createdByName}</Fact>}
        <Fact label="Assignees">
          {task.assignees.length === 0 ? (
            <span className="inline-flex items-center rounded-full border border-dashed border-white/15 px-3 py-1 text-xs text-ink-400">
              Unassigned
            </span>
          ) : (
            <ul className="flex flex-wrap gap-2">
              {task.assignees.map((assignee) => (
                <li
                  key={assignee.id}
                  className="rounded-full border border-white/10 bg-white/5 px-2.5 py-1 text-xs font-medium text-ink-200"
                >
                  {assignee.firstName} {assignee.lastName}
                </li>
              ))}
            </ul>
          )}
        </Fact>
      </dl>
    </article>
  );
}
