import type { ReactNode } from "react";
import { Link } from "react-router-dom";

export default function BackLink({ to, children }: { to: string; children: ReactNode }) {
  return (
    <Link
      to={to}
      className="inline-flex items-center gap-1.5 rounded-md text-sm font-medium text-ink-400 transition hover:text-white focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-accent"
    >
      <svg
        aria-hidden="true"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        strokeWidth={1.8}
        strokeLinecap="round"
        strokeLinejoin="round"
        className="size-4"
      >
        <path d="M15 18l-6-6 6-6" />
      </svg>
      {children}
    </Link>
  );
}
