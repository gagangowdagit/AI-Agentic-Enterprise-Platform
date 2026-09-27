import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { createEmployee, getDepartments, type Department } from '../services/departmentApi';

function RegisterPage() {
  const navigate = useNavigate();
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('EMPLOYEE');
  const [departmentId, setDepartmentId] = useState('');
  const [departments, setDepartments] = useState<Department[]>([]);
  const [message, setMessage] = useState('');
  const [isError, setIsError] = useState(false);

  useEffect(() => {
    const loadDepartments = async () => {
      try {
        const items = await getDepartments();
        setDepartments(items);
        if (items.length > 0 && !departmentId) {
          setDepartmentId(String(items[0].id));
        }
      } catch {
        setMessage('Unable to load departments. Please contact an administrator.');
        setIsError(true);
      }
    };

    void loadDepartments();
  }, []);

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setMessage('');
    setIsError(false);

    if (!departmentId) {
      setIsError(true);
      setMessage('Please select a department.');
      return;
    }

    try {
      await createEmployee({
        firstName,
        lastName,
        email,
        password,
        role,
        departmentId: Number(departmentId),
      });
      setMessage('Account created. Redirecting to sign in...');
      setFirstName('');
      setLastName('');
      setEmail('');
      setPassword('');
      setRole('EMPLOYEE');
      setDepartmentId(departments[0]?.id ? String(departments[0].id) : '');
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
            <span className="sr-only">First name</span>
            <input
              type="text"
              value={firstName}
              onChange={(event) => setFirstName(event.target.value)}
              placeholder="First name"
              autoComplete="given-name"
              required
            />
          </label>

          <label className="login-field">
            <span className="login-field-icon login-user-icon" aria-hidden="true" />
            <span className="sr-only">Last name</span>
            <input
              type="text"
              value={lastName}
              onChange={(event) => setLastName(event.target.value)}
              placeholder="Last name"
              autoComplete="family-name"
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

          <label className="login-field">
            <span className="sr-only">Department</span>
            <select
              value={departmentId}
              onChange={(event) => setDepartmentId(event.target.value)}
              style={{ width: '100%', padding: '0.9rem 1rem', borderRadius: '12px', border: '1px solid #d8dfe8', backgroundColor: '#fff', color: '#1e293b', fontSize: '1rem' }}
              required
            >
              <option value="">Select department</option>
              {departments.map((department) => (
                <option key={department.id} value={department.id}>{department.name}</option>
              ))}
            </select>
          </label>

          <label className="login-field">
            <span className="sr-only">User role</span>
            <select
              value={role}
              onChange={(event) => setRole(event.target.value)}
              style={{ width: '100%', padding: '0.9rem 1rem', borderRadius: '12px', border: '1px solid #d8dfe8', backgroundColor: '#fff', color: '#1e293b', fontSize: '1rem' }}
            >
              <option value="EMPLOYEE">Employee</option>
              <option value="MANAGER">Manager</option>
              <option value="DIRECTOR">Director</option>
              <option value="CEO">CEO</option>
            </select>
          </label>

          <button type="submit" className="login-submit signup-submit">Create account</button>
          <p className="auth-switch">Already have an account? <a href="/login">Sign in</a></p>
        </form>
      </section>
    </main>
  );
}

export default RegisterPage;