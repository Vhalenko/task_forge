import { useMemo, useState } from "react";
import type { Task } from "../../types/task";
import {
  applyTaskFilters,
  DEFAULT_FILTERS,
  hasActiveFilters,
  type TaskFilters,
} from "../../utils/taskFilters";
import EmptyState from "../ui/EmptyState";
import ErrorState from "../ui/ErrorState";
import Spinner from "../ui/Spinner";
import TaskList from "./TaskList";
import TaskToolbar from "./TaskToolbar";

interface TaskBrowserProps {
  tasks: Task[];
  loading: boolean;
  error: string;
  onRetry: () => void;
  emptyTitle: string;
  emptyDescription: string;
}

export default function TaskBrowser({
  tasks,
  loading,
  error,
  onRetry,
  emptyTitle,
  emptyDescription,
}: TaskBrowserProps) {
  const [filters, setFilters] = useState<TaskFilters>(DEFAULT_FILTERS);
  const visible = useMemo(() => applyTaskFilters(tasks, filters), [tasks, filters]);
  const filtering = hasActiveFilters(filters);

  function renderContent() {
    if (loading) return <Spinner />;
    if (error) return <ErrorState message={error} onRetry={onRetry} />;

    if (tasks.length === 0) {
      return <EmptyState title={emptyTitle} description={emptyDescription} />;
    }

    if (visible.length === 0) {
      return (
        <EmptyState
          title="No matching tasks"
          description="Try a different search or clear the filters."
          action={
            <button
              type="button"
              onClick={() => setFilters(DEFAULT_FILTERS)}
              className="rounded-md text-sm font-semibold text-accent underline underline-offset-4 transition hover:text-accent-strong focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-accent"
            >
              Clear filters
            </button>
          }
        />
      );
    }

    return <TaskList tasks={visible} />;
  }

  return (
    <div>
      <TaskToolbar filters={filters} onChange={setFilters} />

      {!loading && !error && tasks.length > 0 && (
        <div className="mt-4 flex flex-wrap items-center gap-3 px-1 text-sm text-ink-400">
          <p>
            {filtering
              ? `${visible.length} of ${tasks.length} tasks`
              : `${tasks.length} ${tasks.length === 1 ? "task" : "tasks"}`}
          </p>
          {filtering && (
            <>
              <span aria-hidden="true" className="h-3 w-px bg-white/15" />
              <button
                type="button"
                onClick={() => setFilters(DEFAULT_FILTERS)}
                className="rounded-md text-xs font-medium text-accent underline underline-offset-4 transition hover:text-accent-strong focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-accent"
              >
                Clear filters
              </button>
            </>
          )}
        </div>
      )}

      <div className="mt-4">{renderContent()}</div>
    </div>
  );
}
