import type { Task } from "../../types/task";
import TaskRow from "./TaskRow";

export default function TaskList({ tasks }: { tasks: Task[] }) {
  return (
    <ul className="space-y-3">
      {tasks.map((task) => (
        <TaskRow key={task.id} task={task} />
      ))}
    </ul>
  );
}
