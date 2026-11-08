import Select from "../ui/Select";
import { fieldClass } from "../ui/fieldStyles";
import { PRIORITIES, PRIORITY_LABELS, STATUSES, STATUS_LABELS } from "../../utils/taskMeta";
import { SORT_LABELS, type SortOption, type TaskFilters } from "../../utils/taskFilters";

interface TaskToolbarProps {
  filters: TaskFilters;
  onChange: (filters: TaskFilters) => void;
}

export default function TaskToolbar({ filters, onChange }: TaskToolbarProps) {
  return (
    <div className="rounded-2xl border border-white/10 bg-ink-900/60 p-3 sm:p-4">
      <div className="flex flex-col gap-3 lg:flex-row lg:items-center">
        <div className="relative min-w-0 flex-1">
          <svg
            aria-hidden="true"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth={1.8}
            strokeLinecap="round"
            className="pointer-events-none absolute left-3.5 top-1/2 size-4 -translate-y-1/2 text-ink-400"
          >
            <circle cx="11" cy="11" r="7" />
            <path d="m16.5 16.5 4 4" />
          </svg>
          <input
            type="search"
            aria-label="Search tasks"
            placeholder="Search tasks"
            value={filters.search}
            onChange={(e) => onChange({ ...filters, search: e.target.value })}
            className={fieldClass(false, "pl-10")}
          />
        </div>

        <div className="grid grid-cols-2 gap-3 sm:flex">
          <Select
            label="Filter by status"
            value={filters.status}
            onChange={(e) => onChange({ ...filters, status: e.target.value as TaskFilters["status"] })}
          >
            <option value="ALL">All statuses</option>
            {STATUSES.map((status) => (
              <option key={status} value={status}>
                {STATUS_LABELS[status]}
              </option>
            ))}
          </Select>

          <Select
            label="Filter by priority"
            value={filters.priority}
            onChange={(e) =>
              onChange({ ...filters, priority: e.target.value as TaskFilters["priority"] })
            }
          >
            <option value="ALL">All priorities</option>
            {PRIORITIES.map((priority) => (
              <option key={priority} value={priority}>
                {PRIORITY_LABELS[priority]}
              </option>
            ))}
          </Select>

          <Select
            label="Sort tasks"
            wrapperClassName="col-span-2 sm:col-span-1"
            value={filters.sort}
            onChange={(e) => onChange({ ...filters, sort: e.target.value as SortOption })}
          >
            {(Object.keys(SORT_LABELS) as SortOption[]).map((option) => (
              <option key={option} value={option}>
                Sort: {SORT_LABELS[option]}
              </option>
            ))}
          </Select>
        </div>
      </div>
    </div>
  );
}
