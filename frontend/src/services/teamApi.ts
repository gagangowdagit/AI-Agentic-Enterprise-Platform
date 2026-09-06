const API_BASE_URL = 'http://localhost:8080/api/v1';

export interface TeamDepartment {
  id: number;
  name: string;
}

export interface TeamMember {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  role: string;
  department: TeamDepartment | null;
  taskSummary?: {
    totalTasks: number;
    completedTasks: number;
    pendingTasks: number;
  };
}

export const getProjectTeam = async (projectId: string): Promise<TeamMember[]> => {
  const response = await fetch(`${API_BASE_URL}/projects/${encodeURIComponent(projectId)}/team`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  });

  if (!response.ok) {
    throw new Error('Failed to fetch project team');
  }

  return response.json();
};

export const getAvailableProjectEmployees = async (projectId: string): Promise<TeamMember[]> => {
  const response = await fetch(`${API_BASE_URL}/projects/${encodeURIComponent(projectId)}/team/available`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  });

  if (!response.ok) {
    throw new Error('Failed to fetch available employees');
  }

  return response.json();
};

export const addProjectTeamMember = async (projectId: string, employeeId: number): Promise<TeamMember> => {
  const response = await fetch(`${API_BASE_URL}/projects/${encodeURIComponent(projectId)}/team`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ employeeId }),
  });

  if (!response.ok) {
    throw new Error('Failed to add team member');
  }

  return response.json();
};

export const removeProjectTeamMember = async (projectId: string, employeeId: number): Promise<void> => {
  const response = await fetch(`${API_BASE_URL}/projects/${encodeURIComponent(projectId)}/team/${employeeId}`, {
    method: 'DELETE',
  });

  if (!response.ok) {
    throw new Error('Failed to remove team member');
  }
};