import type { User } from "../types/auth";
import type { Task } from "../types/task";

// Assumed rule: the creator or an admin can edit and delete a task.
// The server must enforce the same rule; this only decides what the UI shows.
export function canManageTask(task: Task, user: User | null): boolean {
  if (!user) return false;
  return user.role === "ADMIN" || task.createdBy === user.id;
}
