import { useState, type FormEvent } from "react";
import { parseApiError } from "../../utils/apiError";
import Button from "../ui/Button";
import { errorClass, fieldClass } from "../ui/fieldStyles";

interface NameFormProps {
  label: string;
  placeholder: string;
  submitLabel: string;
  onSubmit: (name: string) => Promise<void>;
}

export default function NameForm({ label, placeholder, submitLabel, onSubmit }: NameFormProps) {
  const [name, setName] = useState("");
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const trimmed = name.trim();

    if (!trimmed) {
      setError("Name is required");
      return;
    }
    if (trimmed.length > 100) {
      setError("Name must be at most 100 characters");
      return;
    }

    setSubmitting(true);
    setError("");
    try {
      await onSubmit(trimmed);
      setName("");
    } catch (err) {
      const { message, fieldErrors } = parseApiError(err);
      setError(fieldErrors.name ?? message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} noValidate>
      <div className="flex flex-col gap-3 sm:flex-row">
        <input
          aria-label={label}
          placeholder={placeholder}
          value={name}
          onChange={(e) => {
            setName(e.target.value);
            setError("");
          }}
          className={fieldClass(Boolean(error), "min-w-0 flex-1")}
        />
        <Button type="submit" loading={submitting} fullWidth={false} className="shrink-0">
          {submitLabel}
        </Button>
      </div>
      {error && <p className={errorClass}>{error}</p>}
    </form>
  );
}
