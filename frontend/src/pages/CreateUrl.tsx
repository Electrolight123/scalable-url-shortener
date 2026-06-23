import { useState } from "react";
import type { FormEvent } from "react";
import { Link } from "react-router-dom";
import api from "../api/api";

function CreateUrl() {
  const [originalUrl, setOriginalUrl] = useState("https://github.com");
  const [customAlias, setCustomAlias] = useState("");
  const [result, setResult] = useState<any>(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const createUrl = async (event: FormEvent) => {
    event.preventDefault();
    setError("");
    setResult(null);
    setLoading(true);

    try {
      const response = await api.post("/api/urls", {
        originalUrl,
        customAlias: customAlias.trim() || null,
      });

      setResult(response.data);
    } catch (err) {
      setError("Unable to create short URL. Custom alias may already exist.");
    } finally {
      setLoading(false);
    }
  };

  const copyResult = async () => {
    if (!result?.shortUrl) return;
    await navigator.clipboard.writeText(result.shortUrl);
    alert("Short URL copied.");
  };

  return (
    <section className="auth-card large-card">
      <h1>Create Short URL</h1>
      <p>Generate a short link for any valid URL.</p>

      {error && <div className="error">{error}</div>}

      <form onSubmit={createUrl}>
        <label>Original URL</label>
        <input
          value={originalUrl}
          onChange={(event) => setOriginalUrl(event.target.value)}
          placeholder="https://example.com"
          required
        />

        <label>Custom Alias Optional</label>
        <input
          value={customAlias}
          onChange={(event) => setCustomAlias(event.target.value)}
          placeholder="my-custom-link"
        />

        <button type="submit" disabled={loading}>
          {loading ? "Creating..." : "Create Short URL"}
        </button>
      </form>

      {result && (
        <div className="result-card">
          <h2>Short URL Created</h2>
          <p>
            <strong>Short Code:</strong> {result.shortCode}
          </p>
          <p>
            <strong>Short URL:</strong>{" "}
            <a href={result.shortUrl} target="_blank" rel="noreferrer">
              {result.shortUrl}
            </a>
          </p>

          <button onClick={copyResult}>Copy Short URL</button>
        </div>
      )}

      <p className="small-text">
        <Link to="/dashboard">Back to dashboard</Link>
      </p>
    </section>
  );
}

export default CreateUrl;