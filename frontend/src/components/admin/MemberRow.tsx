import { useState } from "react";
import * as userApi from "../../api/userApi";
import type { User } from "../../types/auth";
import { parseApiError } from "../../utils/apiError";
import ConfirmAction from "../ui/ConfirmAction";

interface MemberRowProps {
  member: User;
  isSelf: boolean;
  onChange: (member: User) => void;
}

export default function MemberRow({ member, isSelf, onChange }: MemberRowProps) {
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");

  const toggleBlocked = async () => {
    setBusy(true);
    setError("");
    try {
      await userApi.setUserBlocked(member.id, !member.blocked);
      onChange({ ...member, blocked: !member.blocked });
    } catch (err) {
      setError(parseApiError(err).message);
    } finally {
      setBusy(false);
    }
  };

  const makeAdmin = async () => {
    await userApi.setUserRole(member.id, "ADMIN");
    onChange({ ...member, role: "ADMIN" });
  };

  return (
    <li className="py-4">
      <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div className="flex min-w-0 items-center gap-3">
          <span
            aria-hidden="true"
            className="grid size-9 shrink-0 place-items-center rounded-full border border-white/10 bg-white/5 text-xs font-semibold text-ink-200"
          >
            {member.firstName?.[0] ?? "U"}
          </span>
          <div className="min-w-0">
            <p className="truncate text-sm font-medium text-white">
              {member.firstName} {member.lastName}
              {isSelf && <span className="font-normal text-ink-400"> (you)</span>}
            </p>
            <p className="truncate text-sm text-ink-400">{member.email}</p>
          </div>
        </div>

        <div className="flex shrink-0 flex-wrap items-center gap-3 text-sm">
          {member.role === "ADMIN" && (
            <span className="rounded-full border border-white/15 bg-white/10 px-2.5 py-0.5 text-xs font-medium text-white">
              Admin
            </span>
          )}
          {member.blocked && (
            <span className="rounded-full border border-rose-400/25 bg-rose-400/10 px-2.5 py-0.5 text-xs font-medium text-rose-200">
              Blocked
            </span>
          )}

          {!isSelf && (
            <>
              {member.role !== "ADMIN" && (
                <ConfirmAction
                  label="Make admin"
                  question="Make this user an admin?"
                  onConfirm={makeAdmin}
                />
              )}
              <button
                type="button"
                onClick={toggleBlocked}
                disabled={busy}
                className="rounded-md text-ink-400 transition hover:text-white focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-accent disabled:opacity-50"
              >
                {member.blocked ? "Unblock" : "Block"}
              </button>
            </>
          )}
        </div>
      </div>

      {error && <p className="mt-2 text-xs font-medium text-rose-300">{error}</p>}
    </li>
  );
}
