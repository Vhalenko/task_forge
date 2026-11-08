import type { TaskPriority } from "../../types/task";
import { PRIORITY_LABELS } from "../../utils/taskMeta";

const STYLES: Record<TaskPriority, { text: string; dot: string }> = {
  LOW: { text: "text-ink-300", dot: "bg-ink-400" },
  MEDIUM: { text: "text-amber-200", dot: "bg-amber-400" },
  HIGH: { text: "text-rose-200", dot: "bg-rose-400" },
};

export default function PriorityBadge({ priority }: { priority: TaskPriority }) {
  const style = STYLES[priority];
  const label = PRIORITY_LABELS[priority];

  return (
    <span
      title={`Priority: ${label}`}
      className={`inline-flex items-center gap-1.5 whitespace-nowrap text-xs font-medium ${style.text}`}
    >
      <span aria-hidden="true" className={`size-1.5 rounded-full ${style.dot}`} />
      <span className="sr-only">Priority: </span>
      {label}
    </span>
  );
}
