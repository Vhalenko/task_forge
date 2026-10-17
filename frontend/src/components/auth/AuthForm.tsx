
import { useState } from 'react'
import type { FormEvent } from 'react'

export type AuthMode = 'login' | 'register'

export type AuthFormData = {
  firstName: string
  lastName: string
  username: string
  email: string
  password: string
  confirmPassword: string
}

type AuthFormProps = {
  mode: AuthMode
  onSubmit: (data: AuthFormData) => void | Promise<void>
}

function AuthForm({ mode, onSubmit }: AuthFormProps) {
  const isLogin = mode === 'login'

  const [firstName, setFirstName] = useState('')
  const [lastName, setLastName] = useState('')
  const [username, setUsername] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [error, setError] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')

    if (isSubmitting) return

    if (!isLogin && password !== confirmPassword) {
      setError('Passwords do not match.')
      return
    }

    setIsSubmitting(true)

    try {
      await onSubmit({
        firstName,
        lastName,
        username,
        email,
        password,
        confirmPassword,
      })
    } catch {
      setError('Something went wrong. Please try again.')
    } finally {
      setIsSubmitting(false)
    }
  }

  const inputClass =
    'w-full min-w-0 rounded-lg border border-slate-700 bg-[#0B1020] px-4 py-3 text-sm text-white outline-none transition placeholder:text-slate-600 focus:border-violet-500 focus:ring-2 focus:ring-violet-500/20'

  const labelClass =
    'mb-2 block text-sm font-medium text-slate-200'

  return (
    <form onSubmit={handleSubmit} className="space-y-5">
      {!isLogin && (
        <>
          {/* First name and last name */}
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
            <div>
              <label htmlFor="firstName" className={labelClass}>
                First name
              </label>

              <input
                id="firstName"
                name="firstName"
                type="text"
                autoComplete="given-name"
                required
                value={firstName}
                onChange={(event) => setFirstName(event.target.value)}
                placeholder="John"
                className={inputClass}
              />
            </div>

            <div>
              <label htmlFor="lastName" className={labelClass}>
                Last name
              </label>

              <input
                id="lastName"
                name="lastName"
                type="text"
                autoComplete="family-name"
                required
                value={lastName}
                onChange={(event) => setLastName(event.target.value)}
                placeholder="Doe"
                className={inputClass}
              />
            </div>
          </div>

          {/* Username */}
          <div>
            <label htmlFor="username" className={labelClass}>
              Username
            </label>

            <input
              id="username"
              name="username"
              type="text"
              autoComplete="username"
              minLength={3}
              required
              value={username}
              onChange={(event) => setUsername(event.target.value)}
              placeholder="john_doe"
              className={inputClass}
            />
          </div>
        </>
      )}

      {/* Email */}
      <div>
        <label htmlFor="email" className={labelClass}>
          Email address
        </label>

        <input
          id="email"
          name="email"
          type="email"
          autoComplete="email"
          required
          value={email}
          onChange={(event) => setEmail(event.target.value)}
          placeholder="you@example.com"
          className={inputClass}
        />
      </div>


      <div className={`grid grid-cols-1 gap-4 ${!isLogin ? 'sm:grid-cols-2' : ''}`}>
        <div>
          <label htmlFor="password" className={labelClass}>
            Password
          </label>

          <input
            id="password"
            name="password"
            type="password"
            autoComplete={isLogin ? 'current-password' : 'new-password'}
            minLength={8}
            required
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            placeholder="At least 8 characters"
            className={inputClass}
          />
        </div>

        {!isLogin && (
          <div>
            <label htmlFor="confirmPassword" className={labelClass}>
              Confirm password
            </label>

            <input
              id="confirmPassword"
              name="confirmPassword"
              type="password"
              autoComplete="new-password"
              minLength={8}
              required
              value={confirmPassword}
              onChange={(event) => setConfirmPassword(event.target.value)}
              placeholder="Repeat password"
              className={inputClass}
            />
          </div>
        )}
      </div>

      {/* Error message */}
      {error && (
        <p role="alert" className="text-sm text-red-400">
          {error}
        </p>
      )}

      {/* Submit button */}
      <button
        type="submit"
        disabled={isSubmitting}
        className="w-full rounded-lg bg-violet-600 px-4 py-3 font-semibold transition hover:bg-violet-500 disabled:cursor-not-allowed disabled:opacity-60"
      >
        {isSubmitting
          ? 'Please wait...'
          : isLogin
            ? 'Sign in'
            : 'Create account'}
      </button>
    </form>
  )
}

export default AuthForm
