export const labelClass = "mb-1.5 block text-sm font-medium text-ink-300";

export const errorClass = "mt-1.5 text-xs font-medium text-rose-300";

/**
 * Shared surface for every text input, textarea, and select.
 * Error state replaces the border/focus colors so the two never fight.
 */
export function fieldClass(hasError = false, extra = "") {
  const state = hasError
    ? "border-rose-400/70 hover:border-rose-400 focus:border-rose-300 focus:ring-rose-400/25"
    : "border-white/15 hover:border-white/25 focus:border-accent/80 focus:ring-accent/25";

  return [
    "block w-full rounded-xl border bg-ink-800 px-3.5 py-2.5 text-sm text-ink-100 shadow-sm shadow-black/25 outline-none transition",
    "placeholder:text-ink-400 focus:ring-2 disabled:cursor-not-allowed disabled:opacity-50",
    state,
    extra,
  ].join(" ");
}
