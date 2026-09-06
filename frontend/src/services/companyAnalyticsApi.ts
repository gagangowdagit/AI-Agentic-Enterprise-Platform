const API_BASE_URL = 'http://localhost:8080/api/v1';

export interface CompanyAnalytics {
  totalProjects: number;
  totalDepartments: number;
  totalEmployees: number;
  completedProjects: number;
  inProgressProjects: number;
  pendingProjects: number;
}

export const getCompanyAnalytics = async (): Promise<CompanyAnalytics> => {
  const response = await fetch(`${API_BASE_URL}/analytics/overview`);
  if (!response.ok) {
    throw new Error('Failed to fetch company analytics');
  }
  return response.json();
};