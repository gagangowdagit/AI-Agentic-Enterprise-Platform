const API_BASE_URL = 'http://localhost:8080/api/v1';

export interface CompanyAnalytics {
  totalProjects: number;
  totalDepartments: number;
  totalEmployees: number;
  completedProjects: number;
  inProgressProjects: number;
  pendingProjects: number;
}

export interface DepartmentAnalytics {
  id: number;
  name: string;
  description?: string | null;
  projectCount: number;
  employeeCount: number;
}

export interface EmployeeAnalytics {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  role: string;
  departmentName: string;
}

export const getCompanyAnalytics = async (): Promise<CompanyAnalytics> => {
  const response = await fetch(`${API_BASE_URL}/analytics/overview`);
  if (!response.ok) {
    throw new Error('Failed to fetch company analytics');
  }
  return response.json();
};

export const getDepartmentBreakdown = async (): Promise<DepartmentAnalytics[]> => {
  const response = await fetch(`${API_BASE_URL}/analytics/departments`);
  if (!response.ok) {
    throw new Error('Failed to fetch department analytics');
  }
  return response.json();
};

export const getEmployeeBreakdown = async (): Promise<EmployeeAnalytics[]> => {
  const response = await fetch(`${API_BASE_URL}/analytics/employees`);
  if (!response.ok) {
    throw new Error('Failed to fetch employee analytics');
  }
  return response.json();
};