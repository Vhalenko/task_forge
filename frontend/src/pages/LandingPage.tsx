import type { ReactNode } from "react";
import { Link } from "react-router-dom";
import LinkButton from "../components/ui/LinkButton";
import { useAuth } from "../hooks/useAuth";
import type { TaskPriority } from "../types/task";
import { PRIORITY_LABELS } from "../utils/taskMeta";

const CURRENT_YEAR = new Date().getFullYear();

const PRIORITY_DOT: Record<TaskPriority, string> = {
  LOW: "bg-ink-400",
  MEDIUM: "bg-amber-400",
  HIGH: "bg-rose-400",
};

const PRIORITY_CHIP: Record<TaskPriority, string> = {
  LOW: "border-white/15 bg-white/[0.06] text-ink-200",
  MEDIUM: "border-amber-400/25 bg-amber-400/10 text-amber-200",
  HIGH: "border-rose-400/25 bg-rose-400/10 text-rose-200",
};

interface PreviewTask {
  title: string;
  priority: TaskPriority;
  due: string;
}

const PREVIEW_COLUMNS: { label: string; dot: string; tasks: PreviewTask[] }[] = [
  {
    label: "To do",
    dot: "bg-ink-400",
    tasks: [
      { title: "Write onboarding checklist", priority: "MEDIUM", due: "Nov 12" },
      { title: "Set up release notes", priority: "LOW", due: "No date" },
    ],
  },
  {
    label: "In progress",
    dot: "bg-sky-400",
    tasks: [
      { title: "Redesign the overview page", priority: "HIGH", due: "Nov 5" },
      { title: "Audit empty states", priority: "MEDIUM", due: "Nov 8" },
    ],
  },
  {
    label: "Done",
    dot: "bg-emerald-400",
    tasks: [{ title: "Ship dark theme tokens", priority: "LOW", due: "Oct 25" }],
  },
];

const FEATURES: { title: string; description: string; icon: ReactNode }[] = [
  {
    title: "Plan together",
    description:
      "Projects, tasks, owners, and due dates live in one shared place — no more hunting through three tools for the latest plan.",
    icon: (
      <svg
        aria-hidden="true"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        strokeWidth={1.7}
        strokeLinecap="round"
        strokeLinejoin="round"
        className="size-5"
      >
        <rect x="4" y="3" width="16" height="18" rx="2" />
        <path d="M9 8h6M9 12h6M9 16h4" />
      </svg>
    ),
  },
  {
    title: "Move work forward",
    description:
      "Drag tasks across a board that mirrors your real workflow, with priorities and due dates that stay visible.",
    icon: (
      <svg
        aria-hidden="true"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        strokeWidth={1.7}
        strokeLinecap="round"
        strokeLinejoin="round"
        className="size-5"
      >
        <rect x="3" y="4" width="5" height="16" rx="1.5" />
        <rect x="10" y="4" width="5" height="10" rx="1.5" />
        <rect x="17" y="4" width="4" height="13" rx="1.5" />
      </svg>
    ),
  },
  {
    title: "Decide in context",
    description:
      "Comments sit on the task itself, so decisions, questions, and context stay next to the work they affect.",
    icon: (
      <svg
        aria-hidden="true"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        strokeWidth={1.7}
        strokeLinecap="round"
        strokeLinejoin="round"
        className="size-5"
      >
        <path d="M20 14.5a3 3 0 0 1-3 3H9l-5 3v-6a3 3 0 0 1-1-2V7a3 3 0 0 1 3-3h11a3 3 0 0 1 3 3z" />
        <path d="M8 9h8M8 12.5h5" />
      </svg>
    ),
  },
  {
    title: "Admin without busywork",
    description:
      "Create teams, invite teammates, assign roles, and keep access tidy — without a separate admin tool.",
    icon: (
      <svg
        aria-hidden="true"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        strokeWidth={1.7}
        strokeLinecap="round"
        strokeLinejoin="round"
        className="size-5"
      >
        <path d="M12 3 5 6v5.4c0 4.2 2.9 7.7 7 9.6 4.1-1.9 7-5.4 7-9.6V6z" />
        <path d="m9.3 12 1.9 1.9 3.5-3.7" />
      </svg>
    ),
  },
];

const STEPS = [
  {
    number: "01",
    title: "Create your team",
    description: "Set up a shared workspace and invite the people who do the work.",
  },
  {
    number: "02",
    title: "Shape the project",
    description: "Break work into projects, assign owners, and set priorities and due dates.",
  },
  {
    number: "03",
    title: "Ship with clarity",
    description: "Move work across the board and keep every conversation attached to its task.",
  },
];

const TEAM_PROJECTS = [
  { name: "Website redesign", progress: 68, color: "bg-accent" },
  { name: "Mobile app", progress: 42, color: "bg-sky-400" },
  { name: "Design system", progress: 84, color: "bg-emerald-400" },
];

const ACTIVITY = [
  {
    initials: "PR",
    name: "Priya",
    action: "commented on",
    target: "Audit empty states",
    time: "12 min ago",
    avatarClass: "bg-sky-400/15 text-sky-200",
  },
  {
    initials: "JW",
    name: "Jonas",
    action: "moved",
    target: "Fix focus states",
    time: "48 min ago",
    avatarClass: "bg-amber-400/15 text-amber-200",
  },
  {
    initials: "ML",
    name: "Maya",
    action: "completed",
    target: "Dark theme tokens",
    time: "2 hours ago",
    avatarClass: "bg-accent/15 text-accent",
  },
];

function Brand({ compact = false }: { compact?: boolean }) {
  return (
    <span className="flex items-center gap-2.5">
      <span className="grid size-8 shrink-0 place-items-center rounded-lg bg-accent text-sm font-bold text-ink-950">
        ✦
      </span>
      <span className={`font-semibold tracking-tight text-white ${compact ? "text-sm" : "text-base"}`}>
        TaskForge
      </span>
    </span>
  );
}

function LandingHeader({ isAuthenticated }: { isAuthenticated: boolean }) {
  return (
    <header className="sticky top-0 z-40 border-b border-white/10 bg-ink-950/80 backdrop-blur-xl">
      <div className="mx-auto flex h-16 max-w-6xl items-center justify-between gap-4 px-4 sm:px-6">
        <Link
          to="/"
          className="rounded-lg focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-accent"
        >
          <Brand />
        </Link>

        <nav aria-label="Landing sections" className="hidden items-center gap-7 text-sm text-ink-400 sm:flex">
          <a href="#features" className="rounded-md transition hover:text-white focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-accent">
            Features
          </a>
          <a href="#workflow" className="rounded-md transition hover:text-white focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-accent">
            How it works
          </a>
        </nav>

        <div className="flex items-center gap-2">
          {isAuthenticated ? (
            <LinkButton to="/tasks">Open workspace</LinkButton>
          ) : (
            <>
              <Link
                to="/login"
                className="hidden rounded-lg px-3 py-2 text-sm font-medium text-ink-300 transition hover:bg-white/5 hover:text-white focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-accent sm:inline-flex"
              >
                Sign in
              </Link>
              <LinkButton to="/register">Get started</LinkButton>
            </>
          )}
        </div>
      </div>
    </header>
  );
}

function ProductPreview() {
  return (
    <div className="relative mx-auto w-full min-w-0 max-w-xl lg:max-w-none">
      <div
        aria-hidden="true"
        className="absolute -inset-5 rounded-[2rem] bg-accent/[0.06] blur-2xl"
      />
      <p className="sr-only">
        Preview of a TaskForge board with tasks arranged in To do, In progress, and Done columns.
      </p>
      <div
        aria-hidden="true"
        className="relative overflow-hidden rounded-2xl border border-white/10 bg-ink-900 shadow-[0_40px_120px_-40px_rgb(0_0_0/0.95)]"
      >
        <div className="flex items-center justify-between gap-3 border-b border-white/10 bg-ink-850/80 px-4 py-3">
          <div className="flex items-center gap-1.5" aria-hidden="true">
            <span className="size-2 rounded-full bg-rose-400/70" />
            <span className="size-2 rounded-full bg-amber-400/70" />
            <span className="size-2 rounded-full bg-emerald-400/70" />
          </div>
          <p className="truncate text-[11px] font-medium text-ink-400">
            Northwind Product · Website redesign
          </p>
          <span className="inline-flex shrink-0 items-center gap-1 rounded-full border border-accent/25 bg-accent/10 px-2 py-0.5 text-[10px] font-semibold text-accent">
            <span aria-hidden="true" className="size-1 rounded-full bg-accent" />
            Preview
          </span>
        </div>

        <div className="flex">
          <aside className="hidden w-32 shrink-0 border-r border-white/10 p-3 sm:block">
            <p className="px-1 text-[10px] font-semibold uppercase tracking-[0.16em] text-ink-400">
              Workspace
            </p>
            <ul className="mt-3 space-y-1">
              <li className="flex items-center gap-2 rounded-lg bg-white/[0.06] px-2 py-1.5 text-[11px] font-medium text-white">
                <span aria-hidden="true" className="size-3 rounded-sm bg-accent/40" />
                My tasks
              </li>
              <li className="flex items-center gap-2 rounded-lg px-2 py-1.5 text-[11px] text-ink-400">
                <span aria-hidden="true" className="size-3 rounded-sm bg-white/10" />
                Projects
              </li>
              <li className="flex items-center gap-2 rounded-lg px-2 py-1.5 text-[11px] text-ink-400">
                <span aria-hidden="true" className="size-3 rounded-sm bg-white/10" />
                Profile
              </li>
            </ul>
          </aside>

          <div className="min-w-0 flex-1 p-3 sm:p-4">
            <div className="mb-3 flex items-center justify-between gap-2">
              <div className="min-w-0">
                <p className="truncate text-xs font-semibold text-white">Website redesign</p>
                <p className="mt-0.5 text-[10px] text-ink-400">6 tasks · 2 in progress</p>
              </div>
              <span className="shrink-0 rounded-md bg-accent px-2 py-1 text-[10px] font-semibold text-ink-950">
                New task
              </span>
            </div>

            <div className="grid grid-cols-2 gap-2 sm:grid-cols-3">
              {PREVIEW_COLUMNS.map((column, index) => (
                <div key={column.label} className={`min-w-0 ${index === 2 ? "hidden sm:block" : ""}`}>
                  <div className="mb-2 flex items-center justify-between gap-1 px-0.5">
                    <span className="flex items-center gap-1.5 text-[10px] font-semibold text-ink-200">
                      <span aria-hidden="true" className={`size-1.5 rounded-full ${column.dot}`} />
                      {column.label}
                    </span>
                    <span className="rounded-full bg-white/5 px-1.5 text-[9px] text-ink-400">
                      {column.tasks.length}
                    </span>
                  </div>
                  <div className="space-y-2">
                    {column.tasks.map((task) => (
                      <div
                        key={task.title}
                        className="rounded-lg border border-white/10 bg-ink-800 p-2.5"
                      >
                        <div className="flex items-start justify-between gap-1.5">
                          <p className="text-[10px] font-medium leading-4 text-white">{task.title}</p>
                          <span
                            aria-hidden="true"
                            className={`mt-1 size-1.5 shrink-0 rounded-full ${PRIORITY_DOT[task.priority]}`}
                          />
                        </div>
                        <div className="mt-2 flex items-center justify-between gap-1">
                          <span className="text-[9px] text-ink-400">{task.due}</span>
                          <span
                            className={`rounded border px-1 py-0.5 text-[9px] font-medium ${PRIORITY_CHIP[task.priority]}`}
                          >
                            {PRIORITY_LABELS[task.priority]}
                          </span>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
      <p className="mt-3 text-center text-[11px] text-ink-400">
        A preview of the TaskForge board — minus the tab juggling.
      </p>
    </div>
  );
}

function Hero({ isAuthenticated }: { isAuthenticated: boolean }) {
  return (
    <section className="relative overflow-hidden">
      <div
        aria-hidden="true"
        className="pointer-events-none absolute -right-40 -top-56 size-[34rem] rounded-full bg-accent/[0.08] blur-3xl"
      />
      <div
        aria-hidden="true"
        className="pointer-events-none absolute -left-40 top-24 size-[26rem] rounded-full bg-sky-400/[0.05] blur-3xl"
      />

      <div className="relative mx-auto grid max-w-6xl gap-12 px-4 pb-16 pt-12 sm:px-6 sm:pt-16 lg:grid-cols-[1.02fr_1fr] lg:items-center lg:gap-14 lg:pb-24 lg:pt-20">
        <div className="min-w-0">
          <p className="inline-flex items-center gap-2 rounded-full border border-accent/25 bg-accent/10 px-3 py-1 text-xs font-medium text-accent">
            <span aria-hidden="true" className="size-1.5 rounded-full bg-accent" />
            Built for small product teams
          </p>

          <h1 className="mt-6 text-4xl font-semibold leading-[1.06] tracking-tight text-white sm:text-5xl lg:text-6xl">
            Where your team&apos;s work{" "}
            <span className="text-accent">comes together.</span>
          </h1>

          <p className="mt-6 max-w-xl text-base leading-7 text-ink-300 sm:text-lg sm:leading-8">
            TaskForge is the shared workspace for planning projects, assigning tasks, and keeping
            every decision next to the work it affects — so your team always knows what&apos;s next.
          </p>

          <div className="mt-8 flex flex-col gap-3 sm:flex-row">
            {isAuthenticated ? (
              <>
                <LinkButton to="/tasks" size="lg">
                  Open your workspace
                </LinkButton>
                <LinkButton to="/projects" size="lg" variant="secondary">
                  Browse projects
                </LinkButton>
              </>
            ) : (
              <>
                <LinkButton to="/register" size="lg">
                  Get started
                </LinkButton>
                <LinkButton to="/login" size="lg" variant="secondary">
                  Sign in
                </LinkButton>
              </>
            )}
          </div>

          <ul className="mt-8 flex flex-wrap items-center gap-x-5 gap-y-2 text-xs text-ink-400">
            {["Projects and tasks in one place", "Boards, priorities, and due dates", "Comments attached to the work"].map(
              (item) => (
                <li key={item} className="inline-flex items-center gap-1.5">
                  <svg
                    aria-hidden="true"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth={2}
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    className="size-3.5 text-accent"
                  >
                    <path d="m4 12 5 5L20 6" />
                  </svg>
                  {item}
                </li>
              )
            )}
          </ul>
        </div>

        <ProductPreview />
      </div>
    </section>
  );
}

function Features() {
  return (
    <section id="features" className="scroll-mt-24 border-t border-white/10">
      <div className="mx-auto max-w-6xl px-4 py-16 sm:px-6 lg:py-24">
        <div className="max-w-2xl">
          <p className="text-xs font-semibold uppercase tracking-[0.18em] text-accent">
            Everything in one place
          </p>
          <h2 className="mt-3 text-3xl font-semibold tracking-tight text-white sm:text-4xl">
            Less chasing. More shipping.
          </h2>
          <p className="mt-4 text-base leading-7 text-ink-400">
            Status updates, task details, and decisions stop living in five different tabs. TaskForge
            gives your team one place to look — and one place to work.
          </p>
        </div>

        <div className="mt-10 grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
          {FEATURES.map((feature) => (
            <article
              key={feature.title}
              className="rounded-2xl border border-white/10 bg-ink-900/60 p-5 transition hover:-translate-y-0.5 hover:border-accent/30"
            >
              <span className="grid size-10 place-items-center rounded-xl border border-accent/20 bg-accent/10 text-accent">
                {feature.icon}
              </span>
              <h3 className="mt-4 text-sm font-semibold text-white">{feature.title}</h3>
              <p className="mt-2 text-sm leading-6 text-ink-400">{feature.description}</p>
            </article>
          ))}
        </div>
      </div>
    </section>
  );
}

function TeamPulse() {
  return (
    <div className="rounded-2xl border border-white/10 bg-ink-900/70 p-5 shadow-[0_30px_80px_-50px_rgb(0_0_0/0.9)] sm:p-6">
      <div className="flex items-center justify-between gap-3 border-b border-white/10 pb-4">
        <div>
          <p className="text-sm font-semibold text-white">Team pulse</p>
          <p className="mt-0.5 text-xs text-ink-400">This week at Northwind</p>
        </div>
        <div className="flex -space-x-2" aria-hidden="true">
          {[
            { initial: "M", className: "bg-accent/20 text-accent" },
            { initial: "J", className: "bg-sky-400/15 text-sky-200" },
            { initial: "P", className: "bg-amber-400/15 text-amber-200" },
            { initial: "T", className: "bg-white/10 text-ink-200" },
          ].map((member) => (
            <span
              key={member.initial}
              className={`grid size-7 place-items-center rounded-full border-2 border-ink-900 text-[10px] font-semibold ${member.className}`}
            >
              {member.initial}
            </span>
          ))}
        </div>
      </div>

      <div className="mt-5 space-y-4">
        {TEAM_PROJECTS.map((project) => (
          <div key={project.name}>
            <div className="flex items-center justify-between gap-3 text-xs">
              <span className="font-medium text-ink-200">{project.name}</span>
              <span className="text-ink-400">{project.progress}%</span>
            </div>
            <div
              role="progressbar"
              aria-valuenow={project.progress}
              aria-valuemin={0}
              aria-valuemax={100}
              aria-label={`${project.name} progress`}
              className="mt-2 h-1.5 overflow-hidden rounded-full bg-white/10"
            >
              <div
                className={`h-full rounded-full ${project.color}`}
                style={{ width: `${project.progress}%` }}
              />
            </div>
          </div>
        ))}
      </div>

      <div className="mt-6 border-t border-white/10 pt-5">
        <p className="text-[11px] font-semibold uppercase tracking-[0.14em] text-ink-400">
          Recent activity
        </p>
        <ul className="mt-3 space-y-3">
          {ACTIVITY.map((item) => (
            <li key={`${item.name}-${item.target}`} className="flex items-start gap-2.5">
              <span
                aria-hidden="true"
                className={`grid size-6 shrink-0 place-items-center rounded-full text-[9px] font-semibold ${item.avatarClass}`}
              >
                {item.initials}
              </span>
              <p className="text-xs leading-5 text-ink-400">
                <span className="font-medium text-ink-200">{item.name}</span> {item.action}{" "}
                <span className="text-ink-300">{item.target}</span>
                <span className="block text-[10px] text-ink-400">{item.time}</span>
              </p>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}

function Workflow() {
  return (
    <section id="workflow" className="scroll-mt-24 border-t border-white/10 bg-ink-900/40">
      <div className="mx-auto max-w-6xl px-4 py-16 sm:px-6 lg:py-24">
        <div className="grid gap-12 lg:grid-cols-[0.9fr_1.1fr] lg:items-center">
          <div>
            <p className="text-xs font-semibold uppercase tracking-[0.18em] text-accent">
              How it works
            </p>
            <h2 className="mt-3 text-3xl font-semibold tracking-tight text-white sm:text-4xl">
              From kickoff to shipped, without the tab juggling.
            </h2>
            <p className="mt-4 text-base leading-7 text-ink-400">
              TaskForge keeps the plan, the progress, and the conversation in the same place — from
              the first task to the final handoff.
            </p>

            <ol className="mt-8 space-y-6">
              {STEPS.map((step) => (
                <li key={step.number} className="flex gap-4">
                  <span className="grid size-8 shrink-0 place-items-center rounded-lg border border-white/10 bg-white/5 text-xs font-semibold text-accent">
                    {step.number}
                  </span>
                  <div>
                    <h3 className="text-sm font-semibold text-white">{step.title}</h3>
                    <p className="mt-1 text-sm leading-6 text-ink-400">{step.description}</p>
                  </div>
                </li>
              ))}
            </ol>
          </div>

          <TeamPulse />
        </div>
      </div>
    </section>
  );
}

function FinalCta({ isAuthenticated }: { isAuthenticated: boolean }) {
  return (
    <section className="mx-auto max-w-6xl px-4 pb-16 sm:px-6 lg:pb-24">
      <div className="relative overflow-hidden rounded-3xl border border-accent/20 bg-gradient-to-br from-accent/[0.12] via-ink-900 to-ink-900 px-6 py-12 text-center sm:px-12 sm:py-16">
        <div
          aria-hidden="true"
          className="pointer-events-none absolute -right-16 -top-20 size-64 rounded-full bg-accent/10 blur-3xl"
        />
        <div className="relative">
          <h2 className="text-3xl font-semibold tracking-tight text-white sm:text-4xl">
            Bring your team&apos;s work into focus.
          </h2>
          <p className="mx-auto mt-4 max-w-xl text-base leading-7 text-ink-300">
            Start with one project, invite your team when you&apos;re ready, and keep everything
            that matters in one place.
          </p>
          <div className="mt-7 flex flex-col justify-center gap-3 sm:flex-row">
            {isAuthenticated ? (
              <>
                <LinkButton to="/tasks" size="lg">
                  Open your workspace
                </LinkButton>
                <LinkButton to="/projects" size="lg" variant="secondary">
                  Browse projects
                </LinkButton>
              </>
            ) : (
              <>
                <LinkButton to="/register" size="lg">
                  Get started
                </LinkButton>
                <LinkButton to="/login" size="lg" variant="secondary">
                  Sign in
                </LinkButton>
              </>
            )}
          </div>
        </div>
      </div>
    </section>
  );
}

function Footer() {
  return (
    <footer className="border-t border-white/10">
      <div className="mx-auto flex max-w-6xl flex-col gap-6 px-4 py-8 sm:flex-row sm:items-center sm:justify-between sm:px-6">
        <div>
          <Link
            to="/"
            className="rounded-lg focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-accent"
          >
            <Brand compact />
          </Link>
          <p className="mt-2 text-xs text-ink-400">A shared workspace for teams that ship.</p>
        </div>

        <nav aria-label="Footer" className="flex flex-wrap gap-x-6 gap-y-2 text-sm text-ink-400">
          <a href="#features" className="transition hover:text-white focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-accent">
            Features
          </a>
          <a href="#workflow" className="transition hover:text-white focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-accent">
            How it works
          </a>
          <Link to="/login" className="transition hover:text-white focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-accent">
            Sign in
          </Link>
          <Link to="/register" className="transition hover:text-white focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-accent">
            Create account
          </Link>
        </nav>

        <p className="text-xs text-ink-400">© {CURRENT_YEAR} TaskForge</p>
      </div>
    </footer>
  );
}

export default function LandingPage() {
  const { isAuthenticated } = useAuth();

  return (
    <div className="min-h-screen">
      <LandingHeader isAuthenticated={isAuthenticated} />
      <main>
        <Hero isAuthenticated={isAuthenticated} />
        <Features />
        <Workflow />
        <FinalCta isAuthenticated={isAuthenticated} />
      </main>
      <Footer />
    </div>
  );
}
