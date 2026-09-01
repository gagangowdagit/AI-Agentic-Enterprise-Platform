import { Routes, Route, Link, useLocation } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import HomePage from './pages/HomePage';

function App() {
  const location = useLocation();
  const isHomePage = location.pathname === '/home';

  return (
    <div>
      {!isHomePage && (
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

      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/home" element={<HomePage />} />
        <Route path="/" element={<LoginPage />} />
      </Routes>
    </div>
  );
}

export default App;