import { useMemo } from "react";
import { getMyTasks } from "../api/taskApi";
import { useAsyncData } from "./useAsyncData";

export function useMyTasks() {
  const { data, loading, error, reload } = useAsyncData(getMyTasks);
  const tasks = useMemo(() => data ?? [], [data]);
  return { tasks, loading, error, reload };
}
