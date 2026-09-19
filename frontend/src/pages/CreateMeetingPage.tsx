import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { createMeeting, type CreateMeetingRequest } from '../services/meetingApi';
import { getProjects, type Project } from '../services/projectApi';
import { getEmployees, type Employee } from '../services/departmentApi';

interface MeetingFormState {
  title: string;
  description: string;
  projectId: string;
  projectName: string;
  meetingDate: string;
  startTime: string;
  endTime: string;
  googleMeetUrl: string;
  agenda: string;
}

const initialState: MeetingFormState = {
  title: '',
  description: '',
  projectId: '',
  projectName: '',
  meetingDate: '',
  startTime: '',
  endTime: '',
  googleMeetUrl: '',
  agenda: '',
};

const isValidUrl = (value: string) => {
  if (!value.trim()) {
    return true;
  }

  try {
    const parsed = new URL(value.trim());
    return ['http:', 'https:'].includes(parsed.protocol) && !!parsed.host;
  } catch {
    return false;
  }
};

const formatTimeForComparison = (time: string) => {
  if (!time) {
    return null;
  }

  const [hours, minutes] = time.split(':').map(Number);
  if (Number.isNaN(hours) || Number.isNaN(minutes)) {
    return null;
  }

  return hours * 60 + minutes;
};

function CreateMeetingPage() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState<MeetingFormState>(initialState);
  const [projects, setProjects] = useState<Project[]>([]);
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [selectedParticipantIds, setSelectedParticipantIds] = useState<number[]>([]);
  const [participantQuery, setParticipantQuery] = useState('');
  const [creating, setCreating] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    const fetchDependencies = async () => {
      try {
        const [projectData, employeeData] = await Promise.all([getProjects(), getEmployees()]);
        setProjects(projectData);
        setEmployees(employeeData);
      } catch (err) {
        setErrorMessage(err instanceof Error ? err.message : 'Unable to load projects and employees.');
      }
    };

    fetchDependencies();
  }, []);

  const filteredEmployees = useMemo(() => {
    const query = participantQuery.trim().toLowerCase();
    return employees.filter((employee) => {
      const fullName = `${employee.firstName} ${employee.lastName}`.toLowerCase();
      if (!query) {
        return true;
      }

      return fullName.includes(query) || employee.email.toLowerCase().includes(query) || employee.role.toLowerCase().includes(query);
    });
  }, [employees, participantQuery]);

  const selectedParticipants = useMemo(
    () => employees.filter((employee) => selectedParticipantIds.includes(employee.id)),
    [employees, selectedParticipantIds],
  );

  const handleInputChange = (field: keyof MeetingFormState, value: string) => {
    setFormData((current) => ({ ...current, [field]: value }));
    setFieldErrors((current) => ({ ...current, [field]: '' }));
  };

  const toggleParticipant = (employeeId: number) => {
    setSelectedParticipantIds((current) => {
      if (current.includes(employeeId)) {
        return current.filter((id) => id !== employeeId);
      }

      return [...current, employeeId];
    });
    setFieldErrors((current) => ({ ...current, participants: '' }));
  };

  const handleProjectChange = (projectId: string) => {
    const selectedProject = projects.find((project) => String(project.id) === projectId);
    setFormData((current) => ({
      ...current,
      projectId,
      projectName: selectedProject?.name ?? '',
    }));
    setFieldErrors((current) => ({ ...current, projectId: '' }));
  };

  const validateForm = () => {
    const errors: Record<string, string> = {};

    if (!formData.title.trim()) {
      errors.title = 'Meeting title is required.';
    }

    if (!formData.projectId) {
      errors.projectId = 'Please select a project.';
    }

    if (!formData.meetingDate) {
      errors.meetingDate = 'Meeting date is required.';
    }

    if (!formData.startTime) {
      errors.startTime = 'Start time is required.';
    }

    if (!formData.endTime) {
      errors.endTime = 'End time is required.';
    }

    const startMinutes = formatTimeForComparison(formData.startTime);
    const endMinutes = formatTimeForComparison(formData.endTime);

    if (startMinutes !== null && endMinutes !== null && endMinutes <= startMinutes) {
      errors.endTime = 'End time must be after start time.';
    }

    if (formData.googleMeetUrl && !isValidUrl(formData.googleMeetUrl)) {
      errors.googleMeetUrl = 'Please enter a valid URL, such as https://meet.google.com/abc-defg-hij.';
    }

    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setErrorMessage(null);

    if (!validateForm()) {
      return;
    }

    setCreating(true);

    try {
      const payload: CreateMeetingRequest = {
        title: formData.title.trim(),
        description: formData.description.trim() || undefined,
        projectId: formData.projectId,
        projectName: formData.projectName,
        meetingDate: formData.meetingDate,
        startTime: formData.startTime,
        endTime: formData.endTime,
        googleMeetUrl: formData.googleMeetUrl.trim() || undefined,
        agenda: formData.agenda.trim() || undefined,
        participantIds: selectedParticipantIds,
      };

      const newMeeting = await createMeeting(payload);
      navigate('/meetings', {
        state: {
          meetingCreated: true,
          meetingTitle: newMeeting.title,
        },
      });
    } catch (err) {
      setErrorMessage(err instanceof Error ? err.message : 'Unable to create the meeting.');
    } finally {
      setCreating(false);
    }
  };

  return (
    <main className="meetings-page">
      <header className="meeting-page-header">
        <div>
          <p className="section-kicker">Operations</p>
          <h1>Create Meeting</h1>
        </div>
      </header>

      <section className="create-meeting-card">
        {errorMessage && (
          <div className="error-state" role="alert">
            {errorMessage}
          </div>
        )}

        <form onSubmit={handleSubmit} className="meeting-form">
          <div className="form-grid">
            <div className="field-group full-width">
              <label htmlFor="meeting-title">Meeting Title</label>
              <input
                id="meeting-title"
                type="text"
                value={formData.title}
                onChange={(event) => handleInputChange('title', event.target.value)}
                placeholder="Quarterly planning session"
              />
              {fieldErrors.title && <span className="form-error">{fieldErrors.title}</span>}
            </div>

            <div className="field-group full-width">
              <label htmlFor="meeting-description">Description</label>
              <textarea
                id="meeting-description"
                rows={3}
                value={formData.description}
                onChange={(event) => handleInputChange('description', event.target.value)}
                placeholder="Optional notes for the meeting"
              />
            </div>

            <div className="field-group">
              <label htmlFor="meeting-project">Project</label>
              <select
                id="meeting-project"
                value={formData.projectId}
                onChange={(event) => handleProjectChange(event.target.value)}
              >
                <option value="">Select a project</option>
                {projects.map((project) => (
                  <option key={project.id} value={String(project.id)}>{project.name}</option>
                ))}
              </select>
              {fieldErrors.projectId && <span className="form-error">{fieldErrors.projectId}</span>}
            </div>

            <div className="field-group">
              <label htmlFor="meeting-date">Meeting Date</label>
              <input
                id="meeting-date"
                type="date"
                value={formData.meetingDate}
                onChange={(event) => handleInputChange('meetingDate', event.target.value)}
              />
              {fieldErrors.meetingDate && <span className="form-error">{fieldErrors.meetingDate}</span>}
            </div>

            <div className="field-group">
              <label htmlFor="meeting-start-time">Start Time</label>
              <input
                id="meeting-start-time"
                type="time"
                value={formData.startTime}
                onChange={(event) => handleInputChange('startTime', event.target.value)}
              />
              {fieldErrors.startTime && <span className="form-error">{fieldErrors.startTime}</span>}
            </div>

            <div className="field-group">
              <label htmlFor="meeting-end-time">End Time</label>
              <input
                id="meeting-end-time"
                type="time"
                value={formData.endTime}
                onChange={(event) => handleInputChange('endTime', event.target.value)}
              />
              {fieldErrors.endTime && <span className="form-error">{fieldErrors.endTime}</span>}
            </div>

            <div className="field-group full-width">
              <label htmlFor="meeting-google-link">Google Meet Link</label>
              <input
                id="meeting-google-link"
                type="url"
                value={formData.googleMeetUrl}
                onChange={(event) => handleInputChange('googleMeetUrl', event.target.value)}
                placeholder="https://meet.google.com/..."
              />
              {fieldErrors.googleMeetUrl && <span className="form-error">{fieldErrors.googleMeetUrl}</span>}
            </div>

            <div className="field-group full-width">
              <label htmlFor="meeting-agenda">Agenda</label>
              <textarea
                id="meeting-agenda"
                rows={5}
                value={formData.agenda}
                onChange={(event) => handleInputChange('agenda', event.target.value)}
                placeholder="Outline the discussion points for the meeting"
              />
            </div>

            <div className="field-group full-width participant-field">
              <label>Participants</label>
              <input
                type="text"
                value={participantQuery}
                onChange={(event) => setParticipantQuery(event.target.value)}
                placeholder="Search employees..."
              />

              <div className="participants-list">
                {filteredEmployees.length === 0 ? (
                  <span className="participant-empty">No employees found.</span>
                ) : (
                  filteredEmployees.map((employee) => {
                    const isSelected = selectedParticipantIds.includes(employee.id);
                    return (
                      <button
                        type="button"
                        key={employee.id}
                        className={isSelected ? 'participant-chip selected' : 'participant-chip'}
                        onClick={() => toggleParticipant(employee.id)}
                      >
                        {employee.firstName} {employee.lastName}
                      </button>
                    );
                  })
                )}
              </div>

              <div className="selected-participants-wrap">
                <span className="selected-participants-label">Selected:</span>
                <div className="selected-participants">
                  {selectedParticipants.length === 0 ? (
                    <span className="participant-empty">No participants selected</span>
                  ) : (
                    selectedParticipants.map((employee) => (
                      <button
                        type="button"
                        key={employee.id}
                        className="selected-participant"
                        onClick={() => toggleParticipant(employee.id)}
                      >
                        {employee.firstName} {employee.lastName} ×
                      </button>
                    ))
                  )}
                </div>
              </div>
              {fieldErrors.participants && <span className="form-error">{fieldErrors.participants}</span>}
            </div>
          </div>

          <div className="form-actions">
            <button type="button" className="secondary-button" onClick={() => navigate('/meetings')}>
              Cancel
            </button>
            <button type="submit" className="primary-button" disabled={creating}>
              {creating ? 'Creating...' : 'Create Meeting'}
            </button>
          </div>
        </form>
      </section>
    </main>
  );
}

export default CreateMeetingPage;
