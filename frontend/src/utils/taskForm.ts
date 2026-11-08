import type {
  CreateTaskRequest,
  Task,
  TaskPriority,
  TaskStatus,
  UpdateTaskRequest,
} from "../types/task";
import type { FieldErrors } from "./apiError";
import { toDateInputValue, toDueDateIso } from "./date";

export interface TaskFormValues {
  title: string;
  description: string;
  priority: TaskPriority;
  status: TaskStatus;
  dueDate: string; // yyyy-MM-dd, or "" for none
  projectId: string; // "" until chosen
}

export const EMPTY_TASK_FORM: TaskFormValues = {
  title: "",
  description: "",
  priority: "MEDIUM",
  status: "TODO",
  dueDate: "",
  projectId: "",
};

export function taskToFormValues(task: Task): TaskFormValues {
  return {
    title: task.title,
    description: task.description ?? "",
    priority: task.priority,
    status: task.status,
    dueDate: toDateInputValue(task.dueDate),
    projectId: String(task.projectId),
  };
}

export function validateTask(values: TaskFormValues, mode: "create" | "edit"): FieldErrors {
  const errors: FieldErrors = {};
  const title = values.title.trim();

  if (!title) errors.title = "Title is required";
  else if (title.length > 200) errors.title = "Title must be at most 200 characters";

  if (values.description.length > 5000) {
    errors.description = "Description must be at most 5000 characters";
  }
  if (mode === "create" && !values.projectId) errors.projectId = "Choose a project";

  return errors;
}

export function toCreateRequest(values: TaskFormValues): CreateTaskRequest {
  return {
    title: values.title.trim(),
    description: values.description.trim(),
    priority: values.priority,
    dueDate: toDueDateIso(values.dueDate),
  };
}

export function toUpdateRequest(values: TaskFormValues): UpdateTaskRequest {
  return {
    title: values.title.trim(),
    description: values.description.trim(),
    priority: values.priority,
    status: values.status,
    dueDate: toDueDateIso(values.dueDate),
  };
}
