import { useState } from "react";
import { useAuth } from "../../hooks/useAuth";
import type { TaskComment } from "../../types/comment";
import { parseApiError } from "../../utils/apiError";
import { formatDateTime } from "../../utils/date";
import CommentForm from "./CommentForm";

interface CommentItemProps {
  comment: TaskComment;
  isOwn: boolean;
  onUpdate: (id: number, text: string) => Promise<void>;
  onDelete: (id: number) => Promise<void>;
}

const actionClass =
  "rounded-md text-ink-400 transition hover:text-white focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-accent";

export default function CommentItem({ comment, isOwn, onUpdate, onDelete }: CommentItemProps) {
  const { user } = useAuth();
  const [editing, setEditing] = useState(false);
  const [confirmingDelete, setConfirmingDelete] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const [deleteError, setDeleteError] = useState("");

  const author = isOwn ? "You" : (comment.createdByName ?? "Team member");
  const initial = isOwn
    ? (user?.firstName?.[0] ?? "Y").toUpperCase()
    : author.trim().charAt(0).toUpperCase() || "?";

  const handleDelete = async () => {
    setDeleting(true);
    setDeleteError("");
    try {
      await onDelete(comment.id);
    } catch (err) {
      setDeleteError(parseApiError(err).message);
      setDeleting(false);
    }
  };

  return (
    <li className="flex gap-3 py-5">
      <span
        aria-hidden="true"
        className="grid size-8 shrink-0 place-items-center rounded-full border border-accent/20 bg-accent/10 text-xs font-semibold text-accent"
      >
        {initial}
      </span>

      <div className="min-w-0 flex-1">
        <div className="flex flex-wrap items-baseline justify-between gap-2">
          <p className="text-sm font-medium text-white">{author}</p>
          <p className="shrink-0 text-xs text-ink-400">{formatDateTime(comment.createdAt)}</p>
        </div>

        {editing ? (
          <div className="mt-3">
            <CommentForm
              initialValue={comment.text}
              submitLabel="Save"
              onSubmit={async (text) => {
                await onUpdate(comment.id, text);
                setEditing(false);
              }}
              onCancel={() => setEditing(false)}
            />
          </div>
        ) : (
          <p className="mt-1 whitespace-pre-wrap text-sm leading-6 text-ink-200">{comment.text}</p>
        )}

        {isOwn && !editing && (
          <div className="mt-2 flex items-center gap-4 text-xs">
            {confirmingDelete ? (
              <>
                <span className="text-ink-300">Delete this comment?</span>
                <button
                  type="button"
                  onClick={handleDelete}
                  disabled={deleting}
                  className="rounded-md font-medium text-rose-300 transition hover:text-rose-200 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-accent disabled:opacity-50"
                >
                  {deleting ? "Deleting…" : "Yes, delete"}
                </button>
                <button
                  type="button"
                  onClick={() => setConfirmingDelete(false)}
                  disabled={deleting}
                  className={actionClass}
                >
                  Cancel
                </button>
              </>
            ) : (
              <>
                <button type="button" onClick={() => setEditing(true)} className={actionClass}>
                  Edit
                </button>
                <button type="button" onClick={() => setConfirmingDelete(true)} className={actionClass}>
                  Delete
                </button>
              </>
            )}
          </div>
        )}

        {deleteError && <p className="mt-2 text-xs font-medium text-rose-300">{deleteError}</p>}
      </div>
    </li>
  );
}
