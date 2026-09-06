import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { createUser } from '../services/userApi';

function RegisterPage() {
  const navigate = useNavigate();
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState('');
  const [isError, setIsError] = useState(false);

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setMessage('');
    setIsError(false);

    try {
      await createUser({
        name,
        email,
        password,
      });
      setMessage('Account created. Redirecting to sign in...');
      setName('');
      setEmail('');
      setPassword('');
      window.setTimeout(() => navigate('/login'), 900);
    } catch (error) {
      setIsError(true);
      setMessage(error instanceof Error ? error.message : 'Sign up failed');
    }
  };

  return (
    <main className="login-page signup-page">
      <section className="login-brand" aria-labelledby="signup-title">
          <div className="login-logo" aria-label="b1 logo">b1</div>
        <h1 id="signup-title">Join the AI Enterprise Platform</h1>
      </section>

      <section className="login-card signup-card">
        {message && (
          <div className={`login-message ${isError ? 'login-message-error' : 'login-message-success'}`} role={isError ? 'alert' : 'status'}>
            {message}
          </div>
        )}

        <form onSubmit={handleSubmit} className="login-form">
          <label className="login-field">
            <span className="login-field-icon login-user-icon" aria-hidden="true" />
            <span className="sr-only">Full name</span>
          <input
            type="text"
            value={name}
            onChange={(event) => setName(event.target.value)}
              placeholder="Full name"
              autoComplete="name"
              required
          />
          </label>

          <label className="login-field">
            <span className="login-field-icon login-user-icon" aria-hidden="true" />
            <span className="sr-only">Email address</span>
          <input
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
            type="password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
              placeholder="Password"
              autoComplete="new-password"
              minLength={6}
              required
          />
          </label>

          <button type="submit" className="login-submit signup-submit">Create account</button>
          <p className="auth-switch">Already have an account? <a href="/login">Sign in</a></p>
        </form>
      </section>
    </main>
  );
}

export default RegisterPage;