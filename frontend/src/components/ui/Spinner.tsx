export default function Spinner() {
  return (
    <div className="flex justify-center py-16" role="status">
      <div className="size-6 animate-spin rounded-full border-2 border-white/15 border-t-accent" />
      <span className="sr-only">Loading</span>
    </div>
  );
}
