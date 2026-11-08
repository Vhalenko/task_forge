import type { ReactNode } from "react";
import { Link } from "react-router-dom";

interface AuthLayoutProps {
  title: string;
  subtitle?: string;
  children: ReactNode;
  footer?: ReactNode;
}

export default function AuthLayout({ title, subtitle, children, footer }: AuthLayoutProps) {
  return (
    <div className="relative flex min-h-screen items-center justify-center overflow-hidden px-5 py-12">
      <div
        aria-hidden="true"
        className="pointer-events-none absolute -left-40 -top-40 size-[30rem] rounded-full bg-accent/10 blur-3xl"
      />
      <div
        aria-hidden="true"
        className="pointer-events-none absolute -bottom-48 -right-24 size-[26rem] rounded-full bg-sky-400/[0.06] blur-3xl"
      />

      <div className="relative w-full max-w-md">
        <Link
          to="/"
          className="mb-6 inline-flex items-center gap-2.5 rounded-lg focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-accent"
        >
          <span className="grid size-8 place-items-center rounded-lg bg-accent text-sm font-bold text-ink-950">
            ✦
          </span>
          <span className="text-base font-semibold tracking-tight text-white">TaskForge</span>
        </Link>

        <div className="rounded-3xl border border-white/10 bg-ink-900/80 p-6 shadow-[0_40px_120px_-30px_rgb(0_0_0/0.9)] ring-1 ring-white/5 backdrop-blur-xl sm:p-9">
          <h1 className="text-2xl font-semibold tracking-tight text-white sm:text-[1.75rem]">{title}</h1>
          {subtitle && <p className="mt-2 text-sm leading-6 text-ink-400">{subtitle}</p>}
          <div className="mt-8">{children}</div>
          {footer && <p className="mt-8 border-t border-white/10 pt-6 text-sm text-ink-400">{footer}</p>}
        </div>
      </div>
    </div>
  );
}
