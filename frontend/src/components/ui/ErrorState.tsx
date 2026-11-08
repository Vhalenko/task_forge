import Alert from "./Alert";
import Button from "./Button";

interface ErrorStateProps {
  message: string;
  onRetry: () => void;
}

export default function ErrorState({ message, onRetry }: ErrorStateProps) {
  return (
    <div className="space-y-3">
      <Alert>{message}</Alert>
      <Button type="button" variant="secondary" fullWidth={false} onClick={onRetry}>
        Try again
      </Button>
    </div>
  );
}
