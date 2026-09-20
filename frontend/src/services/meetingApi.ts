export type MeetingStatus = 'Scheduled' | 'In Progress' | 'In_Progress' | 'Completed' | 'Cancelled';

export interface MeetingParticipant {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  role?: string;
  department?: { id: number; name: string } | null;
}

export interface AgendaItem {
  id?: number;
  title: string;
  displayOrder: number;
}

export interface Meeting {
  id: number;
  title: string;
  description: string;
  projectId: string;
  projectName: string;
  meetingDate: string;
  startTime: string;
  endTime: string;
  participantCount: number;
  status: MeetingStatus;
  googleMeetUrl: string;
  agenda?: string;
  agendaItems?: AgendaItem[];
  participantIds?: number[];
  participants?: MeetingParticipant[];
  transcriptText?: string;
  transcriptFileName?: string;
  transcriptUploadedAt?: string;
  aiSummary?: string;
  aiActionItems?: string[];
  aiDecisions?: string[];
  createdBy?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateMeetingRequest {
  title: string;
  description?: string;
  projectId: string;
  projectName?: string;
  meetingDate: string;
  startTime: string;
  endTime: string;
  googleMeetUrl?: string;
  agenda?: string;
  agendaItems?: AgendaItem[];
  participantIds?: number[];
}

const normalizeMeetingStatus = (status?: string): MeetingStatus => {
  if (status === 'In_Progress') {
    return 'In Progress';
  }

  if (status === 'In Progress' || status === 'Scheduled' || status === 'Completed' || status === 'Cancelled') {
    return status;
  }

  return 'Scheduled';
};

const normalizeParticipant = (participant: Partial<MeetingParticipant> | null | undefined): MeetingParticipant | null => {
  if (!participant) {
    return null;
  }

  return {
    id: Number(participant.id ?? 0),
    firstName: participant.firstName ?? '',
    lastName: participant.lastName ?? '',
    email: participant.email ?? '',
    role: participant.role,
    department: participant.department ?? null,
  };
};

export const parseAgendaItems = (agenda?: string): AgendaItem[] => {
  if (!agenda || !agenda.trim()) {
    return [];
  }

  return agenda
    .split(/\r?\n/)
    .map((line) => line.trim())
    .filter(Boolean)
    .map((line, index) => {
      const cleanLine = line.replace(/^\d+\.\s*/, '').trim();
      return {
        id: index + 1,
        title: cleanLine,
        displayOrder: index + 1,
      };
    })
    .filter((item) => item.title);
};

const normalizeMeeting = (meeting: Partial<Meeting>): Meeting => ({
  id: Number(meeting.id ?? 0),
  title: meeting.title ?? '',
  description: meeting.description ?? '',
  projectId: meeting.projectId ?? '',
  projectName: meeting.projectName ?? 'Unassigned Project',
  meetingDate: meeting.meetingDate ?? '',
  startTime: meeting.startTime ?? '',
  endTime: meeting.endTime ?? '',
  participantCount: Number(meeting.participantCount ?? meeting.participants?.length ?? 0),
  status: normalizeMeetingStatus(meeting.status),
  googleMeetUrl: meeting.googleMeetUrl ?? '',
  agenda: meeting.agenda ?? '',
  agendaItems: meeting.agendaItems ?? parseAgendaItems(meeting.agenda),
  participantIds: meeting.participantIds ?? [],
  participants: Array.isArray(meeting.participants)
    ? meeting.participants.map((participant) => normalizeParticipant(participant)).filter((participant): participant is MeetingParticipant => participant !== null)
    : [],
  transcriptText: meeting.transcriptText ?? '',
  transcriptFileName: meeting.transcriptFileName ?? '',
  transcriptUploadedAt: meeting.transcriptUploadedAt ?? '',
  aiSummary: meeting.aiSummary ?? '',
  aiActionItems: Array.isArray(meeting.aiActionItems) ? meeting.aiActionItems : [],
  aiDecisions: Array.isArray(meeting.aiDecisions) ? meeting.aiDecisions : [],
  createdBy: meeting.createdBy,
  createdAt: meeting.createdAt,
  updatedAt: meeting.updatedAt,
});

const toISODate = (offsetDays: number) => {
  const date = new Date();
  date.setHours(0, 0, 0, 0);
  date.setDate(date.getDate() + offsetDays);
  return date.toISOString().split('T')[0];
};

export const mockMeetings: Meeting[] = [
  {
    id: 1,
    title: 'Sprint Planning',
    description: 'Quarterly sprint planning and capacity review for the AI platform team.',
    projectId: 'PRJ-1042',
    projectName: 'AI Platform',
    meetingDate: toISODate(1),
    startTime: '09:30',
    endTime: '10:30',
    participantCount: 8,
    status: 'Scheduled',
    googleMeetUrl: 'https://meet.google.com/abc-defg-hij',
  },
  {
    id: 2,
    title: 'AI Platform Architecture Review',
    description: 'Architecture review of the knowledge graph and RAG integrations.',
    projectId: 'PRJ-1045',
    projectName: 'AI Platform',
    meetingDate: toISODate(3),
    startTime: '14:00',
    endTime: '15:00',
    participantCount: 11,
    status: 'In Progress',
    googleMeetUrl: 'https://meet.google.com/xyz-abcd-efg',
  },
  {
    id: 3,
    title: 'Backend Integration Meeting',
    description: 'Review integration points, API dependencies, and deployment readiness.',
    projectId: 'PRJ-2048',
    projectName: 'Operations Suite',
    meetingDate: toISODate(0),
    startTime: '11:00',
    endTime: '12:00',
    participantCount: 7,
    status: 'Scheduled',
    googleMeetUrl: 'https://meet.google.com/ops-backend-review',
  },
  {
    id: 4,
    title: 'Frontend Progress Review',
    description: 'Validate UI milestones, QA checklist, and stakeholder feedback.',
    projectId: 'PRJ-1187',
    projectName: 'Client Portal',
    meetingDate: toISODate(-2),
    startTime: '16:00',
    endTime: '16:45',
    participantCount: 6,
    status: 'Completed',
    googleMeetUrl: 'https://meet.google.com/front-end-review',
  },
  {
    id: 5,
    title: 'Vendor Sync',
    description: 'Align on third-party integration milestones and blockers.',
    projectId: 'PRJ-2050',
    projectName: 'Vendor Collaboration',
    meetingDate: toISODate(0),
    startTime: '13:30',
    endTime: '14:15',
    participantCount: 5,
    status: 'Scheduled',
    googleMeetUrl: 'https://meet.google.com/vendor-sync',
  },
  {
    id: 6,
    title: 'Data Migration Review',
    description: 'Status update for the migration timeline and rollback readiness.',
    projectId: 'PRJ-3011',
    projectName: 'Data Modernization',
    meetingDate: toISODate(-5),
    startTime: '10:00',
    endTime: '10:45',
    participantCount: 9,
    status: 'Completed',
    googleMeetUrl: 'https://meet.google.com/data-migration',
  },
  {
    id: 7,
    title: 'Executive Briefing',
    description: 'Weekly stakeholder overview covering milestones and business impact.',
    projectId: 'PRJ-3902',
    projectName: 'Enterprise Strategy',
    meetingDate: toISODate(-1),
    startTime: '15:00',
    endTime: '15:45',
    participantCount: 12,
    status: 'Cancelled',
    googleMeetUrl: 'https://meet.google.com/executive-briefing',
  },
  {
    id: 8,
    title: 'Security Review',
    description: 'Review of controls, user access, and outstanding security actions.',
    projectId: 'PRJ-3209',
    projectName: 'Security Program',
    meetingDate: toISODate(6),
    startTime: '09:00',
    endTime: '10:00',
    participantCount: 10,
    status: 'Scheduled',
    googleMeetUrl: 'https://meet.google.com/security-review',
  },
];

export const getMeetings = async (): Promise<Meeting[]> => {
  const response = await fetch(`${API_BASE_URL}/meetings`, {
    method: 'GET',
    headers: { 'Content-Type': 'application/json' },
  });

  if (!response.ok) {
    let errorMessage = 'Failed to fetch meetings';
    try {
      const payload = await response.json();
      if (payload?.message) {
        errorMessage = payload.message;
      }
    } catch {
      // ignore JSON parse issues and keep the default error
    }
    throw new Error(errorMessage);
  }

  const data = await response.json();
  return Array.isArray(data) ? data.map((meeting) => normalizeMeeting(meeting)) : [];
};

export const getMeetingById = async (meetingId: string | number): Promise<Meeting> => {
  const response = await fetch(`${API_BASE_URL}/meetings/${encodeURIComponent(String(meetingId))}`, {
    method: 'GET',
    headers: { 'Content-Type': 'application/json' },
  });

  if (!response.ok) {
    let errorMessage = 'Failed to fetch meeting details';
    try {
      const payload = await response.json();
      if (payload?.message) {
        errorMessage = payload.message;
      }
    } catch {
      // ignore JSON parse issues and keep the default error
    }
    throw new Error(errorMessage);
  }

  return normalizeMeeting(await response.json());
};

const API_BASE_URL = 'http://localhost:8080/api/v1';

export const createMeeting = async (request: CreateMeetingRequest): Promise<Meeting> => {
  const response = await fetch(`${API_BASE_URL}/meetings`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    let errorMessage = 'Failed to create meeting';
    try {
      const payload = await response.json();
      if (payload?.message) {
        errorMessage = payload.message;
      }
    } catch {
      // ignore JSON parse issues and use the default error
    }

    throw new Error(errorMessage);
  }

  const createdMeeting = await response.json();
  return normalizeMeeting(createdMeeting);
};

export const updateMeeting = async (meetingId: string | number, request: CreateMeetingRequest): Promise<Meeting> => {
  const response = await fetch(`${API_BASE_URL}/meetings/${encodeURIComponent(String(meetingId))}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    let errorMessage = 'Failed to update meeting';
    try {
      const payload = await response.json();
      if (payload?.message) {
        errorMessage = payload.message;
      }
    } catch {
      // ignore JSON parse issues and use the default error
    }

    throw new Error(errorMessage);
  }

  return normalizeMeeting(await response.json());
};

export const cancelMeeting = async (meetingId: string | number): Promise<Meeting> => {
  const response = await fetch(`${API_BASE_URL}/meetings/${encodeURIComponent(String(meetingId))}/cancel`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
  });

  if (!response.ok) {
    let errorMessage = 'Failed to cancel meeting';
    try {
      const payload = await response.json();
      if (payload?.message) {
        errorMessage = payload.message;
      }
    } catch {
      // ignore JSON parse issues and use the default error
    }

    throw new Error(errorMessage);
  }

  return normalizeMeeting(await response.json());
};

export const uploadMeetingTranscript = async (
  meetingId: string | number,
  transcriptText: string,
  fileName?: string
): Promise<Meeting> => {
  const response = await fetch(`${API_BASE_URL}/meetings/${encodeURIComponent(String(meetingId))}/transcript`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ transcriptText, fileName: fileName ?? 'meeting-transcript.txt' }),
  });

  if (!response.ok) {
    let errorMessage = 'Failed to save meeting transcript';
    try {
      const payload = await response.json();
      if (payload?.message) {
        errorMessage = payload.message;
      }
    } catch {
      // ignore JSON parse issues and use the default error
    }

    throw new Error(errorMessage);
  }

  return normalizeMeeting(await response.json());
};

export const regenerateMeetingSummary = async (meetingId: string | number): Promise<Meeting> => {
  const response = await fetch(`${API_BASE_URL}/meetings/${encodeURIComponent(String(meetingId))}/summary/regenerate`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
  });

  if (!response.ok) {
    let errorMessage = 'Failed to regenerate meeting summary';
    try {
      const payload = await response.json();
      if (payload?.message) {
        errorMessage = payload.message;
      }
    } catch {
      // ignore JSON parse issues and use the default error
    }

    throw new Error(errorMessage);
  }

  return normalizeMeeting(await response.json());
};

export const deleteMeeting = async (meetingId: string | number): Promise<void> => {
  const response = await fetch(`${API_BASE_URL}/meetings/${encodeURIComponent(String(meetingId))}`, {
    method: 'DELETE',
    headers: { 'Content-Type': 'application/json' },
  });

  if (!response.ok) {
    let errorMessage = 'Failed to delete meeting';
    try {
      const payload = await response.json();
      if (payload?.message) {
        errorMessage = payload.message;
      }
    } catch {
      // ignore JSON parse issues and use the default error
    }

    throw new Error(errorMessage);
  }
};
