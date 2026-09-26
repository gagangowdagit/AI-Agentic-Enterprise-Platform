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
    <div className="documents-page">
      <div className="documents-header">
        <h1>Project Documents</h1>
        <p>Manage documents for your selected project</p>
      </div>

      <div className="documents-project-selector">
        <label className="documents-project-selector-label">Select Project:</label>
        <div className="documents-project-selector-list">
          {projects.length === 0 ? (
            <p className="documents-empty-projects">No projects available</p>
          ) : (
            projects.map((project) => (
              <button
                key={project.id}
                type="button"
                className={selectedProjectId === String(project.id) ? 'documents-project-button active' : 'documents-project-button'}
                onClick={() => setSelectedProjectId(String(project.id))}
              >
                {project.name}
              </button>
            ))
          )}
        </div>
      </div>

      <button type="button" className="documents-upload-button" onClick={handleUploadDocument}>
        + Upload Document
      </button>

      {showUploadForm && (
        <div className="documents-upload-form">
          <h2>Upload New Document</h2>

          <div className="documents-upload-field">
            <label className="documents-upload-label">Select File</label>
            <input
              ref={fileInputRef}
              type="file"
              className="documents-upload-input"
              onChange={handleFileSelect}
            />
          </div>

          {selectedFile && <div className="documents-selected-file"><strong>Selected File:</strong> {selectedFile.name}</div>}

          <div className="documents-upload-actions">
            <button type="button" className="documents-primary-action" onClick={handleUploadFile} disabled={!selectedFile}>
              Upload
            </button>
            <button type="button" className="documents-secondary-action" onClick={handleCancelUpload}>
              Cancel
            </button>
          </div>
        </div>
      )}

      <div className="documents-list-panel">
        <h2>Documents</h2>
        <div className="documents-table-shell">
          {isLoading ? (
            <p className="documents-loading-text">Loading documents...</p>
          ) : documents.filter((doc) => String(doc.projectId) === selectedProjectId).length === 0 ? (
            <div className="documents-empty-state">
              <p>📁 No documents uploaded yet</p>
              <p>Documents for this project will appear here after upload</p>
            </div>
          ) : (
            <div className="documents-table">
              <div className="documents-table-header">
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
                  <div key={doc.id} className="documents-table-row">
                    <div className="documents-file-name">📄 {doc.fileName}</div>
                    <div className="documents-file-type">
                      <span>{getFileExtension(doc.fileName)}</span>
                    </div>
                    <div className="documents-file-meta">{formatFileSize(doc.fileSize)}</div>
                    <div className="documents-file-meta">{doc.projectId}</div>
                    <div className="documents-file-meta">{doc.uploadedAt}</div>
                    <div className="documents-action-buttons">
                      <button type="button" className="documents-download-button" onClick={() => handleDownloadDocument(doc.id)}>
                        Open/Download
                      </button>
                      <button type="button" className="documents-delete-button" onClick={() => handleDeleteDocument(doc.id)}>
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
