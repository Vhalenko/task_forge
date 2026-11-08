import { Link } from "react-router-dom";
import AuthLayout from "../components/auth/AuthLayout";
import RegisterForm from "../components/auth/RegisterForm";

export default function RegisterPage() {
  return (
    <AuthLayout
      title="Create an account"
      subtitle="Start organizing your team's work."
      footer={
        <>
          Already registered?{" "}
          <Link to="/login" className="font-medium text-accent underline underline-offset-4 hover:text-accent-strong">
            Sign in
          </Link>
        </>
      }
    >
      <RegisterForm />
    </AuthLayout>
  );
}
