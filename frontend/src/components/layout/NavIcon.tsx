import type { ReactNode } from "react";

export type NavIconName = "tasks" | "projects" | "profile" | "admin";

const PATHS: Record<NavIconName, ReactNode> = {
  tasks: (
    <>
      <path d="M9.5 6h10M9.5 12h10M9.5 18h10" />
      <path d="m3.5 6 1.2 1.2L7 4.9" />
      <path d="m3.5 12 1.2 1.2L7 10.9" />
      <path d="m3.5 18 1.2 1.2L7 16.9" />
    </>
  ),
  projects: (
    <path d="M3.5 7.5A2.5 2.5 0 0 1 6 5h3l2 2.5h7A2.5 2.5 0 0 1 20.5 10v6.5A2.5 2.5 0 0 1 18 19H6a2.5 2.5 0 0 1-2.5-2.5z" />
  ),
  profile: (
    <>
      <circle cx="12" cy="8" r="3.5" />
      <path d="M5 20a7 7 0 0 1 14 0" />
    </>
  ),
  admin: (
    <>
      <path d="M12 3 5 6v5.4c0 4.2 2.9 7.7 7 9.6 4.1-1.9 7-5.4 7-9.6V6z" />
      <path d="m9.3 12 1.9 1.9 3.5-3.7" />
    </>
  ),
};

export default function NavIcon({ name }: { name: NavIconName }) {
  return (
    <svg
      aria-hidden="true"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth={1.7}
      strokeLinecap="round"
      strokeLinejoin="round"
      className="size-[18px] shrink-0"
    >
      {PATHS[name]}
    </svg>
  );
}
