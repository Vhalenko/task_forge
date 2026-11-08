import type { TaskPriority, TaskStatus } from "../types/task";

export const STATUSES: TaskStatus[] = ["TODO", "IN_PROGRESS", "DONE"];
export const PRIORITIES: TaskPriority[] = ["LOW", "MEDIUM", "HIGH"];

export const STATUS_LABELS: Record<TaskStatus, string> = {
  TODO: "To do",
  IN_PROGRESS: "In progress",
  DONE: "Done",
};

export const PRIORITY_LABELS: Record<TaskPriority, string> = {
  LOW: "Low",
  MEDIUM: "Medium",
  HIGH: "High",
};

export const PRIORITY_RANK: Record<TaskPriority, number> = {
  LOW: 0,
  MEDIUM: 1,
  HIGH: 2,
};
