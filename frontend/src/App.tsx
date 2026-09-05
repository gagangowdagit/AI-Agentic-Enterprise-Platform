import { Routes, Route, Link, useLocation } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import HomePage from './pages/HomePage';
import ProjectsPage from './pages/ProjectsPage';
import ProjectDetailsPage from './pages/ProjectDetailsPage';
import DocumentsPage from './pages/DocumentsPage';
import TeamsPage from './pages/TeamsPage';
import NovaAIPage from './pages/NovaAIPage';

function App() {
  const location = useLocation();
  const isHomePage = location.pathname === '/home';
  const isProjectDetailsPage = location.pathname.startsWith('/projects/');
  const isDetailPage = location.pathname === '/projects'
    || isProjectDetailsPage
    || ['/documents', '/teams', '/nova-ai'].includes(location.pathname);

  return (
    <div>
      {!isHomePage && !isDetailPage && (
        <nav style={{ marginBottom: '20px', paddingBottom: '10px', borderBottom: '1px solid #ccc' }}>
          <Link
            to="/login"
            style={{
              marginRight: '10px',
              fontWeight: location.pathname === '/login' ? 'bold' : 'normal',
              textDecoration: 'none',
              cursor: 'pointer',
              color: 'blue',
            }}
          >
            Login
          </Link>
          <Link
            to="/register"
            style={{
              fontWeight: location.pathname === '/register' ? 'bold' : 'normal',
              textDecoration: 'none',
              cursor: 'pointer',
              color: 'blue',
            }}
          >
            Register
          </Link>
        </nav>
      )}

      {(isHomePage || isDetailPage) && !isProjectDetailsPage && (
        <nav style={{ marginBottom: '20px', paddingBottom: '10px', borderBottom: '1px solid #ccc' }}>
          <Link
            to="/home"
            style={{
              fontWeight: location.pathname === '/home' ? 'bold' : 'normal',
              textDecoration: 'none',
              cursor: 'pointer',
              color: 'blue',
            }}
          >
            Back to Home
          </Link>
        </nav>
      )}

      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/home" element={<HomePage />} />
        <Route path="/projects" element={<ProjectsPage />} />
        <Route path="/projects/:projectId" element={<ProjectDetailsPage />} />
        <Route path="/documents" element={<DocumentsPage />} />
        <Route path="/teams" element={<TeamsPage />} />
        <Route path="/nova-ai" element={<NovaAIPage />} />
        <Route path="/" element={<LoginPage />} />
      </Routes>
    </div>
  );
}

export default App;