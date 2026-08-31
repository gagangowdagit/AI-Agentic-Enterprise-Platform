const API_BASE_URL = 'http://localhost:8080/api/v1';

export interface CreateUserRequest {
  name: string;
  email: string;
  password: string;
}

export const createUser = async (
  user: CreateUserRequest
) => {
  const response = await fetch(`${API_BASE_URL}/users`, {
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