import type { User } from "./auth";

export type TaskStatus = "TODO" | "IN_PROGRESS" | "DONE";
export type TaskPriority = "LOW" | "MEDIUM" | "HIGH";

export type Assignee = Pick<User, "id" | "firstName" | "lastName">;

export interface Task {
  id: number;
  title: string;
  description: string | null;
  priority: TaskPriority;
  status: TaskStatus;
  dueDate: string | null;
  createdAt: string;
  completedAt: string | null;
  createdBy: number;
  createdByName?: string; // optional until the backend sends it
  assignees: Assignee[];
  projectId: number;
  projectName: string;
}

export interface CreateTaskRequest {
  title: string;
  description: string;
  priority: TaskPriority;
  dueDate: string | null;
}

export interface UpdateTaskRequest {
  title: string;
  description: string;
  priority: TaskPriority;
  status: TaskStatus;
  dueDate: string | null;
}
