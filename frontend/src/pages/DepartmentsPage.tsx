import { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import { createDepartment, createEmployee, getDepartments, getEmployees } from '../services/departmentApi';
import type { Department, Employee } from '../services/departmentApi';
import { getProjects } from '../services/projectApi';
import type { Project } from '../services/projectApi';
import { getProjectTeam } from '../services/teamApi';
import type { TeamMember } from '../services/teamApi';

function DepartmentsPage() {
  const location = useLocation();
  const [departments, setDepartments] = useState<Department[]>([]);
  const [projects, setProjects] = useState<Project[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [createLoading, setCreateLoading] = useState(false);
  const [createError, setCreateError] = useState<string | null>(null);
  const [selectedDepartment, setSelectedDepartment] = useState<Department | null>(null);
  const [selectedProjectId, setSelectedProjectId] = useState<number | null>(null);
  const [expandedDepartmentId, setExpandedDepartmentId] = useState<number | null>(null);
  const [expandedProjectId, setExpandedProjectId] = useState<number | null>(null);
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [showEmployeeForm, setShowEmployeeForm] = useState(false);
  const [employeeFirstName, setEmployeeFirstName] = useState('');
  const [employeeLastName, setEmployeeLastName] = useState('');
  const [employeeEmail, setEmployeeEmail] = useState('');
  const [employeeRole, setEmployeeRole] = useState('');
  const [employeeDepartmentId, setEmployeeDepartmentId] = useState('');
  const [employeeLoading, setEmployeeLoading] = useState(false);
  const [employeeError, setEmployeeError] = useState<string | null>(null);
  const [projectTeamMap, setProjectTeamMap] = useState<Record<number, TeamMember[]>>({});
  const [teamMembersLoading, setTeamMembersLoading] = useState<Record<number, boolean>>({});

  useEffect(() => {
    const loadDepartments = async () => {
      try {
        setLoading(true);
        setError(null);
        const [departmentData, employeeData, projectData] = await Promise.all([
          getDepartments(),
          getEmployees(),
          getProjects(),
        ]);
        setDepartments(departmentData);
        setEmployees(employeeData);
        setProjects(projectData);
      } catch (loadError) {
        setError(loadError instanceof Error ? loadError.message : 'Failed to fetch departments');
      } finally {
        setLoading(false);
      }
    };

    loadDepartments();
  }, []);

  useEffect(() => {
    const focus = (location.state as { analyticsFocus?: 'totalDepartments' | 'totalEmployees' } | null)?.analyticsFocus;
    if (!focus || departments.length === 0) {
      return;
    }

    const firstDepartment = departments[0];
    setSelectedDepartment(firstDepartment);
    setExpandedDepartmentId(firstDepartment.id);

    if (focus === 'totalEmployees') {
      setSelectedProjectId(null);
    }
  }, [departments, location.state]);

  const departmentProjects = selectedDepartment
    ? projects.filter((project) => project.department?.id === selectedDepartment.id)
    : [];

  const selectedProject = departmentProjects.find((project) => project.id === selectedProjectId) || null;

  const handleDepartmentSelect = (department: Department) => {
    setSelectedDepartment(department);
    const relatedProjects = projects.filter((project) => project.department?.id === department.id);
    setSelectedProjectId(relatedProjects[0]?.id ?? null);
    setExpandedProjectId(null);
  };

  const handleProjectTeamToggle = async (project: Project) => {
    if (selectedProjectId !== project.id) {
      setSelectedProjectId(project.id);
    }

    const existingMembers = projectTeamMap[project.id];
    if (existingMembers !== undefined) {
      setExpandedProjectId((current) => (current === project.id ? null : project.id));
      return;
    }

    try {
      setTeamMembersLoading((current) => ({ ...current, [project.id]: true }));
      const members = await getProjectTeam(String(project.id));
      setProjectTeamMap((current) => ({ ...current, [project.id]: members }));
      setExpandedProjectId(project.id);
    } catch (teamError) {
      setError(teamError instanceof Error ? teamError.message : 'Failed to fetch project team');
    } finally {
      setTeamMembersLoading((current) => ({ ...current, [project.id]: false }));
    }
  };

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!name.trim()) return;

    try {
      setCreateLoading(true);
      setCreateError(null);
      const department = await createDepartment({ name: name.trim(), description: description.trim() || undefined });
      setDepartments((current) => [...current, department]);
      setSelectedDepartment(null);
      setName('');
      setDescription('');
      setShowForm(false);
    } catch (submitError) {
      setCreateError(submitError instanceof Error ? submitError.message : 'Failed to create department');
    } finally {
      setCreateLoading(false);
    }
  };

  const handleEmployeeSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!employeeDepartmentId) return;

    try {
      setEmployeeLoading(true);
      setEmployeeError(null);
      await createEmployee({
        firstName: employeeFirstName.trim(),
        lastName: employeeLastName.trim(),
        email: employeeEmail.trim(),
        role: employeeRole.trim(),
        departmentId: Number(employeeDepartmentId),
      });
      setEmployees(await getEmployees());
      setEmployeeFirstName('');
      setEmployeeLastName('');
      setEmployeeEmail('');
      setEmployeeRole('');
      setEmployeeDepartmentId('');
      setShowEmployeeForm(false);
    } catch (submitError) {
      setEmployeeError(submitError instanceof Error ? submitError.message : 'Failed to create employee');
    } finally {
      setEmployeeLoading(false);
    }
  };

  return (
    <main className="department-page" style={{ padding: '24px 20px', maxWidth: '1100px', margin: '0 auto' }}>
      <header className="department-header" style={{ display: 'flex', justifyContent: 'space-between', gap: '16px', alignItems: 'center', flexWrap: 'wrap', marginBottom: '24px' }}>
        <div>
          <h1 style={{ margin: '0 0 8px', color: '#333' }}>Departments</h1>
          <p style={{ margin: 0, color: '#666' }}>Organize your organization by department.</p>
        </div>
        {!showForm && !showEmployeeForm && (
          <div style={{ display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
            <button type="button" onClick={() => setShowForm(true)} style={buttonStyle('#4CAF50')}>Create New Department</button>
            <button type="button" onClick={() => { setShowEmployeeForm(true); setEmployeeError(null); }} style={buttonStyle('#1976D2')}>Create New Employee</button>
          </div>
        )}
      </header>

      {loading && <p>Loading departments...</p>}
      {error && <p role="alert" style={errorStyle}>{error}</p>}

      {showForm && (
        <form className="department-panel" onSubmit={handleSubmit} style={{ marginBottom: '28px', padding: '20px', border: '1px solid #ccc', borderRadius: '6px', backgroundColor: '#f5f5f5' }}>
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

      {showEmployeeForm && (
        <form className="department-panel" onSubmit={handleEmployeeSubmit} style={{ marginBottom: '28px', padding: '20px', border: '1px solid #ccc', borderRadius: '6px', backgroundColor: '#f5f5f5' }}>
          <h2 style={{ marginTop: 0, color: '#333' }}>Create New Employee</h2>
          {employeeError && <p role="alert" style={errorStyle}>{employeeError}</p>}
          <label style={labelStyle} htmlFor="employee-first-name">First name</label>
          <input id="employee-first-name" value={employeeFirstName} onChange={(event) => setEmployeeFirstName(event.target.value)} required style={inputStyle} />
          <label style={labelStyle} htmlFor="employee-last-name">Last name</label>
          <input id="employee-last-name" value={employeeLastName} onChange={(event) => setEmployeeLastName(event.target.value)} required style={inputStyle} />
          <label style={labelStyle} htmlFor="employee-email">Email</label>
          <input id="employee-email" type="email" value={employeeEmail} onChange={(event) => setEmployeeEmail(event.target.value)} required style={inputStyle} />
          <label style={labelStyle} htmlFor="employee-role">Role</label>
          <input id="employee-role" value={employeeRole} onChange={(event) => setEmployeeRole(event.target.value)} required style={inputStyle} />
          <label style={labelStyle} htmlFor="employee-department">Department</label>
          <select id="employee-department" value={employeeDepartmentId} onChange={(event) => setEmployeeDepartmentId(event.target.value)} required style={inputStyle}>
            <option value="">Select department</option>
            {departments.map((department) => <option key={department.id} value={department.id}>{department.name}</option>)}
          </select>
          <div style={{ display: 'flex', gap: '10px', flexWrap: 'wrap', marginTop: '16px' }}>
            <button type="submit" disabled={employeeLoading || departments.length === 0} style={buttonStyle(employeeLoading || departments.length === 0 ? '#9e9e9e' : '#1976D2')}>{employeeLoading ? 'Creating...' : 'Create Employee'}</button>
            <button type="button" onClick={() => { setShowEmployeeForm(false); setEmployeeError(null); }} style={buttonStyle('#757575')}>Cancel</button>
          </div>
        </form>
      )}

      {!loading && !error && departments.length === 0 && <p style={{ color: '#666' }}>No departments have been created yet.</p>}
      {!loading && !error && departments.length > 0 && (
        <>
          {selectedDepartment && (
            <section className="department-selected-panel" aria-live="polite" style={{ marginBottom: '20px', padding: '16px 20px', border: '2px solid #4CAF50', borderRadius: '8px', backgroundColor: '#f7fff7' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', gap: '16px', alignItems: 'flex-start', flexWrap: 'wrap', marginBottom: '18px' }}>
                <div>
                  <p style={{ margin: '0 0 8px', color: '#1976D2', fontSize: '12px', fontWeight: 700, letterSpacing: '0.12em', textTransform: 'uppercase' }}>Team</p>
                  <h2 style={{ margin: '0 0 6px', color: '#333', fontSize: '22px' }}>{selectedDepartment.name}</h2>
                  <p style={{ margin: 0, color: '#555' }}>{employees.filter((employee) => employee.department?.id === selectedDepartment.id).length} employees associated with this department</p>
                </div>
                <button type="button" onClick={() => setExpandedDepartmentId((current) => current === selectedDepartment.id ? null : selectedDepartment.id)} style={buttonStyle('#1976D2')}>
                  {expandedDepartmentId === selectedDepartment.id ? 'Hide employees' : 'View employees'}
                </button>
              </div>

              {expandedDepartmentId === selectedDepartment.id && (
                <div style={{ marginBottom: '18px', display: 'grid', gap: '8px' }}>
                  {employees.filter((employee) => employee.department?.id === selectedDepartment.id).length === 0 ? (
                    <p style={{ margin: 0, color: '#666' }}>No employees are associated with this department.</p>
                  ) : (
                    employees
                      .filter((employee) => employee.department?.id === selectedDepartment.id)
                      .map((employee) => (
                        <div key={employee.id} className="department-employee-row" style={employeeRowStyle}>
                          <strong>{employee.firstName} {employee.lastName}</strong>
                          <span>{employee.role}</span>
                          {employee.email && <span>{employee.email}</span>}
                        </div>
                      ))
                  )}
                </div>
              )}

              <div style={{ display: 'grid', gap: '12px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: '12px', flexWrap: 'wrap' }}>
                  <h3 style={{ margin: 0, color: '#333' }}>Associated projects</h3>
                  <span style={{ color: '#555', fontWeight: 700 }}>{departmentProjects.length} project{departmentProjects.length === 1 ? '' : 's'}</span>
                </div>

                {departmentProjects.length === 0 ? (
                  <p style={{ margin: 0, color: '#666' }}>No projects are currently assigned to this team.</p>
                ) : (
                  departmentProjects.map((project) => {
                    const projectTeamMembers = projectTeamMap[project.id] ?? [];
                    const isSelectedProject = selectedProjectId === project.id;

                    return (
                      <div key={project.id} className="department-project-card" style={{ border: '1px solid #d9e7d9', borderRadius: '8px', backgroundColor: '#fff', overflow: 'hidden' }}>
                        <button
                          type="button"
                          className="department-project-button"
                          onClick={() => setSelectedProjectId(project.id)}
                          style={{
                            width: '100%',
                            padding: '16px 18px',
                            border: 0,
                            textAlign: 'left',
                            backgroundColor: isSelectedProject ? '#ecfdf5' : '#fff',
                            cursor: 'pointer',
                            color: '#333',
                            font: 'inherit',
                          }}
                        >
                          <div style={{ display: 'flex', justifyContent: 'space-between', gap: '16px', alignItems: 'center', flexWrap: 'wrap' }}>
                            <div>
                              <div style={{ fontSize: '12px', color: '#1976D2', fontWeight: 700, letterSpacing: '0.08em', textTransform: 'uppercase', marginBottom: '6px' }}>Project</div>
                              <strong style={{ fontSize: '18px' }}>{project.name}</strong>
                            </div>
                            <span className="department-project-status" style={{ padding: '6px 10px', borderRadius: '999px', backgroundColor: '#e0f2fe', color: '#075985', fontSize: '12px', fontWeight: 700 }}>
                              {project.status}
                            </span>
                          </div>
                        </button>

                        <div style={{ display: 'flex', justifyContent: 'space-between', gap: '12px', alignItems: 'center', padding: '0 18px 16px', flexWrap: 'wrap' }}>
                          <span style={{ color: '#475569', fontWeight: 600 }}>{project.priority}</span>
                          <button
                            type="button"
                            className="department-team-toggle"
                            onClick={() => handleProjectTeamToggle(project)}
                            style={{
                              padding: '8px 12px',
                              border: '1px solid #dbe3ef',
                              borderRadius: '6px',
                              backgroundColor: '#f8fafc',
                              color: '#172033',
                              cursor: 'pointer',
                              fontWeight: 600,
                            }}
                          >
                            {teamMembersLoading[project.id] ? 'Loading...' : `${projectTeamMembers.length} team member${projectTeamMembers.length === 1 ? '' : 's'}`}
                          </button>
                        </div>

                        {expandedProjectId === project.id && (
                          <div style={{ padding: '0 18px 18px' }}>
                            {projectTeamMembers.length === 0 ? (
                              <p style={{ margin: 0, color: '#666' }}>No employees are assigned to this project.</p>
                            ) : (
                              <div style={{ display: 'grid', gap: '8px' }}>
                                {projectTeamMembers.map((member) => (
                                  <div key={member.id} className="department-team-member" style={{ padding: '10px 12px', border: '1px solid #e2e8f0', borderRadius: '6px', backgroundColor: '#f8fafc', color: '#334155' }}>
                                    <strong>{member.firstName} {member.lastName}</strong>
                                    <div style={{ marginTop: '4px', color: '#475569' }}>{member.role}</div>
                                  </div>
                                ))}
                              </div>
                            )}
                          </div>
                        )}
                      </div>
                    );
                  })
                )}
              </div>

              {selectedProject && (
                <div className="department-project-details" style={{ marginTop: '18px', border: '1px solid #dbe3ef', borderRadius: '10px', backgroundColor: '#fff', padding: '18px' }}>
                  <p style={{ margin: '0 0 8px', color: '#1976D2', fontSize: '12px', fontWeight: 700, letterSpacing: '0.12em', textTransform: 'uppercase' }}>Project details</p>
                  <h3 style={{ margin: '0 0 12px', color: '#333', fontSize: '22px' }}>{selectedProject.name}</h3>
                  <p style={{ margin: '0 0 18px', color: '#475569', lineHeight: 1.6 }}>{selectedProject.description || 'No description provided for this project.'}</p>

                  <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(160px, 1fr))', gap: '12px' }}>
                    <div className="department-detail-stat" style={{ padding: '12px', border: '1px solid #edf2f7', borderRadius: '8px', backgroundColor: '#f8fafc' }}>
                      <div style={{ color: '#64748b', fontSize: '11px', fontWeight: 700, letterSpacing: '0.04em', textTransform: 'uppercase', marginBottom: '6px' }}>Status</div>
                      <strong>{selectedProject.status}</strong>
                    </div>
                    <div className="department-detail-stat" style={{ padding: '12px', border: '1px solid #edf2f7', borderRadius: '8px', backgroundColor: '#f8fafc' }}>
                      <div style={{ color: '#64748b', fontSize: '11px', fontWeight: 700, letterSpacing: '0.04em', textTransform: 'uppercase', marginBottom: '6px' }}>Priority</div>
                      <strong>{selectedProject.priority}</strong>
                    </div>
                    <div className="department-detail-stat" style={{ padding: '12px', border: '1px solid #edf2f7', borderRadius: '8px', backgroundColor: '#f8fafc' }}>
                      <div style={{ color: '#64748b', fontSize: '11px', fontWeight: 700, letterSpacing: '0.04em', textTransform: 'uppercase', marginBottom: '6px' }}>Start date</div>
                      <strong>{selectedProject.startDate || 'Not set'}</strong>
                    </div>
                    <div className="department-detail-stat" style={{ padding: '12px', border: '1px solid #edf2f7', borderRadius: '8px', backgroundColor: '#f8fafc' }}>
                      <div style={{ color: '#64748b', fontSize: '11px', fontWeight: 700, letterSpacing: '0.04em', textTransform: 'uppercase', marginBottom: '6px' }}>End date</div>
                      <strong>{selectedProject.endDate || 'Not set'}</strong>
                    </div>
                  </div>
                </div>
              )}
            </section>
          )}
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '16px' }}>
          {departments.map((department) => (
            <button
              key={department.id}
              type="button"
              className="department-card"
              aria-pressed={selectedDepartment?.id === department.id}
              onClick={() => handleDepartmentSelect(department)}
              style={{ ...departmentCardStyle, borderColor: selectedDepartment?.id === department.id ? '#4CAF50' : '#ddd' }}
            >
              <h2 style={{ margin: '0 0 10px', color: '#333', fontSize: '20px' }}>{department.name}</h2>
              <p style={{ margin: '0 0 10px', color: '#1976D2', fontWeight: 700 }}>{employees.filter((employee) => employee.department?.id === department.id).length} employees</p>
              <p style={{ margin: 0, color: '#666', lineHeight: 1.5 }}>{department.description || 'No description provided.'}</p>
            </button>
          ))}
          </div>
        </>
      )}
    </main>
  );
}

const inputStyle = { width: '100%', padding: '9px', marginBottom: '14px', border: '1px solid #ccc', borderRadius: '4px', fontSize: '14px', boxSizing: 'border-box' as const };
const labelStyle = { display: 'block', marginBottom: '6px', fontWeight: '600', color: '#444' };
const errorStyle = { padding: '12px', backgroundColor: '#ffebee', border: '1px solid #f44336', borderRadius: '4px', color: '#c62828' };
const buttonStyle = (backgroundColor: string) => ({ padding: '10px 16px', backgroundColor, color: 'white', border: 0, borderRadius: '4px', cursor: backgroundColor === '#9e9e9e' ? 'not-allowed' : 'pointer', fontWeight: '600' as const });
const departmentCardStyle = { minHeight: '120px', padding: '20px', border: '1px solid #ddd', borderRadius: '8px', backgroundColor: '#fff', boxShadow: '0 2px 8px rgba(0,0,0,0.06)', cursor: 'pointer', textAlign: 'left' as const };
const employeeRowStyle = { display: 'grid', gridTemplateColumns: 'minmax(140px, 1fr) minmax(120px, 1fr) minmax(180px, 1fr)', gap: '12px', padding: '10px 12px', border: '1px solid #dce8dc', borderRadius: '4px', backgroundColor: '#fff', color: '#444' };

export default DepartmentsPage;