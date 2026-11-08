import api from "./client";
import type { Project } from "../types/project";
import type { CreateTaskRequest, Task } from "../types/task";

export const getProject = (id: number) =>
  api.get<Project>(`/projects/${id}`).then((res) => res.data);

export const deleteProject = (id: number) =>
  api.delete(`/projects/${id}`).then(() => undefined);

export const getProjectTasks = (projectId: number) =>
  api.get<Task[]>(`/projects/${projectId}/tasks`).then((res) => res.data);

export const createTask = (projectId: number, payload: CreateTaskRequest) =>
  api.post<Task>(`/projects/${projectId}/tasks`, payload).then((res) => res.data);
