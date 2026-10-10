import { apiFetch, API_BASE_URL } from './apiClient';

export interface ProjectInsights {
  metrics: {
    project: {
      status: string;
    };
    totalTasks: number;
    completedTasks: number;
    pendingTasks: number;
    completionPercentage: number;
  };
  risks: string[];
  bottlenecks: string[];
  priorities: string[];
  recommendations: string[];
  aiAnalysis?: {
    answer?: string;
  } | null;
}

interface ProjectInsightsResponse {
  data: ProjectInsights;
}

export const getProjectInsights = async (projectId: string): Promise<ProjectInsights> => {
  const response = await apiFetch(`${API_BASE_URL}/analytics/projects/${encodeURIComponent(projectId)}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
  });

  if (!response.ok) {
    throw new Error('Failed to fetch project insights');
  }

  const result = (await response.json()) as ProjectInsightsResponse;
  return result.data;
};