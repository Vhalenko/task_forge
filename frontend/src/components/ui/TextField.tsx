import type { ComponentPropsWithoutRef } from "react";
import { errorClass, fieldClass, labelClass } from "./fieldStyles";

interface TextFieldProps extends ComponentPropsWithoutRef<"input"> {
  id: string;
  label: string;
  error?: string;
}

export default function TextField({ id, label, error, className = "", ...props }: TextFieldProps) {
  return (
    <div>
      <label htmlFor={id} className={labelClass}>
        {label}
      </label>
      <input
        id={id}
        aria-invalid={error ? true : undefined}
        aria-describedby={error ? `${id}-error` : undefined}
        className={fieldClass(Boolean(error), className)}
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
