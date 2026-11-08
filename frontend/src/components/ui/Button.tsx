import type { ComponentPropsWithoutRef } from "react";

type ButtonVariant = "primary" | "secondary" | "ghost" | "danger";

interface ButtonProps extends ComponentPropsWithoutRef<"button"> {
  loading?: boolean;
  fullWidth?: boolean;
  variant?: ButtonVariant;
}

const VARIANTS: Record<ButtonVariant, string> = {
  primary:
    "bg-accent text-ink-950 shadow-[0_6px_20px_-12px_rgb(158_234_99/0.6)] hover:bg-accent-strong active:translate-y-px",
  secondary:
    "border border-white/15 bg-white/5 text-ink-200 hover:border-white/25 hover:bg-white/10 hover:text-white active:translate-y-px",
  ghost: "text-ink-300 hover:bg-white/5 hover:text-white",
  danger:
    "border border-rose-400/30 bg-rose-400/10 text-rose-200 hover:border-rose-400/50 hover:bg-rose-400/15",
};

export default function Button({
  children,
  loading = false,
  disabled = false,
  fullWidth = true,
  variant = "primary",
  className = "",
  ...props
}: ButtonProps) {
  return (
    <button
      disabled={loading || disabled}
      className={`inline-flex items-center justify-center gap-2 rounded-xl px-4 py-2.5 text-sm font-semibold transition focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-accent disabled:cursor-not-allowed disabled:opacity-50 ${VARIANTS[variant]} ${
        fullWidth ? "w-full" : ""
      } ${className}`}
      {...props}
    >
      {loading && (
        <span
          aria-hidden="true"
          className="size-3.5 animate-spin rounded-full border-2 border-current border-t-transparent"
        />
      )}
      {loading ? "Please wait…" : children}
    </button>
  );
}
