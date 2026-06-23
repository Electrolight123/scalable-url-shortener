import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Copy, ExternalLink, BarChart3, Trash2 } from "lucide-react";
import api, { API_BASE_URL } from "../api/api";

type UrlItem = {
  id: number;
  originalUrl: string;
  shortCode: string;
  shortUrl?: string;
  active?: boolean;
  createdAt?: string;
};

function Dashboard() {
  const [urls, setUrls] = useState<UrlItem[]>([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  const fetchUrls = async () => {
    setError("");
    setLoading(true);

    try {
      const response = await api.get("/api/urls/my");
      setUrls(response.data);
    } catch (err) {
      setError("Unable to fetch URLs.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUrls();
  }, []);

  const copyUrl = async (shortCode: string, shortUrl?: string) => {
    const finalUrl = shortUrl || `${API_BASE_URL}/${shortCode}`;
    await navigator.clipboard.writeText(finalUrl);
    alert("Short URL copied.");
  };

  const deleteUrl = async (id: number) => {
    const confirmDelete = confirm("Do you want to deactivate this URL?");

    if (!confirmDelete) return;

    try {
      await api.delete(`/api/urls/${id}`);
      fetchUrls();
    } catch (err) {
      alert("Unable to delete URL.");
    }
  };

  return (
    <section>
      <div className="page-header">
        <div>
          <h1>My URLs</h1>
          <p>Manage your shortened links and analytics.</p>
        </div>

        <Link to="/create" className="primary-link">
          Create New URL
        </Link>
      </div>

      {error && <div className="error">{error}</div>}
      {loading && <p>Loading URLs...</p>}

      {!loading && urls.length === 0 && (
        <div className="empty-card">
          <h2>No URLs yet</h2>
          <p>Create your first short URL.</p>
          <Link to="/create" className="primary-link">
            Create URL
          </Link>
        </div>
      )}

      <div className="url-list">
        {urls.map((url) => {
          const shortUrl = url.shortUrl || `${API_BASE_URL}/${url.shortCode}`;

          return (
            <div className="url-card" key={url.id}>
              <div>
                <h3>{url.shortCode}</h3>
                <p className="original-url">{url.originalUrl}</p>
                <a href={shortUrl} target="_blank" rel="noreferrer">
                  {shortUrl}
                </a>
              </div>

              <div className="actions">
                <button onClick={() => copyUrl(url.shortCode, url.shortUrl)}>
                  <Copy size={16} />
                  Copy
                </button>

                <a href={shortUrl} target="_blank" rel="noreferrer">
                  <ExternalLink size={16} />
                  Open
                </a>

                <Link to={`/analytics/${url.shortCode}`}>
                  <BarChart3 size={16} />
                  Analytics
                </Link>

                <button className="danger" onClick={() => deleteUrl(url.id)}>
                  <Trash2 size={16} />
                  Delete
                </button>
              </div>
            </div>
          );
        })}
      </div>
    </section>
  );
}

export default Dashboard;