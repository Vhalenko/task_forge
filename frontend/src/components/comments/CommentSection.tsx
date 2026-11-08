import { useCallback } from "react";
import * as commentApi from "../../api/commentApi";
import { useAsyncData } from "../../hooks/useAsyncData";
import { useAuth } from "../../hooks/useAuth";
import Alert from "../ui/Alert";
import Button from "../ui/Button";
import Spinner from "../ui/Spinner";
import CommentForm from "./CommentForm";
import CommentItem from "./CommentItem";

export default function CommentSection({ taskId }: { taskId: number }) {
  const { user } = useAuth();
  const fetchComments = useCallback(() => commentApi.getTaskComments(taskId), [taskId]);
  const { data: comments, setData, loading, error, reload } = useAsyncData(fetchComments);

  const handleCreate = async (text: string) => {
    const created = await commentApi.createComment(taskId, { text });
    setData((prev) => [...(prev ?? []), created]);
  };

  const handleUpdate = async (id: number, text: string) => {
    const updated = await commentApi.updateComment(id, { text });
    setData((prev) => (prev ?? []).map((c) => (c.id === id ? updated : c)));
  };

  const handleDelete = async (id: number) => {
    await commentApi.deleteComment(id);
    setData((prev) => (prev ?? []).filter((c) => c.id !== id));
  };

  function renderBody() {
    if (loading) return <Spinner />;

    if (error) {
      return (
        <div className="mt-4 space-y-3">
          <Alert>{error}</Alert>
          <Button type="button" variant="secondary" fullWidth={false} onClick={reload}>
            Try again
          </Button>
        </div>
      );
    }

    const list = comments ?? [];

    return (
      <>
        <div className="mt-4 overflow-hidden rounded-2xl border border-white/10 bg-ink-900/70 px-5">
          {list.length === 0 ? (
            <p className="py-8 text-center text-sm text-ink-400">
              No comments yet. Start the conversation below.
            </p>
          ) : (
            <ul className="divide-y divide-white/10">
              {list.map((comment) => (
                <CommentItem
                  key={comment.id}
                  comment={comment}
                  isOwn={comment.createdById === user?.id}
                  onUpdate={handleUpdate}
                  onDelete={handleDelete}
                />
              ))}
            </ul>
          )}
        </div>

        <div className="mt-4 rounded-2xl border border-white/10 bg-ink-900/60 p-4 sm:p-5">
          <CommentForm
            submitLabel="Comment"
            placeholder="Write a comment…"
            onSubmit={handleCreate}
          />
        </div>
      </>
    );
  }

  return (
    <section aria-labelledby="comments-heading">
      <div className="flex items-center gap-2.5">
        <h2 id="comments-heading" className="text-lg font-semibold tracking-tight text-white">
          Comments
        </h2>
        {comments && comments.length > 0 && (
          <span className="rounded-full border border-white/10 bg-white/5 px-2 py-0.5 text-xs font-medium text-ink-300">
            {comments.length}
          </span>
        )}
      </div>
      {renderBody()}
    </section>
  );
}
