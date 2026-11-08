import { Link } from "react-router-dom";
import AuthLayout from "../components/auth/AuthLayout";
import LoginForm from "../components/auth/LoginForm";

export default function LoginPage() {
  return (
    <AuthLayout
      title="Sign in"
      subtitle="Welcome back. Enter your details to continue."
      footer={
        <>
          No account yet?{" "}
          <Link to="/register" className="font-medium text-accent underline underline-offset-4 hover:text-accent-strong">
            Create one
          </Link>
        </>
      }
    >
      <LoginForm />
    </AuthLayout>
  );
}
