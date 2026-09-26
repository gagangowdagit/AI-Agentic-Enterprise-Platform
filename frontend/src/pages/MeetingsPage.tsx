import { useEffect, useMemo, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import MeetingCard from '../components/MeetingCard';
import MeetingRow from '../components/MeetingRow';
import type { Meeting, MeetingStatus } from '../services/meetingApi';
import { getMeetings } from '../services/meetingApi';

const statusOptions: Array<'All Statuses' | MeetingStatus> = [
  'All Statuses',
  'Scheduled',
  'In Progress',
  'Completed',
  'Cancelled',
];

const isSameDay = (meetingDate: string) => {
  const meeting = new Date(`${meetingDate}T00:00:00`);
  const today = new Date();

  return meeting.getFullYear() === today.getFullYear()
    && meeting.getMonth() === today.getMonth()
    && meeting.getDate() === today.getDate();
};

function MeetingsPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const [meetings, setMeetings] = useState<Meeting[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [statusFilter, setStatusFilter] = useState<'All Statuses' | MeetingStatus>('All Statuses');
  const [projectFilter, setProjectFilter] = useState('All Projects');
  const [dateFilter, setDateFilter] = useState('');
  const [projectLookup, setProjectLookup] = useState<Map<string, number>>(new Map());
  const [selectedOverviewStatus, setSelectedOverviewStatus] = useState<'total' | 'upcoming' | 'completed' | null>(null);

  useEffect(() => {
    const fetchMeetings = async () => {
      try {
        setLoading(true);
        setError(null);

        const [data, projectData] = await Promise.all([
          getMeetings(),
          fetch('http://localhost:8080/api/v1/projects', {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' },
          }).then(async (response) => {
            if (!response.ok) {
              throw new Error('Failed to fetch projects');
            }
            return response.json();
          }),
        ]);

        setMeetings(data);

        const lookup = new Map<string, number>();
        projectData.forEach((project: { id: number; name: string }) => {
          lookup.set(project.name, Number(project.id));
        });
        setProjectLookup(lookup);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Unable to load meetings.');
      } finally {
        setLoading(false);
      }
    };

    fetchMeetings();
  }, []);

  useEffect(() => {
    const typedState = location.state as { meetingCreated?: boolean; meetingTitle?: string } | null;
    if (typedState?.meetingCreated) {
      setSuccessMessage(`Meeting "${typedState.meetingTitle ?? 'created'}" was scheduled successfully.`);
      window.history.replaceState({}, '', '/meetings');
    }
  }, [location.state]);

  const projectOptions = useMemo(() => {
    const uniqueProjects = new Set(meetings.map((meeting) => meeting.projectName));
    return ['All Projects', ...Array.from(uniqueProjects)];
  }, [meetings]);

  const filteredMeetings = useMemo(() => meetings.filter((meeting) => {
    const matchesSearch = meeting.title.toLowerCase().includes(searchTerm.toLowerCase().trim());
    const matchesStatus = statusFilter === 'All Statuses' || meeting.status === statusFilter;
    const matchesProject = projectFilter === 'All Projects' || meeting.projectName === projectFilter;
    const matchesDate = !dateFilter || meeting.meetingDate === dateFilter;

    return matchesSearch && matchesStatus && matchesProject && matchesDate;
  }), [meetings, searchTerm, statusFilter, projectFilter, dateFilter]);

  const stats = useMemo(() => {
    const total = meetings.length;
    const upcoming = meetings.filter((meeting) => meeting.status === 'Scheduled' || meeting.status === 'In Progress').length;
    const today = meetings.filter((meeting) => isSameDay(meeting.meetingDate)).length;
    const completed = meetings.filter((meeting) => meeting.status === 'Completed').length;

    return { total, upcoming, today, completed };
  }, [meetings]);

  const statusOverviewData = useMemo(() => {
    const totalProjects = Array.from(new Map(meetings.map((meeting) => [meeting.projectName, meeting])).values());
    const upcomingProjects = meetings.filter((meeting) => meeting.status === 'Scheduled' || meeting.status === 'In Progress');
    const completedProjects = meetings.filter((meeting) => meeting.status === 'Completed');

    return {
      total: totalProjects,
      upcoming: upcomingProjects,
      completed: completedProjects,
    };
  }, [meetings]);

  const selectedStatusProjects = useMemo(() => {
    const statusEntries = selectedOverviewStatus ? statusOverviewData[selectedOverviewStatus] ?? [] : [];
    const uniqueByProject = new Map<string, { projectName: string; meetingCount: number; projectId?: number }>();

    statusEntries.forEach((meeting) => {
      const existing = uniqueByProject.get(meeting.projectName);
      if (existing) {
        existing.meetingCount += 1;
        return;
      }

      uniqueByProject.set(meeting.projectName, {
        projectName: meeting.projectName,
        meetingCount: 1,
        projectId: projectLookup.get(meeting.projectName),
      });
    });

    return Array.from(uniqueByProject.values());
  }, [projectLookup, selectedOverviewStatus, statusOverviewData]);

  const upcomingMeetings = filteredMeetings.filter((meeting) => meeting.status !== 'Completed' && meeting.status !== 'Cancelled');
  const todayMeetings = filteredMeetings.filter((meeting) => isSameDay(meeting.meetingDate));
  const recentMeetings = filteredMeetings.filter((meeting) => meeting.status === 'Completed').slice(0, 4);

  const handleRetry = () => {
    window.location.reload();
  };

  return (
    <main className="meetings-page">
      <header className="meeting-page-header">
        <div>
          <p className="section-kicker">Operations</p>
          <h1>Meetings</h1>
          <p className="section-subtitle">Manage your meetings, agendas, participants, and meeting information.</p>
        </div>

        <button type="button" className="primary-button" aria-label="Create a new meeting" onClick={() => navigate('/meetings/create')}>
          + Create Meeting
        </button>
      </header>

      {successMessage && (
        <div className="success-banner" role="status" aria-live="polite">
          {successMessage}
        </div>
      )}

      {loading && (
        <div className="loading-state" aria-live="polite">
          Loading meetings...
        </div>
      )}

      {!loading && error && (
        <div className="error-state" role="alert">
          <strong>Unable to load meetings</strong>
          <p>{error}</p>
          <button type="button" className="secondary-button" onClick={handleRetry}>
            Try again
          </button>
        </div>
      )}

      {!loading && !error && (
        <>
          <section className="overview-grid" aria-label="Meeting summary overview">
            {[
              { key: 'total', label: 'Total Meetings', value: stats.total },
              { key: 'upcoming', label: 'Upcoming', value: stats.upcoming },
              { key: 'today', label: 'Today', value: stats.today },
              { key: 'completed', label: 'Completed', value: stats.completed },
            ].map((card) => (
              <article
                key={card.key}
                className={card.key === 'today' ? 'overview-card non-status-card' : 'overview-card'}
                onClick={() => {
                  if (card.key === 'today') {
                    return;
                  }

                  setSelectedOverviewStatus((current) => current === card.key ? null : card.key as 'total' | 'upcoming' | 'completed');
                }}
                role={card.key === 'today' ? undefined : 'button'}
                tabIndex={card.key === 'today' ? -1 : 0}
                onKeyDown={(event) => {
                  if (card.key === 'today') {
                    return;
                  }

                  if (event.key === 'Enter' || event.key === ' ') {
                    event.preventDefault();
                    setSelectedOverviewStatus((current) => current === card.key ? null : card.key as 'total' | 'upcoming' | 'completed');
                  }
                }}
              >
                <span>{card.label}</span>
                <strong>{card.value}</strong>
              </article>
            ))}
          </section>

          {selectedOverviewStatus && (
            <section className="status-project-list" aria-live="polite">
              <div className="status-project-list-header">
                <h2>
                  {selectedOverviewStatus === 'total' ? 'Projects in all meetings' : selectedOverviewStatus === 'upcoming' ? 'Projects with upcoming meetings' : 'Projects with completed meetings'}
                </h2>
                <button type="button" className="secondary-button" onClick={() => setSelectedOverviewStatus(null)}>Hide list</button>
              </div>

              {selectedStatusProjects.length === 0 ? (
                <div className="empty-state">No projects found for this status.</div>
              ) : (
                <div className="status-project-items">
                  {selectedStatusProjects.map((project) => (
                    <div key={project.projectName} className="status-project-item">
                      <div>
                        <h3>{project.projectName}</h3>
                        <p>{project.meetingCount} meeting{project.meetingCount > 1 ? 's' : ''}</p>
                      </div>
                      <button
                        type="button"
                        className="primary-button"
                        onClick={() => {
                          if (project.projectId) {
                            navigate(`/projects/${project.projectId}`);
                            return;
                          }

                          navigate('/projects');
                        }}
                      >
                        View more
                      </button>
                    </div>
                  ))}
                </div>
              )}
            </section>
          )}

          <section className="filters-panel" aria-label="Meeting filters">
            <div className="field-group field-search">
              <label htmlFor="meeting-search">Search</label>
              <input
                id="meeting-search"
                type="search"
                value={searchTerm}
                onChange={(event) => setSearchTerm(event.target.value)}
                placeholder="Search meetings"
              />
            </div>

            <div className="field-group">
              <label htmlFor="status-filter">Status</label>
              <select id="status-filter" value={statusFilter} onChange={(event) => setStatusFilter(event.target.value as 'All Statuses' | MeetingStatus)}>
                {statusOptions.map((status) => (
                  <option key={status} value={status}>{status}</option>
                ))}
              </select>
            </div>

            <div className="field-group">
              <label htmlFor="project-filter">Project</label>
              <select id="project-filter" value={projectFilter} onChange={(event) => setProjectFilter(event.target.value)}>
                {projectOptions.map((project) => (
                  <option key={project} value={project}>{project}</option>
                ))}
              </select>
            </div>

            <div className="field-group">
              <label htmlFor="date-filter">Date</label>
              <input
                id="date-filter"
                type="date"
                value={dateFilter}
                onChange={(event) => setDateFilter(event.target.value)}
              />
            </div>
          </section>

          {filteredMeetings.length === 0 && (
            <div className="empty-state wide-empty-state">
              No meetings match your current search and filters.
            </div>
          )}

          {filteredMeetings.length > 0 && (
            <>
              <section className="meeting-section">
                <div className="section-heading">
                  <h2>Upcoming Meetings</h2>
                </div>

                {upcomingMeetings.length === 0 ? (
                  <div className="empty-state">No upcoming meetings</div>
                ) : (
                  <div className="meeting-card-grid">
                    {upcomingMeetings.map((meeting) => (
                      <MeetingCard key={meeting.id} meeting={meeting} />
                    ))}
                  </div>
                )}
              </section>

              <section className="meeting-section">
                <div className="section-heading">
                  <h2>Today's Meetings</h2>
                </div>

                {todayMeetings.length === 0 ? (
                  <div className="empty-state">No meetings scheduled for today</div>
                ) : (
                  <div className="meeting-list">
                    {todayMeetings.map((meeting) => (
                      <div key={meeting.id} className="meeting-inline-item">
                        <div>
                          <h3>{meeting.title}</h3>
                          <p>{meeting.startTime} - {meeting.endTime}</p>
                        </div>
                        <div>
                          <span>{meeting.projectName}</span>
                        </div>
                        <div>
                          <span>{meeting.participantCount} participants</span>
                        </div>
                        <span className={`status-badge status-${meeting.status.toLowerCase().replace(/\s+/g, '-')}`}>
                          {meeting.status}
                        </span>
                      </div>
                    ))}
                  </div>
                )}
              </section>

              <section className="meeting-section">
                <div className="section-heading">
                  <h2>Recent Meetings</h2>
                </div>

                {recentMeetings.length === 0 ? (
                  <div className="empty-state">No recent meetings found</div>
                ) : (
                  <div className="table-wrap">
                    <table className="meeting-table">
                      <thead>
                        <tr>
                          <th>Meeting</th>
                          <th>Date</th>
                          <th>Project</th>
                          <th>Participants</th>
                          <th>Status</th>
                          <th>Action</th>
                        </tr>
                      </thead>
                      <tbody>
                        {recentMeetings.map((meeting) => (
                          <MeetingRow key={meeting.id} meeting={meeting} />
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </section>
            </>
          )}
        </>
      )}
    </main>
  );
}

export default MeetingsPage;
