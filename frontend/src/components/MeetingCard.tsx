import { useNavigate } from 'react-router-dom';
import type { Meeting } from '../services/meetingApi';

interface MeetingCardProps {
  meeting: Meeting;
}

const formatBadgeClass = (status: string) => status.toLowerCase().replace(/\s+/g, '-');

function MeetingCard({ meeting }: MeetingCardProps) {
  const navigate = useNavigate();

  const handleJoinMeet = () => {
    if (!meeting.googleMeetUrl) {
      return;
    }

    window.open(meeting.googleMeetUrl, '_blank', 'noopener,noreferrer');
  };

  return (
    <article className="meeting-card" aria-label={`${meeting.title} meeting card`}>
      <div className="meeting-card-header">
        <div>
          <p className="meeting-label">{meeting.projectName}</p>
          <h3>{meeting.title}</h3>
        </div>
        <span className={`status-badge status-${formatBadgeClass(meeting.status)}`}>
          {meeting.status}
        </span>
      </div>

      <div className="meeting-card-meta">
        <div>
          <span className="meta-label">Date</span>
          <strong>{new Date(`${meeting.meetingDate}T00:00:00`).toLocaleDateString(undefined, { month: 'short', day: 'numeric', year: 'numeric' })}</strong>
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

      <p className="meeting-description">{meeting.description}</p>

      <div className="meeting-card-footer">
        {meeting.googleMeetUrl ? (
          <button type="button" className="secondary-button" onClick={handleJoinMeet} aria-label={`Join Google Meet for ${meeting.title}`}>
            Join Google Meet
          </button>
        ) : null}
        <button type="button" className="secondary-button" aria-label={`View details for ${meeting.title}`} onClick={() => navigate(`/meetings/${meeting.id}`)}>
          View Details
        </button>
      </div>
    </article>
  );
}

export default MeetingCard;
