import { useState } from "react";
import { login } from "../services/authService";

function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  return (
    <main>
      <h1>Task Manager</h1>
      <h2>Login</h2>

      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="email">Email</label>
          <input
            id="email"
            type="email"
            placeholder="tu@email.com"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
          />
        </div>

        <div>
          <label htmlFor="password">Password</label>
          <input
            id="password"
            type="password"
            placeholder="Tu contraseña"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
          />
        </div>

        <button type="submit">Login</button>
      </form>
    </main>
  );
  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();

    try {
      const token = await login(email, password);

      localStorage.setItem("token", token);

      console.log("JWT guardado");

      console.log("JWT:", token);
    } catch (error) {
      console.error("Login failed:", error);
    }
  }
}

export default LoginPage;
