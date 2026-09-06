import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getProjects, createProject } from '../services/projectApi';
import type { Project, ProjectPriority } from '../services/projectApi';
import { getDepartments } from '../services/departmentApi';
import type { Department } from '../services/departmentApi';

interface FormData {
  name: string;
  description: string;
  status: 'Active' | 'Pending' | 'Completed';
  startDate: string;
  endDate: string;
  priority: ProjectPriority;
  departmentId: string;
}

const initialFormData: FormData = {
  name: '',
  description: '',
  status: 'Active',
  startDate: '',
  endDate: '',
  priority: 'MEDIUM',
  departmentId: '',
};

function ProjectsPage() {
  const navigate = useNavigate();
  const [projects, setProjects] = useState<Project[]>([]);
  const [departments, setDepartments] = useState<Department[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState<FormData>(initialFormData);
  const [submittedData, setSubmittedData] = useState<Project | null>(null);
  const [createLoading, setCreateLoading] = useState(false);
  const [createError, setCreateError] = useState<string | null>(null);
  const [departmentsLoading, setDepartmentsLoading] = useState(false);

  // Fetch projects from backend
  useEffect(() => {
    const fetchProjects = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await getProjects();
        setProjects(data);
      } catch (err) {
        const errorMessage = err instanceof Error ? err.message : 'Failed to fetch projects';
        setError(errorMessage);
        console.error('Error fetching projects:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchProjects();
  }, []);

  useEffect(() => {
    const fetchDepartments = async () => {
      try {
        setDepartmentsLoading(true);
        setDepartments(await getDepartments());
      } catch (departmentError) {
        setCreateError(departmentError instanceof Error ? departmentError.message : 'Failed to fetch departments');
      } finally {
        setDepartmentsLoading(false);
      }
    };

    fetchDepartments();
  }, []);

  const handleCreateProject = () => {
    setShowForm(true);
  };

  const handleCancel = () => {
    setShowForm(false);
    setFormData(initialFormData);
    setSubmittedData(null);
    setCreateError(null);
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setCreateLoading(true);
    setCreateError(null);

    try {
      const newProject = await createProject({
        name: formData.name,
        description: formData.description || undefined,
        status: formData.status,
        startDate: formData.startDate || undefined,
        endDate: formData.endDate || undefined,
        priority: formData.priority,
        department: formData.departmentId ? { id: Number(formData.departmentId) } : undefined,
      });

      setSubmittedData(newProject);
      setProjects((currentProjects) => [...currentProjects, newProject]);
      setShowForm(false);
      setFormData(initialFormData);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to create project';
      setCreateError(errorMessage);
      console.error('Error creating project:', err);
    } finally {
      setCreateLoading(false);
    }
  };

  const handleProjectClick = (project: Project) => {
    navigate(`/projects/${project.id}`);
  };

  return (
    <div>
      <h1>Projects</h1>

      {loading && (
        <div style={{ padding: '20px', backgroundColor: '#e3f2fd', border: '1px solid #2196F3', borderRadius: '4px', marginBottom: '20px' }}>
          <p>Loading projects...</p>
        </div>
      )}

      {error && (
        <div style={{ padding: '20px', backgroundColor: '#ffebee', border: '1px solid #f44336', borderRadius: '4px', marginBottom: '20px' }}>
          <p style={{ color: '#c62828' }}>Error: {error}</p>
        </div>
      )}

      {!showForm && !loading && (
        <button
          onClick={handleCreateProject}
          style={{
            padding: '10px 20px',
            marginBottom: '20px',
            backgroundColor: '#4CAF50',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer',
            fontSize: '16px',
          }}
        >
          Create New Project
        </button>
      )}

      {showForm && (
        <div style={{ marginBottom: '30px', padding: '20px', border: '1px solid #ccc', borderRadius: '4px', backgroundColor: '#f5f5f5' }}>
          <h2>Create New Project</h2>
          {createError && (
            <div style={{ padding: '15px', backgroundColor: '#ffebee', border: '1px solid #f44336', borderRadius: '4px', marginBottom: '15px' }}>
              <p style={{ color: '#c62828', margin: '0' }}>Error: {createError}</p>
            </div>
          )}
          <form onSubmit={handleSubmit}>
            <div style={{ marginBottom: '15px' }}>
              <label htmlFor="name" style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>
                Project Name:
              </label>
              <input
                type="text"
                id="name"
                name="name"
                value={formData.name}
                onChange={handleInputChange}
                required
                style={{
                  width: '100%',
                  padding: '8px',
                  border: '1px solid #ccc',
                  borderRadius: '4px',
                  fontSize: '14px',
                  boxSizing: 'border-box',
                }}
              />
            </div>

            <div style={{ marginBottom: '15px' }}>
              <label htmlFor="description" style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>
                Description:
              </label>
              <textarea
                id="description"
                name="description"
                value={formData.description}
                onChange={handleInputChange}
                rows={4}
                style={{
                  width: '100%',
                  padding: '8px',
                  border: '1px solid #ccc',
                  borderRadius: '4px',
                  fontSize: '14px',
                  boxSizing: 'border-box',
                  resize: 'vertical',
                }}
              />
            </div>

            <div style={{ marginBottom: '15px' }}>
              <label htmlFor="status" style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>
                Status:
              </label>
              <select
                id="status"
                name="status"
                value={formData.status}
                onChange={handleInputChange}
                style={{
                  width: '100%',
                  padding: '8px',
                  border: '1px solid #ccc',
                  borderRadius: '4px',
                  fontSize: '14px',
                  boxSizing: 'border-box',
                }}
              >
                <option value="Active">Active</option>
                <option value="Pending">Pending</option>
                <option value="Completed">Completed</option>
              </select>
            </div>

            <div style={{ marginBottom: '15px' }}>
              <label htmlFor="priority" style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>
                Priority:
              </label>
              <select
                id="priority"
                name="priority"
                value={formData.priority}
                onChange={handleInputChange}
                required
                style={{
                  width: '100%',
                  padding: '8px',
                  border: '1px solid #ccc',
                  borderRadius: '4px',
                  fontSize: '14px',
                  boxSizing: 'border-box',
                }}
              >
                <option value="LOW">Low</option>
                <option value="MEDIUM">Medium</option>
                <option value="HIGH">High</option>
              </select>
            </div>

            <div style={{ marginBottom: '15px' }}>
              <label htmlFor="departmentId" style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>
                Assigned To:
              </label>
              <select
                id="departmentId"
                name="departmentId"
                value={formData.departmentId}
                onChange={handleInputChange}
                disabled={departmentsLoading}
                style={{
                  width: '100%',
                  padding: '8px',
                  border: '1px solid #ccc',
                  borderRadius: '4px',
                  fontSize: '14px',
                  boxSizing: 'border-box',
                }}
              >
                <option value="">{departmentsLoading ? 'Loading departments...' : 'Select department'}</option>
                {departments.map((department) => (
                  <option key={department.id} value={department.id}>{department.name}</option>
                ))}
              </select>
            </div>

            <div style={{ display: 'flex', gap: '15px', marginBottom: '15px' }}>
              <div style={{ flex: 1 }}>
                <label htmlFor="startDate" style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>
                  Start Date:
                </label>
                <input
                  type="date"
                  id="startDate"
                  name="startDate"
                  value={formData.startDate}
                  onChange={handleInputChange}
                  style={{
                    width: '100%',
                    padding: '8px',
                    border: '1px solid #ccc',
                    borderRadius: '4px',
                    fontSize: '14px',
                    boxSizing: 'border-box',
                  }}
                />
              </div>
              <div style={{ flex: 1 }}>
                <label htmlFor="endDate" style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>
                  End Date:
                </label>
                <input
                  type="date"
                  id="endDate"
                  name="endDate"
                  value={formData.endDate}
                  onChange={handleInputChange}
                  style={{
                    width: '100%',
                    padding: '8px',
                    border: '1px solid #ccc',
                    borderRadius: '4px',
                    fontSize: '14px',
                    boxSizing: 'border-box',
                  }}
                />
              </div>
            </div>

            <div style={{ display: 'flex', gap: '10px' }}>
              <button
                type="submit"
                disabled={createLoading}
                style={{
                  padding: '10px 20px',
                  backgroundColor: createLoading ? '#ccc' : '#4CAF50',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: createLoading ? 'not-allowed' : 'pointer',
                  fontSize: '14px',
                }}
              >
                {createLoading ? 'Creating...' : 'Create'}
              </button>
              <button
                type="button"
                onClick={handleCancel}
                disabled={createLoading}
                style={{
                  padding: '10px 20px',
                  backgroundColor: '#f44336',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: createLoading ? 'not-allowed' : 'pointer',
                  fontSize: '14px',
                  opacity: createLoading ? 0.6 : 1,
                }}
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      )}

      {submittedData && (
        <div style={{ marginBottom: '20px', padding: '15px', backgroundColor: '#c8e6c9', border: '1px solid #4CAF50', borderRadius: '4px' }}>
          <h3>Project Created Successfully!</h3>
          <p><strong>Project Name:</strong> {submittedData.name}</p>
          <p><strong>Status:</strong> {submittedData.status}</p>
          <p><strong>Priority:</strong> {submittedData.priority}</p>
        </div>
      )}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '20px' }}>
        {projects.map((project) => (
          <div
            key={project.id}
            onClick={() => handleProjectClick(project)}
            style={{
              padding: '20px',
              border: '1px solid #ccc',
              borderRadius: '4px',
              backgroundColor: '#f9f9f9',
              cursor: 'pointer',
              transition: 'background-color 0.2s, box-shadow 0.2s',
              textAlign: 'center',
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.backgroundColor = '#e8f5e9';
              e.currentTarget.style.boxShadow = '0 2px 8px rgba(0, 0, 0, 0.1)';
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.backgroundColor = '#f9f9f9';
              e.currentTarget.style.boxShadow = 'none';
            }}
          >
            <h3>{`${project.id} - ${project.name}`}</h3>
            {project.status && (
              <p style={{ color: '#666', marginTop: '10px' }}>
                <strong>Status:</strong> {project.status}
              </p>
            )}
            {project.priority && (
              <p style={{ color: '#666', marginTop: '10px' }}>
                <strong>Priority:</strong> {project.priority}
              </p>
            )}
            {project.description && (
              <p style={{ color: '#666', marginTop: '10px', whiteSpace: 'pre-wrap' }}>
                {project.description}
              </p>
            )}
            {(project.startDate || project.endDate) && (
              <p style={{ color: '#666', marginTop: '10px' }}>
                <strong>Dates:</strong> {project.startDate || 'Not set'} - {project.endDate || 'Not set'}
              </p>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}

export default ProjectsPage;
