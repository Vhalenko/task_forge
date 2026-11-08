import type { ComponentPropsWithoutRef } from "react";
import { errorClass, fieldClass, labelClass } from "./fieldStyles";

interface SelectProps extends ComponentPropsWithoutRef<"select"> {
  label: string;
  showLabel?: boolean;
  error?: string;
  wrapperClassName?: string;
}

export default function Select({
  label,
  showLabel = false,
  error,
  wrapperClassName = "",
  className = "",
  children,
  ...props
}: SelectProps) {
  return (
    <label className={`block ${wrapperClassName}`}>
      <span className={showLabel ? labelClass : "sr-only"}>{label}</span>
      <span className="relative block">
        <select
          aria-invalid={error ? true : undefined}
          className={fieldClass(Boolean(error), `appearance-none pr-10! ${className}`)}
          {...props}
        >
          {children}
        </select>
        <svg
          aria-hidden="true"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          strokeWidth={1.8}
          strokeLinecap="round"
          strokeLinejoin="round"
          className="pointer-events-none absolute right-3.5 top-1/2 size-4 -translate-y-1/2 text-ink-400"
        >
          <path d="m6 9 6 6 6-6" />
        </svg>
      </span>
      {error && <span className={`block ${errorClass}`}>{error}</span>}
    </label>
  );
}
