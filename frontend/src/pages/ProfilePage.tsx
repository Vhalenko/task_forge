import { useState, type FormEvent } from "react";
import PageHeader from "../components/layout/PageHeader";
import Alert from "../components/ui/Alert";
import Button from "../components/ui/Button";
import TextField from "../components/ui/TextField";
import { useAuth } from "../hooks/useAuth";
import * as userApi from "../api/userApi";
import type { User } from "../types/auth";

type ProfileValues = Pick<User, "firstName" | "lastName" | "username" | "email">;

function ProfileForm({ user }: { user: User | null }) {
  const [form, setForm] = useState<ProfileValues>({
    firstName: user?.firstName ?? "",
    lastName: user?.lastName ?? "",
    username: user?.username ?? "",
    email: user?.email ?? "",
  });
  const [saved, setSaved] = useState(false);
  const [error, setError] = useState("");

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setError("");
    setSaved(false);
    try {
      await userApi.updateMe(form);
      setSaved(true);
    } catch {
      setError("Could not update your profile. Please try again.");
    }
  };

  return (
    <div className="mx-auto max-w-3xl">
      <PageHeader title="Your profile" subtitle="Keep your personal details up to date." />

      <div className="rounded-2xl border border-white/10 bg-ink-900/70 p-5 shadow-[0_24px_60px_-40px_rgb(0_0_0/0.9)] sm:p-7">
        <div className="mb-6 flex items-center gap-4 border-b border-white/10 pb-6">
          <div className="grid size-14 shrink-0 place-items-center rounded-2xl border border-accent/25 bg-accent/10 text-xl font-semibold text-accent">
            {user?.firstName?.[0] ?? "U"}
          </div>
          <div className="min-w-0">
            <p className="truncate text-lg font-semibold text-white">
              {user?.firstName} {user?.lastName}
            </p>
            <p className="mt-0.5 text-sm text-ink-400">
              {user?.role === "ADMIN" ? "Workspace admin" : "Team member"}
              {user?.teamName ? ` · ${user.teamName}` : ""}
            </p>
          </div>
        </div>

        {error && (
          <div className="mb-5">
            <Alert>{error}</Alert>
          </div>
        )}
        {saved && (
          <div className="mb-5">
            <Alert variant="success">Profile saved.</Alert>
          </div>
        )}

        <form onSubmit={submit} className="grid gap-5 sm:grid-cols-2">
          <TextField
            id="firstName"
            label="First name"
            value={form.firstName}
            onChange={(e) => setForm({ ...form, firstName: e.target.value })}
          />
          <TextField
            id="lastName"
            label="Last name"
            value={form.lastName}
            onChange={(e) => setForm({ ...form, lastName: e.target.value })}
          />
          <TextField
            id="username"
            label="Username"
            value={form.username}
            onChange={(e) => setForm({ ...form, username: e.target.value })}
          />
          <TextField
            id="email"
            type="email"
            label="Email"
            value={form.email}
            onChange={(e) => setForm({ ...form, email: e.target.value })}
          />
          <div className="border-t border-white/10 pt-5 sm:col-span-2">
            <Button fullWidth={false}>Save changes</Button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default function ProfilePage() {
  const { user } = useAuth();
  return <ProfileForm key={user?.id ?? 0} user={user} />;
}
