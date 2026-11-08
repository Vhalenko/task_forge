import type { Task, TaskPriority, TaskStatus } from "../types/task";
import { PRIORITY_RANK } from "./taskMeta";

export type SortOption = "dueDate" | "priority" | "newest" | "title";

export interface TaskFilters {
  search: string;
  status: TaskStatus | "ALL";
  priority: TaskPriority | "ALL";
  sort: SortOption;
}

export const DEFAULT_FILTERS: TaskFilters = {
  search: "",
  status: "ALL",
  priority: "ALL",
  sort: "dueDate",
};

export const SORT_LABELS: Record<SortOption, string> = {
  dueDate: "Due date",
  priority: "Priority",
  newest: "Newest first",
  title: "Title A–Z",
};

export function hasActiveFilters(f: TaskFilters): boolean {
  return f.search.trim() !== "" || f.status !== "ALL" || f.priority !== "ALL";
}

// tasks without a due date go last
function compareDueDate(a: Task, b: Task): number {
  if (!a.dueDate && !b.dueDate) return 0;
  if (!a.dueDate) return 1;
  if (!b.dueDate) return -1;
  return Date.parse(a.dueDate) - Date.parse(b.dueDate);
}

const COMPARATORS: Record<SortOption, (a: Task, b: Task) => number> = {
  dueDate: compareDueDate,
  priority: (a, b) => PRIORITY_RANK[b.priority] - PRIORITY_RANK[a.priority],
  newest: (a, b) => Date.parse(b.createdAt) - Date.parse(a.createdAt),
  title: (a, b) => a.title.localeCompare(b.title),
};

export function applyTaskFilters(tasks: Task[], filters: TaskFilters): Task[] {
  const query = filters.search.trim().toLowerCase();

  return tasks
    .filter((task) => {
      if (filters.status !== "ALL" && task.status !== filters.status) return false;
      if (filters.priority !== "ALL" && task.priority !== filters.priority) return false;
      if (query) {
        const haystack = `${task.title} ${task.description ?? ""}`.toLowerCase();
        if (!haystack.includes(query)) return false;
      }
      return true;
    })
    .sort(COMPARATORS[filters.sort]);
}
