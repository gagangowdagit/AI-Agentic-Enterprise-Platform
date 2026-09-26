import { useEffect, useRef, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import {
  cancelMeeting,
  deleteMeeting,
  getMeetingById,
  parseAgendaItems,
  regenerateMeetingSummary,
  type Meeting,
  uploadMeetingTranscript,
} from '../services/meetingApi';

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
  const [isUploadingTranscript, setIsUploadingTranscript] = useState(false);
  const fileInputRef = useRef<HTMLInputElement | null>(null);

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

  const handleEditMeeting = () => {
    if (!meetingId) {
      return;
    }
    navigate(`/meetings/${meetingId}/edit`);
  };

  const handleCancelMeeting = async () => {
    if (!meetingId) {
      return;
    }

    const confirmed = window.confirm('Are you sure you want to cancel this meeting?');
    if (!confirmed) {
      return;
    }

    try {
      const updatedMeeting = await cancelMeeting(meetingId);
      setMeeting(updatedMeeting);
      setCopyMessage('Meeting cancelled successfully');
      window.setTimeout(() => setCopyMessage(null), 2200);
    } catch (cancelError) {
      setError(cancelError instanceof Error ? cancelError.message : 'Unable to cancel meeting.');
    }
  };

  const handleDeleteMeeting = async () => {
    if (!meetingId) {
      return;
    }

    const confirmed = window.confirm('This will permanently delete the meeting. Continue?');
    if (!confirmed) {
      return;
    }

    try {
      await deleteMeeting(meetingId);
      navigate('/meetings', {
        state: {
          meetingCreated: true,
          meetingTitle: meeting?.title ?? 'Meeting',
          action: 'deleted',
        },
      });
    } catch (deleteError) {
      setError(deleteError instanceof Error ? deleteError.message : 'Unable to delete meeting.');
    }
  };

  const handleTranscriptUpload = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file || !meetingId) {
      return;
    }

    try {
      setIsUploadingTranscript(true);
      const transcriptText = await file.text();
      const updatedMeeting = await uploadMeetingTranscript(meetingId, transcriptText, file.name);
      setMeeting(updatedMeeting);
      setCopyMessage('Transcript uploaded and meeting summary refreshed.');
      window.setTimeout(() => setCopyMessage(null), 2600);
    } catch (uploadError) {
      setError(uploadError instanceof Error ? uploadError.message : 'Unable to upload transcript.');
    } finally {
      setIsUploadingTranscript(false);
      if (fileInputRef.current) {
        fileInputRef.current.value = '';
      }
    }
  };

  const handleRegenerateSummary = async () => {
    if (!meetingId) {
      return;
    }

    try {
      const updatedMeeting = await regenerateMeetingSummary(meetingId);
      setMeeting(updatedMeeting);
      setCopyMessage('AI summary regenerated.');
      window.setTimeout(() => setCopyMessage(null), 2200);
    } catch (summaryError) {
      setError(summaryError instanceof Error ? summaryError.message : 'Unable to regenerate summary.');
    }
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
        <div style={{ display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
          <button type="button" className="secondary-button" onClick={handleEditMeeting}>
            Edit Meeting
          </button>
          <button type="button" className="secondary-button" onClick={handleCancelMeeting} disabled={meeting?.status === 'Cancelled'}>
            Cancel Meeting
          </button>
          <button type="button" className="secondary-button" onClick={handleDeleteMeeting}>
            Delete Meeting
          </button>
          <button type="button" className="secondary-button" onClick={() => navigate('/meetings')}>
            Back to Meetings
          </button>
        </div>
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
            {meeting.agendaItems && meeting.agendaItems.length > 0 ? (
              <ol className="meeting-agenda-list">
                {meeting.agendaItems.map((item, index) => (
                  <li key={`${item.id ?? index}-${item.title}`}>
                    <span className="meeting-agenda-index">{String(index + 1).padStart(2, '0')}</span> {item.title}
                  </li>
                ))}
              </ol>
            ) : (
              <p className="meeting-description" style={{ margin: 0 }}>{parseAgendaItems(meeting.agenda).length > 0 ? 'Agenda available.' : 'No agenda has been added for this meeting.'}</p>
            )}
          </div>

          <div className="field-group full-width">
            <label>Participants ({meeting.participants?.length ?? meeting.participantIds?.length ?? 0})</label>
            {meeting.participants && meeting.participants.length > 0 ? (
              <div className="meeting-participants-list">
                {meeting.participants.map((participant) => (
                  <div key={participant.id} className="meeting-participant-item">
                    <strong>{participant.firstName} {participant.lastName}</strong>
                    <div className="meeting-participant-meta meeting-participant-role">{participant.role || 'Team Member'}</div>
                    <div className="meeting-participant-meta">{participant.email}</div>
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

          <div className="field-group full-width">
            <label>AI Meeting Intelligence</label>
            <div className="meeting-intelligence-panel">
              <div className="meeting-intelligence-header">
                <span className="meeting-intelligence-label">
                  Stored transcript & summary
                </span>
                <button type="button" className="secondary-button" onClick={handleRegenerateSummary}>
                  Regenerate summary
                </button>
              </div>

              <div>
                <strong className="meeting-ai-subtitle">Transcript</strong>
                {meeting.transcriptText ? (
                  <>
                    <div className="meeting-transcript-meta">
                      {meeting.transcriptFileName || 'meeting-transcript.txt'}
                      {meeting.transcriptUploadedAt ? ` • ${new Date(meeting.transcriptUploadedAt).toLocaleString()}` : ''}
                    </div>
                    <div className="meeting-transcript-box">
                      {meeting.transcriptText}
                    </div>
                  </>
                ) : (
                  <div className="meeting-empty-inline">No transcript has been uploaded for this meeting yet.</div>
                )}
              </div>

              <div>
                <strong className="meeting-ai-subtitle">AI summary</strong>
                {meeting.aiSummary ? (
                  <div className="meeting-ai-output">
                    {meeting.aiSummary}
                  </div>
                ) : (
                  <div className="meeting-empty-inline">Upload a transcript to generate a stored summary.</div>
                )}
              </div>

              <div className="meeting-ai-groups">
                <div>
                  <strong className="meeting-ai-subtitle">Action items</strong>
                  {meeting.aiActionItems && meeting.aiActionItems.length > 0 ? (
                    <ul className="meeting-ai-list">
                      {meeting.aiActionItems.map((item, index) => <li key={`${item}-${index}`}>{item}</li>)}
                    </ul>
                  ) : (
                    <div className="meeting-empty-inline">No action items extracted yet.</div>
                  )}
                </div>

                <div>
                  <strong className="meeting-ai-subtitle">Decisions</strong>
                  {meeting.aiDecisions && meeting.aiDecisions.length > 0 ? (
                    <ul className="meeting-ai-list">
                      {meeting.aiDecisions.map((item, index) => <li key={`${item}-${index}`}>{item}</li>)}
                    </ul>
                  ) : (
                    <div className="meeting-empty-inline">No decisions captured yet.</div>
                  )}
                </div>
              </div>

              <div className="meeting-intelligence-footer">
                <div className="meeting-intelligence-note">
                  Note: transcript summaries are stored locally and are not yet wired into the broader Nova/RAG system.
                </div>
                <div className="meeting-intelligence-actions">
                  <label className="secondary-button" style={{ cursor: 'pointer', display: 'inline-flex', alignItems: 'center', justifyContent: 'center' }}>
                    {isUploadingTranscript ? 'Uploading...' : 'Upload transcript'}
                    <input ref={fileInputRef} type="file" accept=".txt,.md,.csv,.json" onChange={handleTranscriptUpload} style={{ display: 'none' }} />
                  </label>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </main>
  );
}

export default MeetingDetailsPage;
