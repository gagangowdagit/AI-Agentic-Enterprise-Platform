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
    <div>
      <h1>Login</h1>

      {message && (
        <div style={{ color: isError ? 'red' : 'green', marginBottom: '10px' }}>
          {message}
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="email">Email:</label>
          <input
            id="email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>

        <div>
          <label htmlFor="password">Password:</label>
          <input
            id="password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>

        <button type="submit">Login</button>
      </form>
    </div>
  );
}

export default LoginPage;
