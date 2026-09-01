import { useState } from 'react';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';

function App() {
  const [currentPage, setCurrentPage] = useState<'login' | 'register'>('login');

  return (
    <div>
      <nav style={{ marginBottom: '20px', paddingBottom: '10px', borderBottom: '1px solid #ccc' }}>
        <button
          onClick={() => setCurrentPage('login')}
          style={{ marginRight: '10px', fontWeight: currentPage === 'login' ? 'bold' : 'normal' }}
        >
          Login
        </button>
        <button
          onClick={() => setCurrentPage('register')}
          style={{ fontWeight: currentPage === 'register' ? 'bold' : 'normal' }}
        >
          Register
        </button>
      </nav>

      <div>
        {currentPage === 'login' && <LoginPage />}
        {currentPage === 'register' && <RegisterPage />}
      </div>
    </div>
  );
}

export default App;