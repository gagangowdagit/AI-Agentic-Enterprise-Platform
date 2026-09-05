const API_BASE_URL = 'http://localhost:8080/api/v1';

export interface ProjectTimelineTask {
  id: string;
  title?: string;
  status?: string;
  startDate?: string;
  endDate?: string;
}

export interface ProjectTimeline {
  projectId: string;
  startDate?: string;
  endDate?: string;
  status: string;
  daysRemaining?: number;
  tasks: ProjectTimelineTask[];
}

interface ProjectTimelineResponse {
  data: ProjectTimeline;
}

export const getProjectTimeline = async (projectId: string): Promise<ProjectTimeline> => {
  const response = await fetch(`${API_BASE_URL}/projects/${encodeURIComponent(projectId)}/timeline`);

  if (!response.ok) {
    throw new Error('Failed to fetch project timeline');
  }

  const result = (await response.json()) as ProjectTimelineResponse;
  return result.data;
};