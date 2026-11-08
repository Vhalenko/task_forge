import api from "./client";
import type { CommentRequest, TaskComment } from "../types/comment";

export const getTaskComments = (taskId: number) =>
  api.get<TaskComment[]>(`/tasks/${taskId}/comments`).then((res) => res.data);

export const createComment = (taskId: number, payload: CommentRequest) =>
  api.post<TaskComment>(`/tasks/${taskId}/comments`, payload).then((res) => res.data);

export const updateComment = (id: number, payload: CommentRequest) =>
  api.put<TaskComment>(`/comments/${id}`, payload).then((res) => res.data);

export const deleteComment = (id: number) =>
  api.delete(`/comments/${id}`).then(() => undefined);
