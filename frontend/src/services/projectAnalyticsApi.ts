const API_BASE_URL = 'http://localhost:8080/api/v1';

export interface ProjectTaskMetrics {
  totalTasks: number;
  completedTasks: number;
  inProgressTasks: number;
  pendingTasks: number;
  completionPercentage: number;
  overdueTasks: unknown[];
  overdueTrackingAvailable: boolean;
}

interface ProjectTaskSummaryResponse {
  data: ProjectTaskMetrics;
}

export const getProjectTaskMetrics = async (projectId: string): Promise<ProjectTaskMetrics> => {
  const response = await fetch(`${API_BASE_URL}/projects/${encodeURIComponent(projectId)}/task-summary`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  });

  if (!response.ok) {
    throw new Error('Failed to fetch project task metrics');
  }

  const result = (await response.json()) as ProjectTaskSummaryResponse;
  return result.data;
};