import { isAxiosError } from "axios";

export type FieldErrors = Record<string, string | undefined>;

export interface ParsedApiError {
  message: string;
  fieldErrors: FieldErrors;
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === "object" && value !== null && !Array.isArray(value);
}

const FALLBACK_MESSAGES: Record<number, string> = {
  401: "Incorrect email or password.",
  403: "You do not have access to this.",
  409: "This email is already registered.",
};

export function parseApiError(error: unknown): ParsedApiError {
  if (!isAxiosError(error)) {
    return { message: "Something went wrong. Please try again.", fieldErrors: {} };
  }
  if (!error.response) {
    return { message: "Cannot reach the server. Please try again.", fieldErrors: {} };
  }

  const status = error.response.status;
  const data: unknown = error.response.data;

  if (typeof data === "string" && data) {
    return { message: data, fieldErrors: {} };
  }

  if (isRecord(data)) {
    // validation errors: { fieldName: "message", ... }
    if (status === 400 && !("status" in data) && !("message" in data)) {
      const fieldErrors: FieldErrors = {};
      for (const [key, value] of Object.entries(data)) {
        if (typeof value === "string") fieldErrors[key] = value;
      }
      return { message: "Please fix the highlighted fields.", fieldErrors };
    }
    if (typeof data.message === "string" && data.message) {
      return { message: data.message, fieldErrors: {} };
    }
  }

  return {
    message: FALLBACK_MESSAGES[status] ?? "Something went wrong. Please try again.",
    fieldErrors: {},
  };
}
