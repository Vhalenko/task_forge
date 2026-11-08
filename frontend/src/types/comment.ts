export interface TaskComment {
  id: number;
  text: string;
  createdAt: string;
  createdById: number;
  createdByName?: string; // optional until the backend sends it
}

export interface CommentRequest {
  text: string;
}
