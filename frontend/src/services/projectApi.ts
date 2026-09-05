const API_BASE_URL = 'http://localhost:8080/api/v1';

export interface Project {
  id: number;
  name: string;
  description?: string;
  status: string;
  startDate?: string;
  endDate?: string;
  priority: ProjectPriority;
}

export type ProjectPriority = 'LOW' | 'MEDIUM' | 'HIGH';

export type CreateProjectRequest = Omit<Project, 'id'>;

export const getProjects = async (): Promise<Project[]> => {
  const response = await fetch(`${API_BASE_URL}/projects`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  });

  if (!response.ok) {
    throw new Error('Failed to fetch projects');
  }

  return response.json();
};

export const createProject = async (project: CreateProjectRequest): Promise<Project> => {
  const response = await fetch(`${API_BASE_URL}/projects`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(project),
  });

  if (!response.ok) {
    throw new Error('Failed to create project');
  }

  return response.json();
};
