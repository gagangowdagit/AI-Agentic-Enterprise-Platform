import { useState } from 'react';
import { login } from '../services/authApi';
import { useNavigate } from 'react-router-dom';

function LoginPage() {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState('');
  const [isError, setIsError] = useState(false);

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setMessage('');
    setIsError(false);

    try {
      const result = await login({ email, password });
      localStorage.setItem('rag-auth-user', JSON.stringify({
        name: result.name,
        email: result.email,
      }));
      setMessage(`Login successful! Welcome, ${result.name}`);
      setEmail('');
      setPassword('');
      setTimeout(() => {
        navigate('/home');
      }, 1500);
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Login failed';
      setMessage(errorMessage);
      setIsError(true);
    }
  };

  return (
    <main className="login-page">
      <section className="login-brand" aria-labelledby="login-title">
          <div className="login-logo" aria-label="b1 logo">b1</div>
        <h1 id="login-title">AI Agentic Enterprise Platform</h1>
      </section>

      <section className="login-card">
        {message && (
          <div className={`login-message ${isError ? 'login-message-error' : 'login-message-success'}`} role={isError ? 'alert' : 'status'}>
            {message}
          </div>
        )}

        <form onSubmit={handleSubmit} className="login-form">
          <label className="login-field">
            <span className="login-field-icon login-user-icon" aria-hidden="true" />
            <span className="sr-only">Email address</span>
            <input
              id="email"
              type="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              placeholder="Email address"
              autoComplete="email"
              required
            />
          </label>

          <label className="login-field">
            <span className="login-field-icon login-lock-icon" aria-hidden="true" />
            <span className="sr-only">Password</span>
            <input
              id="password"
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              placeholder="Password"
              autoComplete="current-password"
              required
            />
          </label>

          <button type="submit" className="login-submit">Sign in</button>

          <div className="login-options">
            <label className="stay-signed-in">
              <input type="checkbox" />
              <span>Stay signed in</span>
            </label>
            <button type="button" className="forgot-password" onClick={() => setMessage('Please contact an administrator to reset your password.')}>
              Forgot password?
            </button>
          </div>
          <p className="auth-switch">Need an account? <a href="/signup">Sign up</a></p>
        </form>
      </section>
    </main>
  );
}

export default LoginPage;
