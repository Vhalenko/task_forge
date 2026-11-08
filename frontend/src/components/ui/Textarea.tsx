import type { ComponentPropsWithoutRef } from "react";
import { errorClass, fieldClass, labelClass } from "./fieldStyles";

interface TextareaProps extends ComponentPropsWithoutRef<"textarea"> {
  id: string;
  label: string;
  error?: string;
}

export default function Textarea({ id, label, error, className = "", ...props }: TextareaProps) {
  return (
    <div>
      <label htmlFor={id} className={labelClass}>
        {label}
      </label>
      <textarea
        id={id}
        aria-invalid={error ? true : undefined}
        aria-describedby={error ? `${id}-error` : undefined}
        className={fieldClass(Boolean(error), `min-h-28 resize-y ${className}`)}
        {...props}
      />
      {error && (
        <p id={`${id}-error`} className={errorClass}>
          {error}
        </p>
      )}
    </div>
  );
}
