import { useState, type ChangeEvent, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth";
import { parseApiError, type FieldErrors } from "../../utils/apiError";
import { validateRegister, type RegisterValues } from "../../utils/validation";
import TextField from "../ui/TextField";
import Button from "../ui/Button";
import Alert from "../ui/Alert";

const INITIAL_VALUES: RegisterValues = {
  firstName: "",
  lastName: "",
  username: "",
  email: "",
  password: "",
  confirmPassword: "",
};

export default function RegisterForm() {
  const { register } = useAuth();
  const navigate = useNavigate();

  const [values, setValues] = useState<RegisterValues>(INITIAL_VALUES);
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

    const found = validateRegister(values);
    setErrors(found);
    if (Object.keys(found).length > 0) return;

    setLoading(true);
    try {
      await register({
        firstName: values.firstName.trim(),
        lastName: values.lastName.trim(),
        username: values.username.trim(),
        email: values.email.trim(),
        password: values.password,
      });
      navigate("/login", { replace: true, state: { registered: true } });
    } catch (err) {
      const { message, fieldErrors } = parseApiError(err);
      setFormError(message);
      setErrors(fieldErrors);
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} noValidate className="space-y-5">
      <Alert>{formError}</Alert>

      <div className="grid grid-cols-1 gap-5 sm:grid-cols-2">
        <TextField
          id="firstName"
          name="firstName"
          label="First name"
          autoComplete="given-name"
          value={values.firstName}
          onChange={handleChange}
          error={errors.firstName}
        />
        <TextField
          id="lastName"
          name="lastName"
          label="Last name"
          autoComplete="family-name"
          value={values.lastName}
          onChange={handleChange}
          error={errors.lastName}
        />
      </div>

      <TextField
        id="username"
        name="username"
        label="Username"
        autoComplete="username"
        value={values.username}
        onChange={handleChange}
        error={errors.username}
      />
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
        autoComplete="new-password"
        value={values.password}
        onChange={handleChange}
        error={errors.password}
      />
      <TextField
        id="confirmPassword"
        name="confirmPassword"
        type="password"
        label="Confirm password"
        autoComplete="new-password"
        value={values.confirmPassword}
        onChange={handleChange}
        error={errors.confirmPassword}
      />

      <Button type="submit" loading={loading}>
        Create account
      </Button>
    </form>
  );
}
