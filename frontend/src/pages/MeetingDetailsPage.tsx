import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { getMeetingById, type Meeting } from '../services/meetingApi';

const formatDate = (value?: string) => {
  if (!value) {
    return 'Not scheduled';
  }

  const date = new Date(`${value}T00:00:00`);
  return Number.isNaN(date.getTime())
    ? value
    : date.toLocaleDateString(undefined, { month: 'short', day: 'numeric', year: 'numeric' });
};

function MeetingDetailsPage() {
  const { meetingId } = useParams<{ meetingId: string }>();
  const navigate = useNavigate();
  const [meeting, setMeeting] = useState<Meeting | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [copyMessage, setCopyMessage] = useState<string | null>(null);

  useEffect(() => {
    if (!meetingId) {
      setError('Meeting id is missing.');
      setLoading(false);
      return;
    }

    const loadMeeting = async () => {
      try {
        setLoading(true);
        setError(null);
        const result = await getMeetingById(meetingId);
        setMeeting(result);
      } catch (loadError) {
        setError(loadError instanceof Error ? loadError.message : 'Unable to load meeting details.');
      } finally {
        setLoading(false);
      }
    };

    loadMeeting();
  }, [meetingId]);

  const handleJoinMeet = () => {
    if (!meeting?.googleMeetUrl) {
      return;
    }

    window.open(meeting.googleMeetUrl, '_blank', 'noopener,noreferrer');
  };

  const handleCopyLink = async () => {
    if (!meeting?.googleMeetUrl) {
      return;
    }

    try {
      await navigator.clipboard.writeText(meeting.googleMeetUrl);
      setCopyMessage('Meeting link copied');
    } catch {
      setCopyMessage('Unable to copy the link. Please copy it manually.');
    }

    window.setTimeout(() => setCopyMessage(null), 2200);
  };

  if (loading) {
    return (
      <main className="meetings-page">
        <div className="loading-state" aria-live="polite">Loading meeting details...</div>
      </main>
    );
  }

  if (error || !meeting) {
    return (
      <main className="meetings-page">
        <div className="error-state" role="alert">
          <strong>Unable to load meeting</strong>
          <p>{error ?? 'Meeting not found.'}</p>
          <button type="button" className="secondary-button" onClick={() => navigate('/meetings')}>
            Back to Meetings
          </button>
        </div>
      </main>
    );
  }

  return (
    <main className="meetings-page">
      <header className="meeting-page-header">
        <div>
          <p className="section-kicker">Operations</p>
          <h1>{meeting.title}</h1>
          <p className="section-subtitle">Meeting details and scheduling information.</p>
        </div>
        <button type="button" className="secondary-button" onClick={() => navigate('/meetings')}>
          Back to Meetings
        </button>
      </header>

      <section className="create-meeting-card" aria-live="polite">
        {copyMessage && (
          <div className="success-banner" role="status" aria-live="polite">
            {copyMessage}
          </div>
        )}

        <div className="meeting-card-header">
          <div>
            <p className="meeting-label">{meeting.projectName}</p>
            <h3>{meeting.title}</h3>
          </div>
          <span className={`status-badge status-${meeting.status.toLowerCase().replace(/\s+/g, '-')}`}>
            {meeting.status}
          </span>
        </div>

        <div className="meeting-card-meta">
          <div>
            <span className="meta-label">Date</span>
            <strong>{formatDate(meeting.meetingDate)}</strong>
          </div>
          <div>
            <span className="meta-label">Time</span>
            <strong>{meeting.startTime} - {meeting.endTime}</strong>
          </div>
          <div>
            <span className="meta-label">Participants</span>
            <strong>{meeting.participantCount}</strong>
          </div>
        </div>

        <div className="meeting-form" style={{ display: 'grid', gap: '18px' }}>
          <div className="field-group full-width">
            <label>Project</label>
            <div>
              {meeting.projectId ? (
                <Link to={`/projects/${meeting.projectId}`} className="primary-button" style={{ display: 'inline-flex', textDecoration: 'none' }}>
                  Open project: {meeting.projectName}
                </Link>
              ) : (
                <span>{meeting.projectName}</span>
              )}
            </div>
          </div>

          <div className="field-group full-width">
            <label>Description</label>
            <p className="meeting-description" style={{ margin: 0 }}>{meeting.description || 'No description provided.'}</p>
          </div>

          <div className="field-group full-width">
            <label>Agenda</label>
            <p className="meeting-description" style={{ margin: 0 }}>{meeting.agenda || 'No agenda provided.'}</p>
          </div>

          <div className="field-group full-width">
            <label>Participants ({meeting.participants?.length ?? meeting.participantIds?.length ?? 0})</label>
            {meeting.participants && meeting.participants.length > 0 ? (
              <div style={{ display: 'grid', gap: '10px' }}>
                {meeting.participants.map((participant) => (
                  <div key={participant.id} style={{ border: '1px solid #dbe3ef', borderRadius: '12px', padding: '12px 14px', background: '#f8fafc' }}>
                    <strong>{participant.firstName} {participant.lastName}</strong>
                    <div style={{ color: '#475569', fontSize: '14px', marginTop: '4px' }}>{participant.role || 'Team Member'}</div>
                    <div style={{ color: '#475569', fontSize: '14px' }}>{participant.email}</div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="meeting-description" style={{ margin: 0 }}>
                No participants assigned.
              </div>
            )}
          </div>

          <div className="field-group full-width">
            <label>Google Meet</label>
            {meeting.googleMeetUrl ? (
              <>
                <div style={{ marginBottom: '12px' }}>
                  <a className="meet-link" href={meeting.googleMeetUrl} target="_blank" rel="noopener noreferrer">
                    {meeting.googleMeetUrl}
                  </a>
                </div>
                <div style={{ display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
                  <button type="button" className="primary-button" onClick={handleJoinMeet}>
                    Join Google Meet
                  </button>
                  <button type="button" className="secondary-button" onClick={handleCopyLink}>
                    Copy Link
                  </button>
                </div>
              </>
            ) : (
              <span>No Google Meet link has been added for this meeting.</span>
            )}
          </div>
        </div>
      </section>
    </main>
  );
}

export default MeetingDetailsPage;
