import type { ReactNode } from "react";

interface EmptyStateProps {
  title: string;
  description?: string;
  action?: ReactNode;
}

export default function EmptyState({ title, description, action }: EmptyStateProps) {
  return (
    <div className="rounded-2xl border border-dashed border-white/15 bg-ink-900/40 px-6 py-14 text-center">
      <span
        aria-hidden="true"
        className="mx-auto mb-4 grid size-10 place-items-center rounded-xl border border-white/10 bg-white/5 text-base text-accent"
      >
        ✦
      </span>
      <p className="text-sm font-semibold text-white">{title}</p>
      {description && <p className="mx-auto mt-1.5 max-w-sm text-sm leading-6 text-ink-400">{description}</p>}
      {action && <div className="mt-5 flex justify-center">{action}</div>}
    </div>
  );
}
