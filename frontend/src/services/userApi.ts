import { apiFetch, API_BASE_URL } from './apiClient';

export interface CreateUserRequest {
  name: string;
  email: string;
  password: string;
  role?: string;
}

export const createUser = async (
  user: CreateUserRequest
) => {
  const response = await apiFetch(`${API_BASE_URL}/users`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(user),
  });

  if (!response.ok) {
    throw new Error('Failed to create user');
  }

  return response.json();
};