import { useEffect, useState } from "react";

import { createTask, getTasks } from "../services/taskService";

function TasksPage() {
  const [tasks, setTasks] = useState([]);
  const [title, setTitle] = useState("");

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

  async function handleCreateTask(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();

    try {
      const newTask = await createTask(title);

      setTasks([...tasks, newTask]);
      setTitle("");

      console.log("Created task:", newTask);
    } catch (error) {
      console.error("Failed to create task:", error);
    }
  }

  return (
    <main>
      <h1>Tasks</h1>

      <form onSubmit={handleCreateTask}>
        <input
          type="text"
          placeholder="Nueva tarea"
          value={title}
          onChange={(event) => setTitle(event.target.value)}
        />

        <button type="submit">Create task</button>
      </form>

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
