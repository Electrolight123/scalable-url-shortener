import { useState } from "react";
import type { FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../api/api";

function Login() {
  const navigate = useNavigate();

  const [email, setEmail] = useState("abhishek@example.com");
  const [password, setPassword] = useState("password123");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleLogin = async (event: FormEvent) => {
    event.preventDefault();
    setError("");
    setLoading(true);

    try {
      const response = await api.post("/api/auth/login", {
        email,
        password,
      });

      const token = response.data.token || response.data.jwt || response.data.accessToken;

      if (!token) {
        throw new Error("Token not found in login response");
      }

      localStorage.setItem("token", token);
      navigate("/dashboard");
    } catch (err) {
      setError("Login failed. Check email and password.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="auth-card">
      <h1>Login</h1>
      <p>Login to manage your short URLs.</p>

      {error && <div className="error">{error}</div>}

      <form onSubmit={handleLogin}>
        <label>Email</label>
        <input
          type="email"
          value={email}
          onChange={(event) => setEmail(event.target.value)}
          required
        />

        <label>Password</label>
        <input
          type="password"
          value={password}
          onChange={(event) => setPassword(event.target.value)}
          required
        />

        <button type="submit" disabled={loading}>
          {loading ? "Logging in..." : "Login"}
        </button>
      </form>

      <p className="small-text">
        New user? <Link to="/register">Register here</Link>
      </p>
    </section>
  );
}

export default Login;