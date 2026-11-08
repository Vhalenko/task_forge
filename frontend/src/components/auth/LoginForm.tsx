import { useState, type ChangeEvent, type FormEvent } from "react";
import { useLocation } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth";
import { parseApiError, type FieldErrors } from "../../utils/apiError";
import { validateLogin, type LoginValues } from "../../utils/validation";
import type { AuthLocationState } from "../../types/auth";
import TextField from "../ui/TextField";
import Button from "../ui/Button";
import Alert from "../ui/Alert";

export default function LoginForm() {
  const { login } = useAuth();
  const location = useLocation();
  const state = location.state as AuthLocationState | null;

  const [values, setValues] = useState<LoginValues>({ email: "", password: "" });
  const [errors, setErrors] = useState<FieldErrors>({});
  const [formError, setFormError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setValues((prev) => ({ ...prev, [name]: value }));
    setErrors((prev) => ({ ...prev, [name]: undefined }));
  };

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setFormError("");

    const found = validateLogin(values);
    setErrors(found);
    if (Object.keys(found).length > 0) return;

    setLoading(true);
    try {
      await login(values.email.trim(), values.password);
    } catch (err) {
      const { message, fieldErrors } = parseApiError(err);
      setFormError(message);
      setErrors(fieldErrors);
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} noValidate className="space-y-5">
      {state?.registered && <Alert variant="success">Account created. You can sign in now.</Alert>}
      <Alert>{formError}</Alert>

      <TextField
        id="email"
        name="email"
        type="email"
        label="Email"
        autoComplete="email"
        value={values.email}
        onChange={handleChange}
        error={errors.email}
      />
      <TextField
        id="password"
        name="password"
        type="password"
        label="Password"
        autoComplete="current-password"
        value={values.password}
        onChange={handleChange}
        error={errors.password}
      />

      <Button type="submit" loading={loading}>
        Sign in
      </Button>
    </form>
  );
}
