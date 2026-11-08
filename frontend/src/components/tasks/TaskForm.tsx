import { useState, type ChangeEvent, type FormEvent } from "react";
import type { Project } from "../../types/project";
import { parseApiError, type FieldErrors } from "../../utils/apiError";
import { EMPTY_TASK_FORM, validateTask, type TaskFormValues } from "../../utils/taskForm";
import {
  PRIORITIES,
  PRIORITY_LABELS,
  STATUSES,
  STATUS_LABELS,
} from "../../utils/taskMeta";
import Alert from "../ui/Alert";
import Button from "../ui/Button";
import Select from "../ui/Select";
import Textarea from "../ui/Textarea";
import TextField from "../ui/TextField";

interface TaskFormProps {
  mode: "create" | "edit";
  initialValues?: TaskFormValues;
  projects?: Project[];
  submitLabel: string;
  onSubmit: (values: TaskFormValues) => Promise<void>;
  onCancel: () => void;
}

export default function TaskForm({
  mode,
  initialValues = EMPTY_TASK_FORM,
  projects = [],
  submitLabel,
  onSubmit,
  onCancel,
}: TaskFormProps) {
  const [values, setValues] = useState<TaskFormValues>(initialValues);
  const [errors, setErrors] = useState<FieldErrors>({});
  const [formError, setFormError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const handleChange = (
    e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setValues((prev) => ({ ...prev, [name]: value }));
    setErrors((prev) => ({ ...prev, [name]: undefined }));
  };

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setFormError("");

    const found = validateTask(values, mode);
    setErrors(found);
    if (Object.keys(found).length > 0) return;

    setSubmitting(true);
    try {
      await onSubmit(values);
    } catch (err) {
      const { message, fieldErrors } = parseApiError(err);
      setFormError(message);
      setErrors(fieldErrors);
      setSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} noValidate className="space-y-5">
      <Alert>{formError}</Alert>

      {mode === "create" && (
        <Select
          label="Project"
          showLabel
          name="projectId"
          value={values.projectId}
          onChange={handleChange}
          error={errors.projectId}
        >
          <option value="">Choose a project</option>
          {projects.map((project) => (
            <option key={project.id} value={project.id}>
              {project.name}
            </option>
          ))}
        </Select>
      )}

      <TextField
        id="title"
        name="title"
        label="Title"
        value={values.title}
        onChange={handleChange}
        error={errors.title}
      />

      <Textarea
        id="description"
        name="description"
        label="Description (optional)"
        rows={5}
        value={values.description}
        onChange={handleChange}
        error={errors.description}
      />

      <div className="grid grid-cols-1 gap-5 sm:grid-cols-2">
        <Select
          label="Priority"
          showLabel
          name="priority"
          value={values.priority}
          onChange={handleChange}
          error={errors.priority}
        >
          {PRIORITIES.map((priority) => (
            <option key={priority} value={priority}>
              {PRIORITY_LABELS[priority]}
            </option>
          ))}
        </Select>

        {mode === "edit" && (
          <Select
            label="Status"
            showLabel
            name="status"
            value={values.status}
            onChange={handleChange}
            error={errors.status}
          >
            {STATUSES.map((status) => (
              <option key={status} value={status}>
                {STATUS_LABELS[status]}
              </option>
            ))}
          </Select>
        )}

        <TextField
          id="dueDate"
          name="dueDate"
          type="date"
          label="Due date (optional)"
          value={values.dueDate}
          onChange={handleChange}
          error={errors.dueDate}
        />
      </div>

      <div className="flex items-center gap-4 border-t border-white/10 pt-5">
        <Button type="submit" loading={submitting} fullWidth={false}>
          {submitLabel}
        </Button>
        <button
          type="button"
          onClick={onCancel}
          className="rounded-md text-sm text-ink-400 transition hover:text-white focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-accent"
        >
          Cancel
        </button>
      </div>
    </form>
  );
}
