import { apiFetch, API_BASE_URL } from './apiClient';

export interface Department {
  id: number;
  name: string;
  description?: string;
}

export interface CreateDepartmentRequest {
  name: string;
  description?: string;
}

export interface Employee {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  role: string;
  department: { id: number; name: string } | null;
}

export interface CreateEmployeeRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  role: string;
  departmentId: number;
}

export const getDepartments = async (): Promise<Department[]> => {
  const response = await apiFetch(`${API_BASE_URL}/departments`, {
    method: 'GET',
    headers: { 'Content-Type': 'application/json' },
  });

  if (!response.ok) {
    throw new Error('Failed to fetch departments');
  }

  return response.json();
};

export const createDepartment = async (department: CreateDepartmentRequest): Promise<Department> => {
  const response = await apiFetch(`${API_BASE_URL}/departments`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(department),
  });

  if (!response.ok) {
    throw new Error('Failed to create department');
  }

  return response.json();
};

export const createEmployee = async (employee: CreateEmployeeRequest): Promise<Employee> => {
  const response = await apiFetch(`${API_BASE_URL}/employees`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(employee),
  });

  if (!response.ok) {
    throw new Error('Failed to create employee');
  }

  return response.json();
};

export const getEmployees = async (): Promise<Employee[]> => {
  const response = await apiFetch(`${API_BASE_URL}/employees`, {
    method: 'GET',
    headers: { 'Content-Type': 'application/json' },
  });

  if (!response.ok) {
    throw new Error('Failed to fetch employees');
  }

  return response.json();
};