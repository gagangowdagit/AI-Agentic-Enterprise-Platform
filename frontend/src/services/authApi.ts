import { API_BASE_URL } from './apiClient';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  id: number;
  name: string;
  email: string;
  role?: string;
  message: string;
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  expiresAt: string;
}

export const login = async (
  credentials: LoginRequest
): Promise<AuthResponse> => {
  const response = await fetch(`${API_BASE_URL}/auth/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(credentials),
  });

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    throw new Error(errorData.error?.message || 'Login failed');
  }

  return response.json();
};
