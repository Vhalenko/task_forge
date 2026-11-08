import type { FieldErrors } from "./apiError";

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export interface LoginValues {
  email: string;
  password: string;
}

export interface RegisterValues {
  firstName: string;
  lastName: string;
  username: string;
  email: string;
  password: string;
  confirmPassword: string;
}

export function validateLogin(v: LoginValues): FieldErrors {
  const errors: FieldErrors = {};
  if (!v.email.trim()) errors.email = "Email is required";
  else if (!EMAIL_PATTERN.test(v.email.trim())) errors.email = "Enter a valid email address";
  if (!v.password) errors.password = "Password is required";
  return errors;
}

export function validateRegister(v: RegisterValues): FieldErrors {
  const errors: FieldErrors = {};
  if (!v.firstName.trim()) errors.firstName = "First name is required";
  if (!v.lastName.trim()) errors.lastName = "Last name is required";
  if (!v.username.trim()) errors.username = "Username is required";

  if (!v.email.trim()) errors.email = "Email is required";
  else if (!EMAIL_PATTERN.test(v.email.trim())) errors.email = "Enter a valid email address";

  if (!v.password) errors.password = "Password is required";
  else if (v.password.length < 8) errors.password = "Use at least 8 characters";

  if (!v.confirmPassword) errors.confirmPassword = "Please confirm your password";
  else if (v.confirmPassword !== v.password) errors.confirmPassword = "Passwords do not match";

  return errors;
}
