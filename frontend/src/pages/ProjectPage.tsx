import { useCallback, useMemo, useState, type FormEvent } from "react";
import { Link, useParams } from "react-router-dom";
import * as projectApi from "../api/projectApi";
import * as taskApi from "../api/taskApi";
import PageHeader from "../components/layout/PageHeader";
import BackLink from "../components/ui/BackLink";
import EmptyState from "../components/ui/EmptyState";
import ErrorState from "../components/ui/ErrorState";
import LinkButton from "../components/ui/LinkButton";
import Spinner from "../components/ui/Spinner";
import { useAsyncData } from "../hooks/useAsyncData";
import type { TaskPriority, TaskStatus } from "../types/task";
import { formatDate, isOverdue } from "../utils/date";
import { PRIORITY_LABELS, STATUS_LABELS } from "../utils/taskMeta";

const STATUS_DOT: Record<TaskStatus, string> = {
  TODO: "bg-ink-400",
  IN_PROGRESS: "bg-sky-400",
  DONE: "bg-emerald-400",
};

const PRIORITY_DOT: Record<TaskPriority, string> = {
  LOW: "bg-ink-400",
  MEDIUM: "bg-amber-400",
  HIGH: "bg-rose-400",
};

const PRIORITY_CHIP: Record<TaskPriority, string> = {
  LOW: "border-white/15 bg-white/[0.06] text-ink-200",
  MEDIUM: "border-amber-400/25 bg-amber-400/10 text-amber-200",
  HIGH: "border-rose-400/25 bg-rose-400/10 text-rose-200",
};

function ProjectView({ projectId }: { projectId: number }) {
  const fetchProject = useCallback(() => projectApi.getProject(projectId), [projectId]);
  const fetchTasks = useCallback(() => projectApi.getProjectTasks(projectId), [projectId]);
  const projectState = useAsyncData(fetchProject);
  const tasksState = useAsyncData(fetchTasks);
  const tasks = useMemo(() => tasksState.data ?? [], [tasksState.data]);
  const [dragged, setDragged] = useState<number | null>(null);
  const [customColumns, setCustomColumns] = useState<string[]>(() =>
    JSON.parse(localStorage.getItem(`taskforge-columns-${projectId}`) ?? "[]")
  );
  const [newColumn, setNewColumn] = useState("");

  const columns: Array<{ id: string; label: string; status?: TaskStatus }> = [
    { id: "TODO", label: STATUS_LABELS.TODO, status: "TODO" },
    { id: "IN_PROGRESS", label: STATUS_LABELS.IN_PROGRESS, status: "IN_PROGRESS" },
    { id: "DONE", label: STATUS_LABELS.DONE, status: "DONE" },
    ...customColumns.map((label) => ({ id: label, label })),
  ];

  const moveTask = async (status: TaskStatus) => {
    if (dragged === null) return;
    const task = tasks.find((item) => item.id === dragged);
    if (!task || task.status === status) return;
    tasksState.setData((prev) =>
      (prev ?? []).map((item) => (item.id === task.id ? { ...item, status } : item))
    );
    try {
      await taskApi.updateTask(task.id, {
        title: task.title,
        description: task.description ?? "",
        priority: task.priority,
        status,
        dueDate: task.dueDate,
      });
    } catch {
      tasksState.reload();
    }
    setDragged(null);
  };

  const addColumn = (event: FormEvent) => {
    event.preventDefault();
    const label = newColumn.trim();
    if (!label || customColumns.includes(label)) return;
    const next = [...customColumns, label];
    setCustomColumns(next);
    localStorage.setItem(`taskforge-columns-${projectId}`, JSON.stringify(next));
    setNewColumn("");
  };

  if (projectState.error) {
    return (
      <div>
        <div className="mb-6">
          <BackLink to="/projects">Projects</BackLink>
        </div>
        <ErrorState message={projectState.error} onRetry={projectState.reload} />
      </div>
    );
  }

  return (
    <div>
      <div className="mb-6">
        <BackLink to="/projects">Projects</BackLink>
      </div>
      <PageHeader
        title={projectState.data?.name ?? "Project"}
        action={<LinkButton to={`/tasks/new?project=${projectId}`}>New task</LinkButton>}
      />

      <div className="mb-5 flex flex-col gap-3 rounded-2xl border border-white/10 bg-ink-900/60 p-3 sm:flex-row sm:items-center sm:justify-between sm:px-4">
        <p className="px-1 text-sm text-ink-400">
          Drag tasks between columns to update their status.
        </p>
        <form onSubmit={addColumn} className="flex gap-2">
          <input
            value={newColumn}
            onChange={(event) => setNewColumn(event.target.value)}
            placeholder="Custom column"
            aria-label="Custom column name"
            className="w-full min-w-0 rounded-lg border border-white/15 bg-ink-800 px-3 py-2 text-xs text-ink-100 outline-none transition placeholder:text-ink-400 focus:border-accent/80 focus:ring-2 focus:ring-accent/25 sm:w-40"
          />
          <button
            type="submit"
            className="shrink-0 rounded-lg border border-white/15 bg-white/5 px-3 py-2 text-xs font-medium text-ink-200 transition hover:border-accent/40 hover:text-accent focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-accent"
          >
            + Add column
          </button>
        </form>
      </div>

      {tasksState.loading ? (
        <Spinner />
      ) : tasksState.error ? (
        <ErrorState message={tasksState.error} onRetry={tasksState.reload} />
      ) : (
        <div className="grid gap-4 xl:grid-cols-3">
          {columns.map((column) => {
            const columnTasks = column.status
              ? tasks.filter((task) => task.status === column.status)
              : [];
            const dot = column.status ? STATUS_DOT[column.status] : "bg-ink-500";

            return (
              <section
                key={column.id}
                onDragOver={(event) => event.preventDefault()}
                onDrop={() => column.status && moveTask(column.status)}
                className="min-h-[380px] rounded-2xl border border-white/10 bg-ink-900/60 p-3"
              >
                <div className="mb-3 flex items-center justify-between gap-2 px-2 pt-1">
                  <h2 className="flex items-center gap-2 text-sm font-semibold text-white">
                    <span aria-hidden="true" className={`size-2 rounded-full ${dot}`} />
                    {column.label}
                  </h2>
                  <span className="rounded-full border border-white/10 bg-white/5 px-2 py-0.5 text-xs font-medium text-ink-300">
                    {columnTasks.length}
                  </span>
                </div>

                <div className="space-y-3">
                  {columnTasks.map((task) => (
                    <Link
                      key={task.id}
                      to={`/tasks/${task.id}`}
                      draggable
                      onDragStart={() => setDragged(task.id)}
                      className="group block cursor-grab rounded-xl border border-white/10 bg-ink-800 p-4 shadow-lg shadow-black/20 transition hover:-translate-y-0.5 hover:border-accent/35 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-accent active:cursor-grabbing"
                    >
                      <div className="mb-2.5 flex items-start justify-between gap-3">
                        <p className="text-sm font-medium leading-5 text-white">{task.title}</p>
                        <span
                          title={`Priority: ${PRIORITY_LABELS[task.priority]}`}
                          className={`mt-1 size-2 shrink-0 rounded-full ${PRIORITY_DOT[task.priority]}`}
                        />
                      </div>
                      {task.description && (
                        <p className="mb-3 line-clamp-2 text-xs leading-5 text-ink-400">
                          {task.description}
                        </p>
                      )}
                      <div className="flex items-center justify-between gap-2 text-xs">
                        <span className={isOverdue(task) ? "font-medium text-rose-300" : "text-ink-400"}>
                          {isOverdue(task) && <span className="sr-only">Overdue: </span>}
                          {task.dueDate ? formatDate(task.dueDate) : "No due date"}
                        </span>
                        <span
                          className={`rounded-md border px-2 py-1 text-[11px] font-medium ${PRIORITY_CHIP[task.priority]}`}
                        >
                          {PRIORITY_LABELS[task.priority]}
                        </span>
                      </div>
                    </Link>
                  ))}
                </div>

                {columnTasks.length === 0 && (
                  <div className="mt-3 rounded-xl border border-dashed border-white/15 py-10 text-center text-xs text-ink-400">
                    {column.status ? "Drop tasks here" : "Custom column — not linked to a status"}
                  </div>
                )}
              </section>
            );
          })}
        </div>
      )}
    </div>
  );
}

export default function ProjectPage() {
  const { projectId } = useParams();
  const id = Number(projectId);

  if (!Number.isInteger(id) || id <= 0) {
    return (
      <EmptyState
        title="Project not found"
        description="This link does not point to a project."
        action={<BackLink to="/projects">Projects</BackLink>}
      />
    );
  }

  return <ProjectView key={id} projectId={id} />;
}
