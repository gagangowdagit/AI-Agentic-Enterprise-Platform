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

  const handleProjectKeyDown = (event: React.KeyboardEvent<HTMLElement>, project: Project) => {
    if (event.key === 'Enter' || event.key === ' ') {
      event.preventDefault();
      handleProjectClick(project);
    }
  };

  const formatDate = (value?: string) => {
    if (!value) {
      return 'Not set';
    }

    const parsedDate = new Date(value);
    if (Number.isNaN(parsedDate.getTime())) {
      return value;
    }

    return parsedDate.toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
    });
  };

  const getStatusClass = (status: string) => {
    const normalizedStatus = status.toLowerCase();
    if (normalizedStatus.includes('active')) return 'project-badge-active';
    if (normalizedStatus.includes('pending')) return 'project-badge-pending';
    if (normalizedStatus.includes('complete')) return 'project-badge-complete';
    return 'project-badge-neutral';
  };

  const getPriorityClass = (priority: string) => {
    const normalizedPriority = priority.toLowerCase();
    if (normalizedPriority === 'high') return 'project-priority-high';
    if (normalizedPriority === 'medium') return 'project-priority-medium';
    return 'project-priority-low';
  };

  return (
    <main className="project-page">
      <header className="project-page-header">
        <div className="project-header-copy">
          <p className="section-kicker">Portfolio</p>
          <h1>Projects</h1>
          <p className="section-subtitle">
            Track delivery, priorities, and ownership across every active initiative.
          </p>
        </div>

        {!showForm && !loading && (
          <button type="button" className="primary-button" onClick={handleCreateProject}>
            + Create Project
          </button>
        )}
      </header>

      {loading && (
        <div className="project-state project-state-loading">
          <p>Loading projects...</p>
        </div>
      )}

      {error && (
        <div className="project-state project-state-error">
          <p>Error: {error}</p>
        </div>
      )}

      {showForm && (
        <section className="project-form-card">
          <div className="project-form-header">
            <div>
              <p className="section-kicker">New project</p>
              <h2>Create a new project</h2>
            </div>
          </div>

          {createError && (
            <div className="project-state project-state-error project-form-error">
              <p>Error: {createError}</p>
            </div>
          )}

          <form className="project-form" onSubmit={handleSubmit}>
            <div className="project-form-grid">
              <div className="field-group">
                <label htmlFor="name">Project Name</label>
                <input
                  type="text"
                  id="name"
                  name="name"
                  value={formData.name}
                  onChange={handleInputChange}
                  required
                  placeholder="Enter project name"
                />
              </div>

              <div className="field-group">
                <label htmlFor="departmentId">Department</label>
                <select
                  id="departmentId"
                  name="departmentId"
                  value={formData.departmentId}
                  onChange={handleInputChange}
                  disabled={departmentsLoading}
                >
                  <option value="">{departmentsLoading ? 'Loading departments...' : 'Select department'}</option>
                  {departments.map((department) => (
                    <option key={department.id} value={department.id}>{department.name}</option>
                  ))}
                </select>
              </div>

              <div className="field-group">
                <label htmlFor="status">Status</label>
                <select id="status" name="status" value={formData.status} onChange={handleInputChange}>
                  <option value="Active">Active</option>
                  <option value="Pending">Pending</option>
                  <option value="Completed">Completed</option>
                </select>
              </div>

              <div className="field-group">
                <label htmlFor="priority">Priority</label>
                <select
                  id="priority"
                  name="priority"
                  value={formData.priority}
                  onChange={handleInputChange}
                  required
                >
                  <option value="LOW">Low</option>
                  <option value="MEDIUM">Medium</option>
                  <option value="HIGH">High</option>
                </select>
              </div>

              <div className="field-group field-group-wide">
                <label htmlFor="description">Description</label>
                <textarea
                  id="description"
                  name="description"
                  value={formData.description}
                  onChange={handleInputChange}
                  rows={4}
                  placeholder="Add a short project summary"
                />
              </div>

              <div className="field-group">
                <label htmlFor="startDate">Start Date</label>
                <input type="date" id="startDate" name="startDate" value={formData.startDate} onChange={handleInputChange} />
              </div>

              <div className="field-group">
                <label htmlFor="endDate">End Date</label>
                <input type="date" id="endDate" name="endDate" value={formData.endDate} onChange={handleInputChange} />
              </div>
            </div>

            <div className="project-form-actions">
              <button type="button" className="secondary-button" onClick={handleCancel} disabled={createLoading}>
                Cancel
              </button>
              <button type="submit" className="primary-button" disabled={createLoading}>
                {createLoading ? 'Creating...' : 'Create Project'}
              </button>
            </div>
          </form>
        </section>
      )}

      {submittedData && (
        <div className="project-success-banner">
          <div>
            <p className="section-kicker">Project created</p>
            <h3>{submittedData.name}</h3>
          </div>
          <div className="project-success-meta">
            <span className={`project-badge ${getStatusClass(submittedData.status)}`}>{submittedData.status}</span>
            <span className={`project-priority ${getPriorityClass(submittedData.priority)}`}>{submittedData.priority}</span>
          </div>
        </div>
      )}

      {!loading && !error && projects.length === 0 && (
        <div className="project-empty-state">
          <h2>No projects yet</h2>
          <p>Start by creating your first initiative and assign ownership.</p>
        </div>
      )}

      <div className="project-card-grid">
        {projects.map((project) => (
          <article
            key={project.id}
            className="project-card"
            onClick={() => handleProjectClick(project)}
            onKeyDown={(event) => handleProjectKeyDown(event, project)}
            role="button"
            tabIndex={0}
          >
            <div className="project-card-header">
              <div>
                <p className="project-card-id">Project {project.id}</p>
                <h3>{project.name}</h3>
              </div>
              <span className={`project-badge ${getStatusClass(project.status)}`}>{project.status}</span>
            </div>

            <div className="project-card-meta">
              <span className={`project-priority ${getPriorityClass(project.priority)}`}>{project.priority}</span>
              <span>{project.department?.name || 'No department'}</span>
            </div>

            {project.description && <p className="project-card-description">{project.description}</p>}

            <div className="project-card-details">
              <div>
                <span>Start</span>
                <strong>{formatDate(project.startDate)}</strong>
              </div>
              <div>
                <span>End</span>
                <strong>{formatDate(project.endDate)}</strong>
              </div>
            </div>

            <div className="project-card-footer">
              <span>View details</span>
              <span aria-hidden="true">→</span>
            </div>
          </article>
        ))}
      </div>
    </main>
  );
}

export default ProjectsPage;
