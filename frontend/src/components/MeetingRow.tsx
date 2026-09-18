import type { Meeting } from '../services/meetingApi';

interface MeetingRowProps {
  meeting: Meeting;
}

const formatBadgeClass = (status: string) => status.toLowerCase().replace(/\s+/g, '-');

function MeetingRow({ meeting }: MeetingRowProps) {
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
        <button type="button" className="table-action-button" aria-label={`View details for ${meeting.title}`}>
          View Details
        </button>
      </td>
    </tr>
  );
}

export default MeetingRow;
