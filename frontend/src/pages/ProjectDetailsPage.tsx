import { useEffect, useState } from 'react';
import type { Dispatch, SetStateAction } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { getProjects } from '../services/projectApi';
import type { Project } from '../services/projectApi';
import { getProjectTaskMetrics } from '../services/projectAnalyticsApi';
import type { ProjectTaskMetrics } from '../services/projectAnalyticsApi';
import { getProjectTimeline } from '../services/projectTimelineApi';
import type { ProjectTimeline } from '../services/projectTimelineApi';
import { deleteDocument, downloadDocument, getDocumentsByProject } from '../services/documentApi';
import type { DocumentResponse } from '../services/documentApi';
import { getProjectInsights } from '../services/projectInsightsApi';
import type { ProjectInsights } from '../services/projectInsightsApi';

const sections = ['Overview', 'Tasks', 'Timeline', 'Team', 'Documents', 'AI Knowledge', 'AI Insights'] as const;

type Section = (typeof sections)[number];

function ProjectDetailsPage() {
  const { projectId } = useParams<{ projectId: string }>();
  const navigate = useNavigate();
  const [project, setProject] = useState<Project | null>(null);
  const [activeSection, setActiveSection] = useState<Section>('Overview');
  const [taskMetrics, setTaskMetrics] = useState<ProjectTaskMetrics | null>(null);
  const [timeline, setTimeline] = useState<ProjectTimeline | null>(null);
  const [documents, setDocuments] = useState<DocumentResponse[]>([]);
  const [documentsLoading, setDocumentsLoading] = useState(false);
  const [documentsError, setDocumentsError] = useState<string | null>(null);
  const [insights, setInsights] = useState<ProjectInsights | null>(null);
  const [insightsLoading, setInsightsLoading] = useState(false);
  const [insightsError, setInsightsError] = useState<string | null>(null);
  const [tasksLoading, setTasksLoading] = useState(false);
  const [tasksError, setTasksError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const loadProject = async () => {
      try {
        setLoading(true);
        setError(null);
        const projects = await getProjects();
        const matchingProject = projects.find((item) => String(item.id) === projectId);

        if (!matchingProject) {
          throw new Error('Project not found');
        }

        setProject(matchingProject);
      } catch (loadError) {
        setError(loadError instanceof Error ? loadError.message : 'Failed to load project');
      } finally {
        setLoading(false);
      }
    };

    loadProject();
  }, [projectId]);

  useEffect(() => {
    if ((activeSection !== 'Tasks' && activeSection !== 'Timeline') || !projectId) {
      return;
    }

    const loadTaskMetrics = async () => {
      try {
        setTasksLoading(true);
        setTasksError(null);
        setTaskMetrics(await getProjectTaskMetrics(projectId));
      } catch (loadError) {
        setTasksError(loadError instanceof Error ? loadError.message : 'Failed to load task metrics');
      } finally {
        setTasksLoading(false);
      }
    };

    loadTaskMetrics();
  }, [activeSection, projectId]);

  useEffect(() => {
    if (activeSection !== 'AI Insights' || !projectId) {
      return;
    }

    const loadInsights = async () => {
      try {
        setInsightsLoading(true);
        setInsightsError(null);
        setInsights(await getProjectInsights(projectId));
      } catch (loadError) {
        setInsightsError(loadError instanceof Error ? loadError.message : 'Failed to load project insights');
      } finally {
        setInsightsLoading(false);
      }
    };

    loadInsights();
  }, [activeSection, projectId]);

  useEffect(() => {
    if (activeSection !== 'Documents' && activeSection !== 'AI Knowledge' || !projectId) {
      return;
    }

    const loadDocuments = async () => {
      try {
        setDocumentsLoading(true);
        setDocumentsError(null);
        setDocuments(await getDocumentsByProject(projectId));
      } catch (loadError) {
        setDocumentsError(loadError instanceof Error ? loadError.message : 'Failed to load documents');
      } finally {
        setDocumentsLoading(false);
      }
    };

    loadDocuments();
  }, [activeSection, projectId]);

  useEffect(() => {
    if (activeSection !== 'Timeline' || !projectId) {
      return;
    }

    getProjectTimeline(projectId)
      .then(setTimeline)
      .catch(() => setTimeline(null));
  }, [activeSection, projectId]);

  const formatValue = (value?: string) => value || 'Not set';

  return (
    <main style={{ display: 'flex', gap: '32px', width: '100%', minHeight: '100vh', padding: '32px', backgroundColor: '#fff' }}>
      <aside style={{ width: '240px', flexShrink: 0, borderRight: '1px solid #e0e0e0', paddingRight: '24px' }}>
        <button
          type="button"
          onClick={() => navigate('/projects')}
          style={{ border: 0, background: 'none', color: '#1976D2', cursor: 'pointer', padding: '0 0 20px', fontSize: '14px' }}
        >
          ← Back to Projects
        </button>
        <nav aria-label="Project sections">
          {sections.map((section) => (
            <button
              key={section}
              type="button"
              onClick={() => setActiveSection(section)}
              style={{
                display: 'block',
                width: '100%',
                padding: '12px 14px',
                marginBottom: '4px',
                border: 0,
                borderRadius: '4px',
                backgroundColor: activeSection === section ? '#e3f2fd' : 'transparent',
                color: activeSection === section ? '#1565C0' : '#555',
                fontWeight: activeSection === section ? '600' : '400',
                textAlign: 'left',
                cursor: 'pointer',
              }}
            >
              {section}
            </button>
          ))}
        </nav>
      </aside>

      <section style={{ flex: 1, minWidth: 0 }}>
        {loading && <p>Loading project...</p>}
        {error && (
          <div style={{ padding: '20px', backgroundColor: '#ffebee', border: '1px solid #f44336', borderRadius: '4px' }}>
            <p style={{ color: '#c62828', margin: 0 }}>Error: {error}</p>
          </div>
        )}

        {!loading && !error && project && (
          <>
            <header style={{ marginBottom: '24px' }}>
              <p style={{ margin: '0 0 8px', color: '#666', fontSize: '14px' }}>Project {project.id}</p>
              <h1 style={{ margin: 0, color: '#333' }}>{project.name}</h1>
            </header>

            {activeSection === 'Overview' ? (
              <div style={{ border: '1px solid #ddd', borderRadius: '8px', padding: '24px', backgroundColor: '#fff' }}>
                <h2 style={{ marginTop: 0, color: '#333' }}>Overview</h2>
                <p style={{ color: '#666', whiteSpace: 'pre-wrap', lineHeight: 1.6 }}>
                  {formatValue(project.description)}
                </p>
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '20px', marginTop: '24px' }}>
                  <Detail label="Status" value={formatValue(project.status)} />
                  <Detail label="Priority" value={formatValue(project.priority)} />
                  <Detail label="Start date" value={formatValue(project.startDate)} />
                  <Detail label="End date" value={formatValue(project.endDate)} />
                  <Detail label="Project ID" value={String(project.id)} />
                </div>
              </div>
            ) : activeSection === 'Tasks' ? (
              <TasksSection metrics={taskMetrics} loading={tasksLoading} error={tasksError} />
            ) : activeSection === 'Timeline' ? (
              <TimelineSection project={project} timeline={timeline} metrics={taskMetrics} loading={tasksLoading} error={tasksError} />
            ) : activeSection === 'Documents' ? (
              <DocumentsSection
                projectId={projectId || String(project.id)}
                documents={documents}
                loading={documentsLoading}
                error={documentsError}
                onDocumentsChange={setDocuments}
              />
            ) : activeSection === 'AI Knowledge' ? (
              <KnowledgeSection
                projectId={projectId || String(project.id)}
                documents={documents}
                loading={documentsLoading}
                error={documentsError}
                onAskNova={() => navigate(`/nova-ai?projectId=${encodeURIComponent(projectId || String(project.id))}`)}
              />
            ) : activeSection === 'AI Insights' ? (
              <InsightsSection
                projectId={projectId || String(project.id)}
                insights={insights}
                loading={insightsLoading}
                error={insightsError}
                onRefresh={async () => {
                  try {
                    setInsightsLoading(true);
                    setInsightsError(null);
                    setInsights(await getProjectInsights(projectId || String(project.id)));
                  } catch (refreshError) {
                    setInsightsError(refreshError instanceof Error ? refreshError.message : 'Failed to refresh project insights');
                  } finally {
                    setInsightsLoading(false);
                  }
                }}
              />
            ) : (
              <div style={{ padding: '40px 24px', border: '1px solid #ddd', borderRadius: '8px', backgroundColor: '#f9f9f9', textAlign: 'center' }}>
                <h2 style={{ marginTop: 0, color: '#333' }}>{activeSection}</h2>
                <p style={{ marginBottom: 0, color: '#666' }}>Coming soon</p>
              </div>
            )}
          </>
        )}
      </section>
    </main>
  );
}

function InsightsSection({
  projectId,
  insights,
  loading,
  error,
  onRefresh,
}: {
  projectId: string;
  insights: ProjectInsights | null;
  loading: boolean;
  error: string | null;
  onRefresh: () => Promise<void>;
}) {
  if (loading && !insights) {
    return <p>Loading AI insights...</p>;
  }

  if (error && !insights) {
    return (
      <div>
        <InsightError message={error} />
        <RefreshButton onRefresh={onRefresh} loading={loading} />
      </div>
    );
  }

  if (!insights) {
    return <p style={{ color: '#666' }}>No AI insights available.</p>;
  }

  const health = getProjectHealth(insights);
  const sections = [
    { title: 'Risks', items: insights.risks },
    { title: 'Bottlenecks and issues', items: insights.bottlenecks },
    { title: 'Priorities', items: insights.priorities },
    { title: 'Recommendations', items: insights.recommendations },
  ];

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', gap: '16px', alignItems: 'baseline', marginBottom: '24px', flexWrap: 'wrap' }}>
        <div>
          <h2 style={{ margin: '0 0 8px', color: '#333' }}>AI Insights</h2>
          <p style={{ margin: 0, color: '#666' }}>Read-only analysis for project {projectId}</p>
        </div>
        <RefreshButton onRefresh={onRefresh} loading={loading} />
      </div>

      {error && <InsightError message={error} />}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '16px', marginBottom: '24px' }}>
        <InsightMetric label="Project health" value={health} />
        <InsightMetric label="Project status" value={insights.metrics.project.status || 'Not set'} />
        <InsightMetric label="Completion" value={`${insights.metrics.completionPercentage.toFixed(0)}%`} />
      </div>

      {insights.aiAnalysis?.answer && (
        <div style={{ marginBottom: '16px', padding: '20px', border: '1px solid #ddd', borderRadius: '8px', backgroundColor: '#fff' }}>
          <h3 style={{ margin: '0 0 10px', color: '#333' }}>AI summary</h3>
          <p style={{ margin: 0, color: '#555', lineHeight: 1.6, whiteSpace: 'pre-wrap' }}>{insights.aiAnalysis.answer}</p>
        </div>
      )}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '16px' }}>
        {sections.map((section) => (
          <InsightList key={section.title} title={section.title} items={section.items} />
        ))}
      </div>
    </div>
  );
}

function InsightMetric({ label, value }: { label: string; value: string }) {
  return (
    <div style={{ minHeight: '96px', padding: '18px', border: '1px solid #ddd', borderRadius: '8px', backgroundColor: '#f9f9f9' }}>
      <p style={{ margin: '0 0 10px', color: '#777', fontSize: '13px', fontWeight: '600' }}>{label}</p>
      <p style={{ margin: 0, color: '#333', fontSize: '20px', fontWeight: '700' }}>{value}</p>
    </div>
  );
}

function InsightList({ title, items }: { title: string; items: string[] }) {
  return (
    <div style={{ minHeight: '140px', padding: '20px', border: '1px solid #ddd', borderRadius: '8px', backgroundColor: '#fff' }}>
      <h3 style={{ margin: '0 0 12px', color: '#333', fontSize: '17px' }}>{title}</h3>
      {items.length === 0 ? (
        <p style={{ margin: 0, color: '#777' }}>None identified</p>
      ) : (
        <ul style={{ margin: 0, paddingLeft: '20px', color: '#555', lineHeight: 1.6 }}>
          {items.map((item, index) => <li key={`${title}-${index}`}>{item}</li>)}
        </ul>
      )}
    </div>
  );
}

function RefreshButton({ onRefresh, loading }: { onRefresh: () => Promise<void>; loading: boolean }) {
  return (
    <button type="button" onClick={onRefresh} disabled={loading} style={{ padding: '10px 16px', border: 0, borderRadius: '4px', backgroundColor: loading ? '#9e9e9e' : '#1976D2', color: 'white', cursor: loading ? 'not-allowed' : 'pointer', fontWeight: '600' }}>
      {loading ? 'Refreshing...' : 'Refresh Insights'}
    </button>
  );
}

function InsightError({ message }: { message: string }) {
  return <p role="alert" style={{ margin: '0 0 16px', padding: '12px', border: '1px solid #f44336', borderRadius: '4px', backgroundColor: '#ffebee', color: '#c62828' }}>{message}</p>;
}

function getProjectHealth(insights: ProjectInsights) {
  if (insights.metrics.project.status && !insights.metrics.project.status.toLowerCase().includes('active')) {
    return 'Needs attention';
  }
  return insights.risks.length > 0 ? 'Needs attention' : 'Healthy';
}

function KnowledgeSection({
  projectId,
  documents,
  loading,
  error,
  onAskNova,
}: {
  projectId: string;
  documents: DocumentResponse[];
  loading: boolean;
  error: string | null;
  onAskNova: () => void;
}) {
  const projectDocuments = documents.filter((document) => String(document.projectId) === projectId);
  const processedDocuments = projectDocuments.filter((document) => document.extractedText?.trim()).length;
  const knowledgeStatus = projectDocuments.length === 0
    ? 'Empty'
    : processedDocuments === 0
      ? 'Not processed'
      : processedDocuments < projectDocuments.length
        ? 'Partially processed'
        : 'Ready';

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', gap: '16px', alignItems: 'baseline', marginBottom: '24px', flexWrap: 'wrap' }}>
        <div>
          <h2 style={{ margin: '0 0 8px', color: '#333' }}>AI Knowledge</h2>
          <p style={{ margin: 0, color: '#666' }}>Read-only knowledge-base summary for this project</p>
        </div>
        <button type="button" onClick={onAskNova} style={{ padding: '10px 16px', border: 0, borderRadius: '4px', backgroundColor: '#172033', color: 'white', cursor: 'pointer', fontWeight: '600' }}>
          Ask Nova
        </button>
      </div>

      {loading && <p>Loading knowledge-base information...</p>}
      {error && <p style={{ color: '#c62828' }}>Error: {error}</p>}

      {!loading && !error && (
        <>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '16px', marginBottom: '24px' }}>
            <KnowledgeMetric label="Total documents" value={String(projectDocuments.length)} />
            <KnowledgeMetric label="Processed documents" value={String(processedDocuments)} />
            <KnowledgeMetric label="Document chunks" value="Not available" />
            <KnowledgeMetric label="Knowledge-base status" value={knowledgeStatus} />
          </div>

          <div style={{ padding: '18px 20px', border: '1px solid #ddd', borderRadius: '8px', backgroundColor: '#f9f9f9', color: '#666' }}>
            <p style={{ margin: 0 }}>
              Project ID: <strong style={{ color: '#333' }}>{projectId}</strong>
            </p>
            <p style={{ margin: '8px 0 0' }}>Chunk counts are not exposed by the current document API.</p>
          </div>
        </>
      )}
    </div>
  );
}

function KnowledgeMetric({ label, value }: { label: string; value: string }) {
  return (
    <div style={{ minHeight: '96px', padding: '18px', border: '1px solid #ddd', borderRadius: '8px', backgroundColor: '#f9f9f9' }}>
      <p style={{ margin: '0 0 10px', color: '#777', fontSize: '13px', fontWeight: '600' }}>{label}</p>
      <p style={{ margin: 0, color: '#333', fontSize: '20px', fontWeight: '700' }}>{value}</p>
    </div>
  );
}

function DocumentsSection({
  projectId,
  documents,
  loading,
  error,
  onDocumentsChange,
}: {
  projectId: string;
  documents: DocumentResponse[];
  loading: boolean;
  error: string | null;
  onDocumentsChange: Dispatch<SetStateAction<DocumentResponse[]>>;
}) {
  const projectDocuments = documents.filter((document) => String(document.projectId) === projectId);

  const handleDownload = async (documentId: number) => {
    try {
      await downloadDocument(documentId);
    } catch (downloadError) {
      console.error('Failed to download document:', downloadError);
    }
  };

  const handleDelete = async (documentId: number) => {
    try {
      await deleteDocument(documentId);
      onDocumentsChange((currentDocuments) => currentDocuments.filter((document) => document.id !== documentId));
    } catch (deleteError) {
      console.error('Failed to delete document:', deleteError);
    }
  };

  if (loading) {
    return <p>Loading documents...</p>;
  }

  if (error) {
    return (
      <div style={{ padding: '20px', backgroundColor: '#ffebee', border: '1px solid #f44336', borderRadius: '4px' }}>
        <p style={{ color: '#c62828', margin: 0 }}>Error: {error}</p>
      </div>
    );
  }

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', gap: '16px', alignItems: 'baseline', marginBottom: '24px', flexWrap: 'wrap' }}>
        <div>
          <h2 style={{ margin: '0 0 8px', color: '#333' }}>Documents</h2>
          <p style={{ margin: 0, color: '#666' }}>Documents for this project</p>
        </div>
        <strong style={{ color: '#333' }}>{projectDocuments.length} documents</strong>
      </div>

      {projectDocuments.length === 0 ? (
        <div style={{ padding: '40px 24px', border: '2px dashed #ddd', borderRadius: '8px', backgroundColor: '#fafafa', textAlign: 'center' }}>
          <p style={{ margin: 0, color: '#777' }}>No documents uploaded for this project.</p>
        </div>
      ) : (
        <div style={{ overflowX: 'auto', border: '1px solid #ddd', borderRadius: '8px' }}>
          <div style={{ minWidth: '720px' }}>
            <div style={{ display: 'grid', gridTemplateColumns: 'minmax(220px, 2fr) 100px 110px 150px 190px', gap: '16px', padding: '14px 18px', borderBottom: '2px solid #ddd', fontWeight: '600', color: '#333', fontSize: '13px' }}>
              <div>File name</div>
              <div>Type</div>
              <div>Size</div>
              <div>Uploaded</div>
              <div>Actions</div>
            </div>
            {projectDocuments.map((document) => (
              <div key={document.id} style={{ display: 'grid', gridTemplateColumns: 'minmax(220px, 2fr) 100px 110px 150px 190px', gap: '16px', padding: '16px 18px', borderBottom: '1px solid #eee', alignItems: 'center', color: '#555', fontSize: '14px' }}>
                <div style={{ color: '#333', overflowWrap: 'anywhere' }}>{document.fileName}</div>
                <div>{document.fileType || getFileExtension(document.fileName)}</div>
                <div>{formatFileSize(document.fileSize)}</div>
                <div>{document.uploadedAt}</div>
                <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
                  <button type="button" onClick={() => handleDownload(document.id)} style={documentActionStyle('#4CAF50')}>Open/Download</button>
                  <button type="button" onClick={() => handleDelete(document.id)} style={documentActionStyle('#e53935')}>Delete</button>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}

function documentActionStyle(backgroundColor: string) {
  return { padding: '7px 10px', backgroundColor, color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '12px', fontWeight: '600' as const };
}

function getFileExtension(fileName: string) {
  const parts = fileName.split('.');
  return parts.length > 1 ? parts[parts.length - 1].toUpperCase() : 'FILE';
}

function formatFileSize(bytes: number) {
  if (bytes === 0) return '0 B';
  const units = ['B', 'KB', 'MB', 'GB'];
  const unitIndex = Math.min(Math.floor(Math.log(bytes) / Math.log(1024)), units.length - 1);
  return `${Math.round((bytes / Math.pow(1024, unitIndex)) * 100) / 100} ${units[unitIndex]}`;
}

function TimelineSection({
  project,
  timeline,
  metrics,
  loading,
  error,
}: {
  project: Project;
  timeline: ProjectTimeline | null;
  metrics: ProjectTaskMetrics | null;
  loading: boolean;
  error: string | null;
}) {
  const daysRemaining = getDaysRemaining(project.endDate);
  const progress = metrics ? Math.min(100, Math.max(0, metrics.completionPercentage)) : null;

  return (
    <div>
      <div style={{ marginBottom: '24px' }}>
        <h2 style={{ margin: '0 0 8px', color: '#333' }}>Timeline</h2>
        <p style={{ margin: 0, color: '#666' }}>Project schedule and current progress</p>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '16px', marginBottom: '24px' }}>
        <TimelineValue label="Start date" value={formatDate(project.startDate)} />
        <TimelineValue label="End date" value={formatDate(project.endDate)} />
        <TimelineValue label="Days remaining" value={daysRemaining} />
      </div>

      <div style={{ border: '1px solid #ddd', borderRadius: '8px', padding: '24px', backgroundColor: '#fff', marginBottom: '24px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', gap: '16px', marginBottom: '12px', color: '#333' }}>
          <strong>Current progress</strong>
          <strong>{loading ? 'Loading...' : progress === null ? 'Not available' : `${progress.toFixed(0)}%`}</strong>
        </div>
        <div role="progressbar" aria-label="Project progress" aria-valuemin={0} aria-valuemax={100} aria-valuenow={progress ?? 0} style={{ height: '12px', overflow: 'hidden', borderRadius: '6px', backgroundColor: '#e0e0e0' }}>
          <div style={{ width: `${progress ?? 0}%`, height: '100%', borderRadius: '6px', backgroundColor: '#2196F3', transition: 'width 0.3s ease' }} />
        </div>
        {error && <p style={{ margin: '12px 0 0', color: '#c62828' }}>{error}</p>}
      </div>

      <div style={{ border: '1px solid #ddd', borderRadius: '8px', padding: '24px', backgroundColor: '#f9f9f9' }}>
        <h3 style={{ margin: '0 0 8px', color: '#333' }}>Task timeline</h3>
        {!timeline || timeline.tasks.length === 0 ? (
          <p style={{ margin: 0, color: '#666' }}>No project tasks are available.</p>
        ) : (
          <div style={{ display: 'grid', gap: '12px' }}>
            {timeline.tasks.map((task) => (
              <div key={task.id} style={{ padding: '14px', border: '1px solid #ddd', borderRadius: '6px', backgroundColor: '#fff' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', gap: '16px', flexWrap: 'wrap' }}>
                  <strong style={{ color: '#333' }}>{task.title || task.id}</strong>
                  <span style={{ color: '#666', textTransform: 'capitalize' }}>{task.status || 'Not set'}</span>
                </div>
                <p style={{ margin: '8px 0 0', color: '#777', fontSize: '13px' }}>
                  {task.startDate || task.endDate
                    ? `${formatDate(task.startDate)} - ${formatDate(task.endDate)}`
                    : 'Task dates are not available'}
                </p>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

function TimelineValue({ label, value }: { label: string; value: string }) {
  return (
    <div style={{ minHeight: '96px', padding: '18px', border: '1px solid #ddd', borderRadius: '8px', backgroundColor: '#f9f9f9' }}>
      <p style={{ margin: '0 0 10px', color: '#777', fontSize: '13px', fontWeight: '600' }}>{label}</p>
      <p style={{ margin: 0, color: '#333', fontSize: '20px', fontWeight: '700' }}>{value}</p>
    </div>
  );
}

function formatDate(value?: string) {
  if (!value) {
    return 'Not set';
  }

  return new Intl.DateTimeFormat(undefined, {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  }).format(new Date(`${value}T12:00:00`));
}

function getDaysRemaining(endDate?: string) {
  if (!endDate) {
    return 'Not set';
  }

  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const end = new Date(`${endDate}T00:00:00`);
  const days = Math.ceil((end.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));

  return days < 0 ? `${Math.abs(days)} days overdue` : `${days} days`;
}

function TasksSection({
  metrics,
  loading,
  error,
}: {
  metrics: ProjectTaskMetrics | null;
  loading: boolean;
  error: string | null;
}) {
  if (loading) {
    return <p>Loading task metrics...</p>;
  }

  if (error) {
    return (
      <div style={{ padding: '20px', backgroundColor: '#ffebee', border: '1px solid #f44336', borderRadius: '4px' }}>
        <p style={{ color: '#c62828', margin: 0 }}>Error: {error}</p>
      </div>
    );
  }

  if (!metrics) {
    return <p style={{ color: '#666' }}>No task metrics available.</p>;
  }

  const completionPercentage = Math.min(100, Math.max(0, metrics.completionPercentage));
  const overdueValue = metrics.overdueTrackingAvailable ? String(metrics.overdueTasks.length) : 'Not tracked';
  const inProgressValue = metrics.inProgressTasks === undefined ? 'Not tracked' : String(metrics.inProgressTasks);

  return (
    <div>
      <div style={{ marginBottom: '24px' }}>
        <h2 style={{ margin: '0 0 8px', color: '#333' }}>Task Summary</h2>
        <p style={{ margin: 0, color: '#666' }}>Progress for this project</p>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(160px, 1fr))', gap: '16px', marginBottom: '24px' }}>
        <MetricCard label="Total tasks" value={String(metrics.totalTasks)} />
        <MetricCard label="Completed" value={String(metrics.completedTasks)} />
        <MetricCard label="In progress" value={inProgressValue} />
        <MetricCard label="Pending" value={String(metrics.pendingTasks)} />
        <MetricCard label="Overdue" value={overdueValue} />
      </div>

      <div style={{ border: '1px solid #ddd', borderRadius: '8px', padding: '24px', backgroundColor: '#fff' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', gap: '16px', marginBottom: '12px', color: '#333' }}>
          <strong>Completion</strong>
          <strong>{completionPercentage.toFixed(0)}%</strong>
        </div>
        <div role="progressbar" aria-label="Task completion" aria-valuemin={0} aria-valuemax={100} aria-valuenow={completionPercentage} style={{ height: '12px', overflow: 'hidden', borderRadius: '6px', backgroundColor: '#e0e0e0' }}>
          <div style={{ width: `${completionPercentage}%`, height: '100%', borderRadius: '6px', backgroundColor: '#2196F3', transition: 'width 0.3s ease' }} />
        </div>
      </div>
    </div>
  );
}

function MetricCard({ label, value }: { label: string; value: string }) {
  return (
    <div style={{ minHeight: '96px', padding: '18px', border: '1px solid #ddd', borderRadius: '8px', backgroundColor: '#f9f9f9' }}>
      <p style={{ margin: '0 0 10px', color: '#777', fontSize: '13px', fontWeight: '600' }}>{label}</p>
      <p style={{ margin: 0, color: '#333', fontSize: '24px', fontWeight: '700' }}>{value}</p>
    </div>
  );
}

function Detail({ label, value }: { label: string; value: string }) {
  return (
    <div>
      <p style={{ margin: '0 0 6px', color: '#777', fontSize: '13px', fontWeight: '600' }}>{label}</p>
      <p style={{ margin: 0, color: '#333' }}>{value}</p>
    </div>
  );
}

export default ProjectDetailsPage;