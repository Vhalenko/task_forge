
import { useLocation, useNavigate } from 'react-router'
import Navbar from '../components/layout/Navbar'
import AuthForm, {
  type AuthMode,
  type AuthFormData,
} from '../components/auth/AuthForm'
import { registerUser } from '../api/auth'

function AuthPage() {
  const location = useLocation()
  const navigate = useNavigate()

  const mode: AuthMode =
    location.pathname === '/register' ? 'register' : 'login'

  const isLogin = mode === 'login'

  async function handleAuthSubmit(data: AuthFormData) {
    if (isLogin) {
      throw new Error('Login is not connected yet.')
    }

    await registerUser({
      firstName: data.firstName,
      lastName: data.lastName,
      username: data.username,
      email: data.email,
      password: data.password,
    })
  }

  return (
    <main className="min-h-screen bg-[#0B1020] text-white">
      <Navbar />

      <section className="flex min-h-[calc(100vh-88px)] items-center justify-center px-4 py-10">
        <div className="w-full max-w-2xl">
          <div className="rounded-2xl border border-slate-800 bg-[#131B30] p-6 shadow-2xl shadow-black/20 sm:p-8">
            <header className="mb-6">
              <h1 className="text-2xl font-bold tracking-tight">
                {isLogin ? 'Welcome back' : 'Create your account'}
              </h1>

              <p className="mt-2 text-sm text-slate-400">
                {isLogin
                  ? 'Sign in to continue to your workspace.'
                  : 'Get started with TaskForge today.'}
              </p>
            </header>

            <AuthForm
              mode={mode}
              onSubmit={handleAuthSubmit}
            />

            <p className="mt-6 text-center text-sm text-slate-400">
              {isLogin
                ? "Don't have an account?"
                : 'Already have an account?'}{' '}

              <button
                type="button"
                onClick={() =>
                  navigate(isLogin ? '/register' : '/login')
                }
                className="font-semibold text-violet-400 transition hover:text-violet-300"
              >
                {isLogin ? 'Create account' : 'Sign in'}
              </button>
            </p>
          </div>

          <p className="mt-6 text-center text-xs text-slate-600">
            TaskForge · Organize work. Get things done.
          </p>
        </div>
      </section>
    </main>
  )
}

export default AuthPage
