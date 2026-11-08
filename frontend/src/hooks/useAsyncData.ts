import { useEffect, useState } from "react";
import { parseApiError } from "../utils/apiError";

export function useAsyncData<T>(fetcher: () => Promise<T>) {
  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [reloadKey, setReloadKey] = useState(0);

  useEffect(() => {
    let cancelled = false;

    fetcher()
      .then((result) => {
        if (!cancelled) setData(result);
      })
      .catch((err: unknown) => {
        if (!cancelled) setError(parseApiError(err).message);
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, [fetcher, reloadKey]);

  const reload = () => {
    setLoading(true);
    setError("");
    setReloadKey((key) => key + 1);
  };

  return { data, setData, loading, error, reload };
}
