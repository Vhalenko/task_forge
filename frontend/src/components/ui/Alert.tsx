import type { ReactNode } from "react";

type AlertVariant = "error" | "success" | "info";

interface AlertProps {
  variant?: AlertVariant;
  children?: ReactNode;
}

const STYLES: Record<AlertVariant, string> = {
  error: "border-rose-400/25 bg-rose-400/10 text-rose-200",
  success: "border-accent/25 bg-accent/10 text-accent",
  info: "border-sky-400/25 bg-sky-400/10 text-sky-200",
};

export default function Alert({ variant = "error", children }: AlertProps) {
  if (!children) return null;
  return (
    <div
      role="alert"
      className={`flex items-start gap-2.5 rounded-xl border px-3.5 py-2.5 text-sm leading-5 ${STYLES[variant]}`}
    >
      <span aria-hidden="true" className="mt-1.5 size-1.5 shrink-0 rounded-full bg-current" />
      <span>{children}</span>
    </div>
  );
}
