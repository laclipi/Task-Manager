import { useEffect, useState } from "react";

import { getTasks } from "../services/taskService";

function TasksPage() {
  const [tasks, setTasks] = useState([]);

  useEffect(() => {
    async function loadTasks() {
      try {
        const data = await getTasks();

        setTasks(data);

        console.log("Tasks:", data);
      } catch (error) {
        console.error("Failed to load tasks:", error);
      }
    }

    loadTasks();
  }, []);

  return (
    <main>
      <h1>Tasks</h1>

      {tasks.length === 0 ? (
        <p>No tasks yet</p>
      ) : (
        <ul>
          {tasks.map((task) => (
            <li key={task.id}>{task.title}</li>
          ))}
        </ul>
      )}
    </main>
  );
}

export default TasksPage;
