const API_BASE_URL = 'http://localhost:8080/api/v1';

export interface DocumentResponse {
  id: number;
  projectId: string;
  fileName: string;
  fileType: string;
  fileSize: number;
  filePath: string;
  uploadedAt: string;
  extractedText?: string;
}

export const uploadDocument = async (
  projectId: string,
  file: File
): Promise<DocumentResponse> => {
  const formData = new FormData();
  formData.append('projectId', projectId);
  formData.append('file', file);

  const response = await fetch(`${API_BASE_URL}/documents`, {
    method: 'POST',
    body: formData,
  });

  if (!response.ok) {
    throw new Error('Failed to upload document');
  }

  return response.json();
};

export const getDocumentsByProject = async (
  projectId: string
): Promise<DocumentResponse[]> => {
  const response = await fetch(
    `${API_BASE_URL}/documents?projectId=${projectId}`,
    {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    }
  );

  if (!response.ok) {
    throw new Error('Failed to fetch documents');
  }

  return response.json();
};

export const downloadDocument = async (documentId: number): Promise<void> => {
  const response = await fetch(`${API_BASE_URL}/documents/${documentId}/download`);

  if (!response.ok) {
    throw new Error('Failed to download document');
  }

  const blob = await response.blob();
  const contentDisposition = response.headers.get('Content-Disposition') || '';
  const match = /filename="?([^";]+)"?/.exec(contentDisposition);
  const fileName = match?.[1] || `document-${documentId}`;
  const url = window.URL.createObjectURL(blob);
  const link = document.createElement('a');

  link.href = url;
  link.download = fileName;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  window.URL.revokeObjectURL(url);
};

export const deleteDocument = async (documentId: number): Promise<void> => {
  const response = await fetch(`${API_BASE_URL}/documents/${documentId}`, {
    method: 'DELETE',
  });

  if (!response.ok) {
    throw new Error('Failed to delete document');
  }
};
