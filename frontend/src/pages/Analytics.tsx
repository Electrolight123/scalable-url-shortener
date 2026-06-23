import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import api from "../api/api";

function Analytics() {
  const { shortCode } = useParams();
  const [analytics, setAnalytics] = useState<any>(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchAnalytics = async () => {
      setError("");
      setLoading(true);

      try {
        const response = await api.get(`/api/analytics/${shortCode}`);
        setAnalytics(response.data);
      } catch (err) {
        setError("Unable to fetch analytics.");
      } finally {
        setLoading(false);
      }
    };

    fetchAnalytics();
  }, [shortCode]);

  return (
    <section>
      <div className="page-header">
        <div>
          <h1>Analytics</h1>
          <p>Short code: {shortCode}</p>
        </div>

        <Link to="/dashboard" className="primary-link">
          Back to Dashboard
        </Link>
      </div>

      {loading && <p>Loading analytics...</p>}
      {error && <div className="error">{error}</div>}

      {analytics && (
        <div className="analytics-grid">
          <div className="stat-card">
            <h2>{analytics.shortCode || shortCode}</h2>
            <p>Short Code</p>
          </div>

          <div className="stat-card">
            <h2>{analytics.totalClicks ?? analytics.clickCount ?? 0}</h2>
            <p>Total Clicks</p>
          </div>

          <div className="stat-card">
            <h2>{analytics.originalUrl ? "Available" : "N/A"}</h2>
            <p>Original URL</p>
          </div>
        </div>
      )}

      {analytics?.recentClicks && analytics.recentClicks.length > 0 && (
        <div className="table-card">
          <h2>Recent Clicks</h2>

          <table>
            <thead>
              <tr>
                <th>Clicked At</th>
                <th>IP Address</th>
                <th>User Agent</th>
              </tr>
            </thead>

            <tbody>
              {analytics.recentClicks.map((click: any, index: number) => (
                <tr key={index}>
                  <td>{click.clickedAt || click.createdAt || "N/A"}</td>
                  <td>{click.ipAddress || "N/A"}</td>
                  <td>{click.userAgent || "N/A"}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}

export default Analytics;