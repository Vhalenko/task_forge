import PageHeader from "../components/layout/PageHeader";
import TaskBrowser from "../components/tasks/TaskBrowser";
import LinkButton from "../components/ui/LinkButton";
import { useMyTasks } from "../hooks/useMyTasks";

export default function TasksPage() {
  const { tasks, loading, error, reload } = useMyTasks();

  return (
    <div>
      <PageHeader title="My tasks" action={<LinkButton to="/tasks/new">New task</LinkButton>} />
      <TaskBrowser
        tasks={tasks}
        loading={loading}
        error={error}
        onRetry={reload}
        emptyTitle="No tasks yet"
        emptyDescription="Tasks assigned to you will show up here."
      />
    </div>
  );
}
