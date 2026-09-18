export type MeetingStatus = 'Scheduled' | 'In Progress' | 'Completed' | 'Cancelled';

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
}

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
  await new Promise((resolve) => setTimeout(resolve, 450));
  return mockMeetings;
};
