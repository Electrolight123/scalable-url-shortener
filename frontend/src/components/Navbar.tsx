import { Link, useNavigate } from "react-router-dom";
import { LinkIcon, LogOut } from "lucide-react";

function Navbar() {
  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  const logout = () => {
    localStorage.removeItem("token");
    navigate("/login");
  };

  return (
    <nav className="navbar">
      <Link to="/dashboard" className="brand">
        <LinkIcon size={24} />
        <span>Scalable URL Shortener</span>
      </Link>

      <div className="nav-links">
        {token ? (
          <>
            <Link to="/dashboard">Dashboard</Link>
            <Link to="/create">Create URL</Link>
            <button onClick={logout} className="logout-btn">
              <LogOut size={16} />
              Logout
            </button>
          </>
        ) : (
          <>
            <Link to="/login">Login</Link>
            <Link to="/register">Register</Link>
          </>
        )}
      </div>
    </nav>
  );
}

export default Navbar;