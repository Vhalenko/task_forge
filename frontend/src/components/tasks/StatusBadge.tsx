import type { TaskStatus } from "../../types/task";
import { STATUS_LABELS } from "../../utils/taskMeta";

const STYLES: Record<TaskStatus, { chip: string; dot: string }> = {
  TODO: { chip: "border-white/15 bg-white/[0.06] text-ink-200", dot: "bg-ink-400" },
  IN_PROGRESS: { chip: "border-sky-400/25 bg-sky-400/10 text-sky-200", dot: "bg-sky-400" },
  DONE: { chip: "border-emerald-400/25 bg-emerald-400/10 text-emerald-200", dot: "bg-emerald-400" },
};

export default function StatusBadge({ status }: { status: TaskStatus }) {
  const style = STYLES[status];

  return (
    <span
      className={`inline-flex items-center gap-1.5 whitespace-nowrap rounded-full border px-2.5 py-1 text-xs font-medium ${style.chip}`}
    >
      <span aria-hidden="true" className={`size-1.5 rounded-full ${style.dot}`} />
      {STATUS_LABELS[status]}
    </span>
  );
}
