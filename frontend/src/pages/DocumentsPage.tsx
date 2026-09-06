import { useState, useRef, useEffect } from 'react';
import {
  uploadDocument,
  getDocumentsByProject,
  downloadDocument,
  deleteDocument,
} from '../services/documentApi';
import { getProjects, type Project as ProjectItem } from '../services/projectApi';

interface Document {
  id: number;
  fileName: string;
  fileType: string;
  fileSize: number;
  projectId: string;
  uploadedAt: string;
}

function DocumentsPage() {
  const [projects, setProjects] = useState<ProjectItem[]>([]);

  // Helper function to format file size
  const formatFileSize = (bytes: number): string => {
    if (bytes === 0) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i];
  };

  // Helper function to get file extension from name
  const getFileExtension = (fileName: string): string => {
    const parts = fileName.split('.');
    return parts.length > 1 ? parts[parts.length - 1].toUpperCase() : 'FILE';
  };

  const [documents, setDocuments] = useState<Document[]>([]);
  const [selectedProjectId, setSelectedProjectId] = useState<string>('');
  const [showUploadForm, setShowUploadForm] = useState(false);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    const fetchProjects = async () => {
      try {
        const projectList = await getProjects();
        setProjects(projectList);
        if (projectList.length > 0 && !selectedProjectId) {
          setSelectedProjectId(String(projectList[0].id));
        }
      } catch (error) {
        console.error('Failed to fetch projects:', error);
        setProjects([]);
      }
    };

    fetchProjects();
  }, []);

  // Fetch documents from backend when page loads or project changes
  useEffect(() => {
    if (!selectedProjectId) {
      return;
    }

    const fetchDocuments = async () => {
      try {
        setIsLoading(true);
        const docs = await getDocumentsByProject(selectedProjectId);
        setDocuments(docs);
      } catch (error) {
        console.error('Failed to fetch documents:', error);
        setDocuments([]);
      } finally {
        setIsLoading(false);
      }
    };

    fetchDocuments();
  }, [selectedProjectId]);

  const handleUploadDocument = () => {
    setShowUploadForm(true);
  };

  const handleFileSelect = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      setSelectedFile(file);
    }
  };

  const handleUploadFile = async () => {
    if (selectedFile) {
      try {
        // Upload file via API - backend will handle storage and metadata
        await uploadDocument(selectedProjectId, selectedFile);

        // Fetch fresh documents from backend to ensure state is in sync
        const updatedDocs = await getDocumentsByProject(selectedProjectId);
        setDocuments(updatedDocs);
        
        // Reset form
        setSelectedFile(null);
        setShowUploadForm(false);
        if (fileInputRef.current) {
          fileInputRef.current.value = '';
        }
      } catch (error) {
        console.error('Failed to upload document:', error);
        alert('Failed to upload document. Please try again.');
      }
    }
  };

  const handleCancelUpload = () => {
    setSelectedFile(null);
    setShowUploadForm(false);
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

  const handleDeleteDocument = async (docId: number) => {
    try {
      await deleteDocument(docId);
      setDocuments((current) => current.filter((doc) => doc.id !== docId));
    } catch (error) {
      console.error('Failed to delete document:', error);
      alert('Failed to delete document. Please try again.');
    }
  };

  const handleDownloadDocument = async (docId: number) => {
    try {
      await downloadDocument(docId);
    } catch (error) {
      console.error('Failed to download document:', error);
      alert('Failed to download document. Please try again.');
    }
  };

  return (
    <div style={{ padding: '20px' }}>
      <div style={{ marginBottom: '30px' }}>
        <h1 style={{ marginBottom: '10px', color: '#333' }}>Project Documents</h1>
        <p style={{ color: '#666', margin: '0' }}>Manage documents for your selected project</p>
      </div>

      <div style={{ marginBottom: '30px', padding: '20px', backgroundColor: '#f5f5f5', borderRadius: '8px' }}>
        <label
          style={{
            display: 'block',
            marginBottom: '12px',
            fontWeight: '600',
            color: '#333',
            fontSize: '14px',
          }}
        >
          Select Project:
        </label>
        <div style={{ display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
          {projects.length === 0 ? (
            <p style={{ margin: 0, color: '#666' }}>No projects available</p>
          ) : (
            projects.map((project) => (
              <button
                key={project.id}
                onClick={() => setSelectedProjectId(String(project.id))}
                style={{
                  padding: '10px 16px',
                  backgroundColor: selectedProjectId === String(project.id) ? '#2196F3' : '#e0e0e0',
                  color: selectedProjectId === String(project.id) ? 'white' : '#333',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: 'pointer',
                  fontSize: '14px',
                  fontWeight: selectedProjectId === String(project.id) ? '600' : '500',
                  transition: 'background-color 0.3s',
                }}
                onMouseEnter={(e) => {
                  if (selectedProjectId !== String(project.id)) {
                    e.currentTarget.style.backgroundColor = '#d0d0d0';
                  }
                }}
                onMouseLeave={(e) => {
                  if (selectedProjectId !== String(project.id)) {
                    e.currentTarget.style.backgroundColor = '#e0e0e0';
                  }
                }}
              >
                {project.name}
              </button>
            ))
          )}
        </div>
      </div>

      <button
        onClick={handleUploadDocument}
        style={{
          padding: '10px 20px',
          marginBottom: '20px',
          backgroundColor: '#2196F3',
          color: 'white',
          border: 'none',
          borderRadius: '4px',
          cursor: 'pointer',
          fontSize: '16px',
          fontWeight: '500',
          transition: 'background-color 0.3s',
        }}
        onMouseEnter={(e) => (e.currentTarget.style.backgroundColor = '#1976D2')}
        onMouseLeave={(e) => (e.currentTarget.style.backgroundColor = '#2196F3')}
      >
        + Upload Document
      </button>

      {showUploadForm && (
        <div
          style={{
            marginBottom: '30px',
            padding: '30px',
            border: '2px solid #2196F3',
            borderRadius: '8px',
            backgroundColor: '#E3F2FD',
          }}
        >
          <h2 style={{ marginTop: '0', color: '#1976D2', marginBottom: '20px' }}>Upload New Document</h2>
          
          <div style={{ marginBottom: '20px' }}>
            <label
              style={{
                display: 'block',
                marginBottom: '10px',
                fontWeight: '500',
                color: '#333',
              }}
            >
              Select File
            </label>
            <input
              ref={fileInputRef}
              type="file"
              onChange={handleFileSelect}
              style={{
                display: 'block',
                marginBottom: '15px',
                padding: '8px',
                border: '1px solid #999',
                borderRadius: '4px',
                cursor: 'pointer',
              }}
            />
          </div>

          {selectedFile && (
            <div
              style={{
                marginBottom: '20px',
                padding: '15px',
                backgroundColor: '#fff',
                border: '1px solid #4CAF50',
                borderRadius: '4px',
                color: '#333',
              }}
            >
              <strong>Selected File:</strong> {selectedFile.name}
            </div>
          )}

          <div style={{ display: 'flex', gap: '10px' }}>
            <button
              onClick={handleUploadFile}
              disabled={!selectedFile}
              style={{
                padding: '10px 20px',
                backgroundColor: selectedFile ? '#4CAF50' : '#ccc',
                color: 'white',
                border: 'none',
                borderRadius: '4px',
                cursor: selectedFile ? 'pointer' : 'not-allowed',
                fontSize: '14px',
                fontWeight: '500',
                transition: 'background-color 0.3s',
              }}
              onMouseEnter={(e) => {
                if (selectedFile) {
                  e.currentTarget.style.backgroundColor = '#45a049';
                }
              }}
              onMouseLeave={(e) => {
                if (selectedFile) {
                  e.currentTarget.style.backgroundColor = '#4CAF50';
                }
              }}
            >
              Upload
            </button>
            <button
              onClick={handleCancelUpload}
              style={{
                padding: '10px 20px',
                backgroundColor: '#f44336',
                color: 'white',
                border: 'none',
                borderRadius: '4px',
                cursor: 'pointer',
                fontSize: '14px',
                fontWeight: '500',
                transition: 'background-color 0.3s',
              }}
              onMouseEnter={(e) => (e.currentTarget.style.backgroundColor = '#d32f2f')}
              onMouseLeave={(e) => (e.currentTarget.style.backgroundColor = '#f44336')}
            >
              Cancel
            </button>
          </div>
        </div>
      )}

      <div style={{ marginTop: '30px' }}>
        <h2 style={{ marginBottom: '20px', color: '#333' }}>Documents</h2>
        <div
          style={{
            padding: '40px',
            border: '2px dashed #ddd',
            borderRadius: '4px',
            backgroundColor: '#fafafa',
            textAlign: 'center',
            minHeight: '300px',
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'center',
            alignItems: 'center',
          }}
        >
          {isLoading ? (
            <p style={{ color: '#999', fontSize: '16px' }}>Loading documents...</p>
          ) : documents.filter((doc) => String(doc.projectId) === selectedProjectId).length === 0 ? (
            <>
              <p style={{ color: '#999', fontSize: '18px', marginBottom: '10px' }}>
                📁 No documents uploaded yet
              </p>
              <p style={{ color: '#bbb', fontSize: '14px', margin: '0' }}>
                Documents for this project will appear here after upload
              </p>
            </>
          ) : (
            <div style={{ width: '100%', textAlign: 'left' }}>
              <div
                style={{
                  display: 'grid',
                  gridTemplateColumns: '2fr 100px 100px 120px 150px 160px',
                  gap: '20px',
                  marginBottom: '20px',
                  paddingBottom: '15px',
                  borderBottom: '2px solid #ddd',
                  fontWeight: '600',
                  color: '#333',
                  fontSize: '14px',
                }}
              >
                <div>Document Name</div>
                <div>Type</div>
                <div>Size</div>
                <div>Project ID</div>
                <div>Uploaded Date</div>
                <div>Action</div>
              </div>
              {documents
                .filter((doc) => String(doc.projectId) === selectedProjectId)
                .map((doc) => (
                  <div
                    key={doc.id}
                    style={{
                      display: 'grid',
                      gridTemplateColumns: '2fr 100px 100px 120px 150px 160px',
                      gap: '20px',
                      padding: '12px 0',
                      borderBottom: '1px solid #eee',
                      alignItems: 'center',
                      fontSize: '14px',
                    }}
                  >
                    <div style={{ color: '#333' }}>📄 {doc.fileName}</div>
                    <div style={{ color: '#666', fontSize: '12px' }}>
                      <span style={{ backgroundColor: '#f0f0f0', padding: '2px 6px', borderRadius: '3px' }}>
                        {getFileExtension(doc.fileName)}
                      </span>
                    </div>
                    <div style={{ color: '#999', fontSize: '13px' }}>{formatFileSize(doc.fileSize)}</div>
                    <div style={{ color: '#666', fontSize: '13px' }}>{doc.projectId}</div>
                    <div style={{ color: '#999', fontSize: '13px' }}>{doc.uploadedAt}</div>
                    <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
                      <button
                        onClick={() => handleDownloadDocument(doc.id)}
                        style={{
                          padding: '6px 10px',
                          backgroundColor: '#4CAF50',
                          color: 'white',
                          border: 'none',
                          borderRadius: '4px',
                          cursor: 'pointer',
                          fontSize: '12px',
                          fontWeight: '500',
                        }}
                      >
                        Open/Download
                      </button>
                      <button
                        onClick={() => handleDeleteDocument(doc.id)}
                        style={{
                          padding: '6px 10px',
                          backgroundColor: '#ff5252',
                          color: 'white',
                          border: 'none',
                          borderRadius: '4px',
                          cursor: 'pointer',
                          fontSize: '12px',
                          fontWeight: '500',
                          transition: 'background-color 0.3s',
                        }}
                        onMouseEnter={(e) => (e.currentTarget.style.backgroundColor = '#ff1744')}
                        onMouseLeave={(e) => (e.currentTarget.style.backgroundColor = '#ff5252')}
                      >
                        Delete
                      </button>
                    </div>
                  </div>
                ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default DocumentsPage;
