export const API_BASE_URL = 'http://localhost:8080/api/v1';
export const AUTH_EXPIRED_EVENT = 'rag-auth-expired';
export const AUTH_CHANGED_EVENT = 'rag-auth-changed';

const ACCESS_TOKEN_KEY = 'rag-access-token';
const apiBaseUrl = new URL(API_BASE_URL);

// Temporary until the backend supports HttpOnly cookie sessions; sessionStorage limits persistence but is still script-accessible.
export const getAccessToken = () => sessionStorage.getItem(ACCESS_TOKEN_KEY);

export const saveAccessToken = (token: string) => {
  sessionStorage.setItem(ACCESS_TOKEN_KEY, token);
};

export const clearAccessToken = () => {
  sessionStorage.removeItem(ACCESS_TOKEN_KEY);
};

const isPlatformApiRequest = (url: URL) => url.origin === apiBaseUrl.origin
  && (url.pathname === apiBaseUrl.pathname || url.pathname.startsWith(`${apiBaseUrl.pathname}/`));

const isPublicApiRequest = (url: URL, method: string) => {
  const normalizedMethod = method.toUpperCase();
  if (normalizedMethod === 'OPTIONS') {
    return true;
  }

  return (url.pathname === `${apiBaseUrl.pathname}/auth/login` && normalizedMethod === 'POST')
    || ((url.pathname === '/api/health' || url.pathname === '/api/v1/health') && normalizedMethod === 'GET')
    || (url.pathname === `${apiBaseUrl.pathname}/departments` && normalizedMethod === 'GET')
    || (url.pathname === `${apiBaseUrl.pathname}/employees` && normalizedMethod === 'POST');
};

export const apiFetch = async (input: RequestInfo | URL, init: RequestInit = {}): Promise<Response> => {
  const requestUrl = new URL(input instanceof Request ? input.url : input.toString(), window.location.href);
  const method = init.method ?? (input instanceof Request ? input.method : 'GET');
  const isPlatformRequest = isPlatformApiRequest(requestUrl);
  const headers = new Headers(input instanceof Request ? input.headers : undefined);
  new Headers(init.headers).forEach((value, name) => headers.set(name, value));

  if (isPlatformRequest && !isPublicApiRequest(requestUrl, method)) {
    const token = getAccessToken();
    if (token) {
      headers.set('Authorization', `Bearer ${token}`);
    }
  }

  const response = await fetch(input, { ...init, headers });
  if (isPlatformRequest && response.status === 401 && !isPublicApiRequest(requestUrl, method)) {
    clearAccessToken();
    window.dispatchEvent(new Event(AUTH_EXPIRED_EVENT));
  }

  return response;
};