import Navbar from '../components/layout/Navbar'
import { Link } from 'react-router'

const features = [
  {
    number: '01',
    title: 'Organize tasks',
    description:
      'Keep your work organized and know what needs your attention.',
  },
  {
    number: '02',
    title: 'Track progress',
    description:
      'See what is in progress, what is completed, and what comes next.',
  },
  {
    number: '03',
    title: 'Work together',
    description:
      'Assign tasks and keep project discussions in one place.',
  },
]

function HomePage() {
  return (
    <main className="min-h-screen bg-[#0B1020] text-white">
        <Navbar />

      <section className="mx-auto max-w-6xl px-6 pb-24 pt-20 sm:pt-28">
        <div className="max-w-3xl">
          <span className="inline-flex rounded-full border border-violet-500/30 bg-violet-500/10 px-4 py-2 text-sm text-violet-300">
            Your productivity workspace
          </span>

          <h1 className="mt-8 text-5xl font-bold leading-tight tracking-tight sm:text-7xl">
            Turn your plans
            <br />
            into <span className="text-violet-400">progress.</span>
          </h1>

          <p className="mt-6 max-w-2xl text-lg leading-8 text-slate-400">
            TaskForge helps you organize tasks, track progress,
            and stay focused on the work that matters.
          </p>

          <div className="mt-9 flex flex-wrap gap-4">
            <Link
              to="/register"
              className="rounded-lg bg-violet-600 px-6 py-3 font-semibold transition hover:bg-violet-500"
            >
              Get started for free →
            </Link>

            <a
              href="#features"
              className="rounded-lg border border-slate-700 px-6 py-3 font-semibold transition hover:border-slate-500 hover:bg-slate-800/50"
            >
              Explore features
            </a>
          </div>
        </div>

        <section id="features" className="mt-28">
          <div className="mb-8">
            <h2 className="text-3xl font-bold">
              Everything in one place
            </h2>
            <p className="mt-3 text-slate-400">
              Simple tools to help you stay in control of your work.
            </p>
          </div>

          <div className="grid gap-5 md:grid-cols-3">
            {features.map((feature) => (
              <article
                key={feature.number}
                className="rounded-2xl border border-slate-800 bg-[#131B30] p-6 transition hover:border-violet-500/50"
              >
                <span className="text-sm font-semibold text-violet-400">
                  {feature.number}
                </span>

                <h3 className="mt-5 text-xl font-semibold">
                  {feature.title}
                </h3>

                <p className="mt-3 leading-7 text-slate-400">
                  {feature.description}
                </p>
              </article>
            ))}
          </div>
        </section>
      </section>

      <footer className="border-t border-slate-800 px-6 py-6 text-center text-sm text-slate-500">
        © {new Date().getFullYear()} TaskForge. Built to get things done.
      </footer>
    </main>
  )
}

export default HomePage
