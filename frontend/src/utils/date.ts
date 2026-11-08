import type { Task } from "../types/task";

export function formatDate(iso: string): string {
  return new Intl.DateTimeFormat(undefined, {
    day: "numeric",
    month: "short",
    year: "numeric",
  }).format(new Date(iso));
}

export function isOverdue(task: Pick<Task, "dueDate" | "status">): boolean {
  if (!task.dueDate || task.status === "DONE") return false;
  return Date.parse(task.dueDate) < Date.now();
}

export function formatDateTime(iso: string): string {
  return new Intl.DateTimeFormat(undefined, {
    dateStyle: "medium",
    timeStyle: "short",
  }).format(new Date(iso));
}

// "2026-10-30" (date input) -> "2026-10-30T23:59:59" (what LocalDateTime accepts)
export function toDueDateIso(dateInput: string): string | null {
  return dateInput ? `${dateInput}T23:59:59` : null;
}

// "2026-10-30T23:59:59" -> "2026-10-30" (what a date input needs)
export function toDateInputValue(iso: string | null): string {
  return iso ? iso.slice(0, 10) : "";
}
