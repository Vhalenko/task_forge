import { useState } from "react";
import { parseApiError } from "../../utils/apiError";

interface ConfirmActionProps {
  label: string;
  question: string;
  confirmLabel?: string;
  onConfirm: () => Promise<void>;
}

export default function ConfirmAction({
  label,
  question,
  confirmLabel = "Yes",
  onConfirm,
}: ConfirmActionProps) {
  const [confirming, setConfirming] = useState(false);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");

  const handleConfirm = async () => {
    setBusy(true);
    setError("");
    try {
      await onConfirm();
    } catch (err) {
      setError(parseApiError(err).message);
      setBusy(false);
    }
  };

  return (
    <div className="flex flex-col items-end gap-1">
      <div className="flex items-center gap-3 text-sm">
        {confirming ? (
          <>
            <span className="text-ink-300">{question}</span>
            <button
              type="button"
              onClick={handleConfirm}
              disabled={busy}
              className="rounded-md font-medium text-rose-300 transition hover:text-rose-200 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-accent disabled:opacity-50"
            >
              {busy ? "Please wait…" : confirmLabel}
            </button>
            <button
              type="button"
              onClick={() => setConfirming(false)}
              disabled={busy}
              className="rounded-md text-ink-400 transition hover:text-white focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-accent disabled:opacity-50"
            >
              Cancel
            </button>
          </>
        ) : (
          <button
            type="button"
            onClick={() => setConfirming(true)}
            className="rounded-md text-ink-400 transition hover:text-white focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-accent"
          >
            {label}
          </button>
        )}
      </div>
      {error && <p className="text-xs font-medium text-rose-300">{error}</p>}
    </div>
  );
}
