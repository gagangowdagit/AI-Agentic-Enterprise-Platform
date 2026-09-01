import { useState } from 'react';

interface Project {
  id: string;
  name: string;
}

interface FormData {
  projectId: string;
  projectName: string;
  status: 'PLANNED' | 'IN_PROGRESS' | 'COMPLETED';
}

function ProjectsPage() {
  const [projects, setProjects] = useState<Project[]>([
    { id: 'p1', name: 'Project-1' },
    { id: 'p2', name: 'Project-2' },
    { id: 'p3', name: 'Project-3' },
  ]);

  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState<FormData>({
    projectId: '',
    projectName: '',
    status: 'PLANNED',
  });
  const [submittedData, setSubmittedData] = useState<FormData | null>(null);

  const handleCreateProject = () => {
    setShowForm(true);
  };

  const handleCancel = () => {
    setShowForm(false);
    setFormData({ projectId: '', projectName: '', status: 'PLANNED' });
    setSubmittedData(null);
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setSubmittedData(formData);
    setProjects([...projects, { id: formData.projectId, name: formData.projectName }]);
    setShowForm(false);
    setFormData({ projectId: '', projectName: '', status: 'PLANNED' });
  };

  const handleProjectClick = (project: Project) => {
    alert(`Project ${project.id} - ${project.name} clicked`);
  };

  return (
    <div>
      <h1>Projects</h1>

      {!showForm && (
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
          <form onSubmit={handleSubmit}>
            <div style={{ marginBottom: '15px' }}>
              <label htmlFor="projectId" style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>
                Project ID:
              </label>
              <input
                type="text"
                id="projectId"
                name="projectId"
                value={formData.projectId}
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
              <label htmlFor="projectName" style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>
                Project Name:
              </label>
              <input
                type="text"
                id="projectName"
                name="projectName"
                value={formData.projectName}
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
                <option value="PLANNED">PLANNED</option>
                <option value="IN_PROGRESS">IN_PROGRESS</option>
                <option value="COMPLETED">COMPLETED</option>
              </select>
            </div>

            <div style={{ display: 'flex', gap: '10px' }}>
              <button
                type="submit"
                style={{
                  padding: '10px 20px',
                  backgroundColor: '#4CAF50',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: 'pointer',
                  fontSize: '14px',
                }}
              >
                Create
              </button>
              <button
                type="button"
                onClick={handleCancel}
                style={{
                  padding: '10px 20px',
                  backgroundColor: '#f44336',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: 'pointer',
                  fontSize: '14px',
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
          <p><strong>Project ID:</strong> {submittedData.projectId}</p>
          <p><strong>Project Name:</strong> {submittedData.projectName}</p>
          <p><strong>Status:</strong> {submittedData.status}</p>
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
            <h3>{`${project.id.toUpperCase()} - ${project.name}`}</h3>
          </div>
        ))}
      </div>
    </div>
  );
}

export default ProjectsPage;
