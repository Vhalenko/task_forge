import api from "./client";
import type { User } from "../types/auth";
import type { Project, ProjectRequest } from "../types/project";
import type { Team, TeamRequest } from "../types/team";

export const getTeams = () => api.get<Team[]>("/teams").then((res) => res.data);

export const getTeam = (id: number) => api.get<Team>(`/teams/${id}`).then((res) => res.data);

export const createTeam = (payload: TeamRequest) =>
  api.post<Team>("/teams", payload).then((res) => res.data);

export const deleteTeam = (id: number) => api.delete(`/teams/${id}`).then(() => undefined);

export const getTeamUsers = (id: number) =>
  api.get<User[]>(`/teams/${id}/users`).then((res) => res.data);

export const getTeamProjects = (id: number) =>
  api.get<Project[]>(`/teams/${id}/projects`).then((res) => res.data);

export const createProject = (teamId: number, payload: ProjectRequest) =>
  api.post<Project>(`/teams/${teamId}/projects`, payload).then((res) => res.data);

export const inviteUser = (teamId: number, email: string) =>
  api.post<User>(`/teams/${teamId}/invitations`, { email }).then((res) => res.data);
