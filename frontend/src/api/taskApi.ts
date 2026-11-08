import api from "./client";
import type { Task, UpdateTaskRequest } from "../types/task";

export const getMyTasks = () =>
  api.get<Task[]>("/users/me/tasks").then((res) => res.data);

export const getTask = (id: number) =>
  api.get<Task>(`/tasks/${id}`).then((res) => res.data);

export const updateTask = (id: number, payload: UpdateTaskRequest) =>
  api.put<Task>(`/tasks/${id}`, payload).then((res) => res.data);

export const deleteTask = (id: number) =>
  api.delete(`/tasks/${id}`).then(() => undefined);

export const assignUser = (taskId: number, userId: number) =>
  api.patch(`/tasks/${taskId}/assignee/${userId}`).then(() => undefined);
