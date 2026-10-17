
import { Link } from 'react-router'

function Navbar() {
  return (
    <nav className="mx-auto flex max-w-6xl items-center justify-between px-6 py-6">
      <Link to="/" className="flex items-center gap-3">
        <span className="flex size-10 items-center justify-center rounded-xl bg-violet-600 font-bold">
          T
        </span>
        <span className="text-xl font-bold">TaskForge</span>
      </Link>

      <div className="flex items-center gap-5">
        <Link
          to="/login"
          className="text-sm text-slate-300 transition hover:text-white"
        >
          Sign in
        </Link>

        <Link
          to="/register"
          className="rounded-lg bg-violet-600 px-4 py-2.5 text-sm font-semibold transition hover:bg-violet-500"
        >
          Get started
        </Link>
      </div>
    </nav>
  )
}

export default Navbar
