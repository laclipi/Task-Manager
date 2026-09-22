export async function getTasks() {
  const token = localStorage.getItem("token");

  const response = await fetch("http://localhost:8080/tasks", {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!response.ok) {
    throw new Error("Failed to load tasks");
  }

  return response.json();
}