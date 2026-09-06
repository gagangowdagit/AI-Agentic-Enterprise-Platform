import { useEffect, useState } from 'react';
import { Routes, Route, Link, useLocation, useNavigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import HomePage from './pages/HomePage';
import ProjectsPage from './pages/ProjectsPage';
import ProjectDetailsPage from './pages/ProjectDetailsPage';
import DocumentsPage from './pages/DocumentsPage';
import DepartmentsPage from './pages/DepartmentsPage';
import NovaAIPage from './pages/NovaAIPage';
import AnalyticsPage from './pages/AnalyticsPage';

interface AuthUser {
  name: string;
  email: string;
}

function App() {
  const location = useLocation();
  const navigate = useNavigate();
  const [profileOpen, setProfileOpen] = useState(false);
  const [user, setUser] = useState<AuthUser | null>(null);
  const [darkMode, setDarkMode] = useState(() => localStorage.getItem('rag-theme') === 'dark');
  const isHomePage = location.pathname === '/home';
  const isProjectDetailsPage = location.pathname.startsWith('/projects/');
  const isDetailPage = location.pathname === '/projects'
    || isProjectDetailsPage
    || ['/departments', '/documents', '/nova-ai', '/analytics'].includes(location.pathname);

  useEffect(() => {
    const storedUser = localStorage.getItem('rag-auth-user');
    if (!storedUser) {
      return;
    }

    try {
      setUser(JSON.parse(storedUser) as AuthUser);
    } catch {
      localStorage.removeItem('rag-auth-user');
    }
  }, [location.pathname]);

  useEffect(() => {
    localStorage.setItem('rag-theme', darkMode ? 'dark' : 'light');
    document.documentElement.classList.toggle('dark-mode', darkMode);
  }, [darkMode]);

  const handleLogout = () => {
    localStorage.removeItem('rag-auth-user');
    setUser(null);
    setProfileOpen(false);
    navigate('/login');
  };

  return (
    <div className={darkMode ? 'app-shell dark-mode' : 'app-shell'}>
      {(isHomePage || isDetailPage) && !isProjectDetailsPage && (
        <nav style={{ display: 'flex', justifyContent: isHomePage ? 'flex-end' : 'space-between', alignItems: 'center', marginBottom: '20px', padding: '10px 16px', borderBottom: '1px solid #ccc' }}>
          {!isHomePage && (
            <Link
              to="/home"
              style={{
                display: 'inline-flex',
                alignItems: 'center',
                padding: '10px 16px',
                borderRadius: '6px',
                backgroundColor: '#2563eb',
                color: 'white',
                fontWeight: '600',
                textDecoration: 'none',
                cursor: 'pointer',
              }}
            >
              Back to Home
            </Link>
          )}
          <div style={{ position: 'relative' }}>
            <button
              type="button"
              onClick={() => setProfileOpen((open) => !open)}
              aria-expanded={profileOpen}
              className="profile-trigger"
              style={{ padding: '10px 14px', border: '1px solid #cbd5e1', borderRadius: '6px', backgroundColor: 'white', color: '#172033', fontSize: '15px', fontWeight: '600', cursor: 'pointer' }}
            >
              Profile
            </button>
            {profileOpen && (
              <div className="profile-menu" style={{ position: 'absolute', right: 0, top: 'calc(100% + 8px)', zIndex: 10, minWidth: '220px', padding: '14px', border: '1px solid #dbe3ef', borderRadius: '8px', backgroundColor: 'white', boxShadow: '0 8px 24px rgba(15, 23, 42, 0.15)' }}>
                {user ? (
                  <>
                    <strong style={{ display: 'block', marginBottom: '4px', color: '#172033' }}>{user.name}</strong>
                    <span style={{ display: 'block', marginBottom: '14px', color: '#64748b', fontSize: '14px' }}>{user.email}</span>
                    <button
                      type="button"
                      onClick={handleLogout}
                      style={{ width: '100%', padding: '9px 12px', border: 0, borderRadius: '6px', backgroundColor: '#dc2626', color: 'white', fontWeight: '600', cursor: 'pointer' }}
                    >
                      Logout
                    </button>
                  </>
                ) : (
                  <>
                    <span style={{ display: 'block', marginBottom: '14px', color: '#64748b', fontSize: '14px' }}>No profile loaded</span>
                    <button
                      type="button"
                      onClick={handleLogout}
                      style={{ width: '100%', padding: '9px 12px', border: 0, borderRadius: '6px', backgroundColor: '#2563eb', color: 'white', fontWeight: '600', cursor: 'pointer' }}
                    >
                      Logout
                    </button>
                  </>
                )}
                <div className="theme-toggle-row">
                  <span>{darkMode ? 'Dark mode' : 'Light mode'}</span>
                  <button
                    type="button"
                    className={darkMode ? 'theme-toggle is-dark' : 'theme-toggle'}
                    onClick={() => setDarkMode((enabled) => !enabled)}
                    aria-label={`Switch to ${darkMode ? 'light' : 'dark'} mode`}
                    aria-pressed={darkMode}
                  >
                    <span />
                  </button>
                </div>
              </div>
            )}
          </div>
        </nav>
      )}

      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<RegisterPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/home" element={<HomePage />} />
        <Route path="/projects" element={<ProjectsPage />} />
        <Route path="/projects/:projectId" element={<ProjectDetailsPage />} />
        <Route path="/documents" element={<DocumentsPage />} />
        <Route path="/departments" element={<DepartmentsPage />} />
        <Route path="/nova-ai" element={<NovaAIPage />} />
        <Route path="/analytics" element={<AnalyticsPage />} />
        <Route path="/" element={<LoginPage />} />
      </Routes>
    </div>
  );
}

export default App;