import type { ReactNode } from "react";
import { Link } from "react-router-dom";

type LinkButtonVariant = "primary" | "secondary";
type LinkButtonSize = "md" | "lg";

interface LinkButtonProps {
  to: string;
  children: ReactNode;
  variant?: LinkButtonVariant;
  size?: LinkButtonSize;
  className?: string;
}

const VARIANTS: Record<LinkButtonVariant, string> = {
  primary:
    "bg-accent text-ink-950 shadow-[0_6px_20px_-12px_rgb(158_234_99/0.6)] hover:bg-accent-strong",
  secondary:
    "border border-white/15 bg-white/5 text-ink-100 hover:border-white/25 hover:bg-white/10",
};

const SIZES: Record<LinkButtonSize, string> = {
  md: "px-4 py-2.5 text-sm",
  lg: "px-5 py-3 text-[15px]",
};

export default function LinkButton({
  to,
  children,
  variant = "primary",
  size = "md",
  className = "",
}: LinkButtonProps) {
  return (
    <Link
      to={to}
      className={`inline-flex shrink-0 items-center justify-center gap-2 rounded-xl font-semibold transition focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-accent active:translate-y-px ${VARIANTS[variant]} ${SIZES[size]} ${className}`}
    >
      {children}
    </Link>
  );
}
