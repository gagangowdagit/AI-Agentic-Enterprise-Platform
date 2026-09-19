import { useNavigate } from 'react-router-dom';
import type { Meeting } from '../services/meetingApi';

interface MeetingRowProps {
  meeting: Meeting;
}

const formatBadgeClass = (status: string) => status.toLowerCase().replace(/\s+/g, '-');

function MeetingRow({ meeting }: MeetingRowProps) {
  const navigate = useNavigate();

  const handleJoinMeet = () => {
    if (!meeting.googleMeetUrl) {
      return;
    }

    window.open(meeting.googleMeetUrl, '_blank', 'noopener,noreferrer');
  };

  return (
    <tr className="meeting-table-row">
      <td>{meeting.title}</td>
      <td>{new Date(`${meeting.meetingDate}T00:00:00`).toLocaleDateString(undefined, { month: 'short', day: 'numeric', year: 'numeric' })}</td>
      <td>{meeting.projectName}</td>
      <td>{meeting.participantCount}</td>
      <td>
        <span className={`status-badge status-${formatBadgeClass(meeting.status)}`}>
          {meeting.status}
        </span>
      </td>
      <td>
        <div style={{ display: 'flex', gap: '8px', justifyContent: 'flex-end', flexWrap: 'wrap' }}>
          {meeting.googleMeetUrl ? (
            <button type="button" className="table-action-button" aria-label={`Join Google Meet for ${meeting.title}`} onClick={handleJoinMeet}>
              Join
            </button>
          ) : null}
          <button type="button" className="table-action-button" aria-label={`View details for ${meeting.title}`} onClick={() => navigate(`/meetings/${meeting.id}`)}>
            View Details
          </button>
        </div>
      </td>
    </tr>
  );
}

export default MeetingRow;
