import { useEffect, useState } from 'react';
import { createDepartment, getDepartments } from '../services/departmentApi';
import type { Department } from '../services/departmentApi';

function DepartmentsPage() {
  const [departments, setDepartments] = useState<Department[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [createLoading, setCreateLoading] = useState(false);
  const [createError, setCreateError] = useState<string | null>(null);

  useEffect(() => {
    const loadDepartments = async () => {
      try {
        setLoading(true);
        setError(null);
        setDepartments(await getDepartments());
      } catch (loadError) {
        setError(loadError instanceof Error ? loadError.message : 'Failed to fetch departments');
      } finally {
        setLoading(false);
      }
    };

    loadDepartments();
  }, []);

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!name.trim()) return;

    try {
      setCreateLoading(true);
      setCreateError(null);
      const department = await createDepartment({ name: name.trim(), description: description.trim() || undefined });
      setDepartments((current) => [...current, department]);
      setName('');
      setDescription('');
      setShowForm(false);
    } catch (submitError) {
      setCreateError(submitError instanceof Error ? submitError.message : 'Failed to create department');
    } finally {
      setCreateLoading(false);
    }
  };

  return (
    <main style={{ padding: '24px 20px', maxWidth: '1100px', margin: '0 auto' }}>
      <header style={{ display: 'flex', justifyContent: 'space-between', gap: '16px', alignItems: 'center', flexWrap: 'wrap', marginBottom: '24px' }}>
        <div>
          <h1 style={{ margin: '0 0 8px', color: '#333' }}>Departments</h1>
          <p style={{ margin: 0, color: '#666' }}>Organize your organization by department.</p>
        </div>
        {!showForm && <button type="button" onClick={() => setShowForm(true)} style={buttonStyle('#4CAF50')}>Create New Department</button>}
      </header>

      {loading && <p>Loading departments...</p>}
      {error && <p role="alert" style={errorStyle}>{error}</p>}

      {showForm && (
        <form onSubmit={handleSubmit} style={{ marginBottom: '28px', padding: '20px', border: '1px solid #ccc', borderRadius: '6px', backgroundColor: '#f5f5f5' }}>
          <h2 style={{ marginTop: 0, color: '#333' }}>Create New Department</h2>
          {createError && <p role="alert" style={errorStyle}>{createError}</p>}
          <label style={labelStyle} htmlFor="department-name">Department name</label>
          <input id="department-name" value={name} onChange={(event) => setName(event.target.value)} required style={inputStyle} />
          <label style={labelStyle} htmlFor="department-description">Description</label>
          <textarea id="department-description" value={description} onChange={(event) => setDescription(event.target.value)} rows={4} style={inputStyle} />
          <div style={{ display: 'flex', gap: '10px', flexWrap: 'wrap', marginTop: '16px' }}>
            <button type="submit" disabled={createLoading} style={buttonStyle(createLoading ? '#9e9e9e' : '#4CAF50')}>{createLoading ? 'Creating...' : 'Create Department'}</button>
            <button type="button" onClick={() => { setShowForm(false); setCreateError(null); }} style={buttonStyle('#757575')}>Cancel</button>
          </div>
        </form>
      )}

      {!loading && !error && departments.length === 0 && <p style={{ color: '#666' }}>No departments have been created yet.</p>}
      {!loading && !error && departments.length > 0 && (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '16px' }}>
          {departments.map((department) => (
            <article key={department.id} style={{ minHeight: '120px', padding: '20px', border: '1px solid #ddd', borderRadius: '8px', backgroundColor: '#fff', boxShadow: '0 2px 8px rgba(0,0,0,0.06)' }}>
              <h2 style={{ margin: '0 0 10px', color: '#333', fontSize: '20px' }}>{department.name}</h2>
              <p style={{ margin: 0, color: '#666', lineHeight: 1.5 }}>{department.description || 'No description provided.'}</p>
            </article>
          ))}
        </div>
      )}
    </main>
  );
}

const inputStyle = { width: '100%', padding: '9px', marginBottom: '14px', border: '1px solid #ccc', borderRadius: '4px', fontSize: '14px', boxSizing: 'border-box' as const };
const labelStyle = { display: 'block', marginBottom: '6px', fontWeight: '600', color: '#444' };
const errorStyle = { padding: '12px', backgroundColor: '#ffebee', border: '1px solid #f44336', borderRadius: '4px', color: '#c62828' };
const buttonStyle = (backgroundColor: string) => ({ padding: '10px 16px', backgroundColor, color: 'white', border: 0, borderRadius: '4px', cursor: backgroundColor === '#9e9e9e' ? 'not-allowed' : 'pointer', fontWeight: '600' as const });

export default DepartmentsPage;