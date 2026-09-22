import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getCompanyAnalytics, type CompanyAnalytics } from '../services/companyAnalyticsApi';
import { getProjects, type Project } from '../services/projectApi';

const metricCards = [
  { key: 'totalProjects', label: 'Total projects' },
  { key: 'totalDepartments', label: 'Total departments' },
  { key: 'totalEmployees', label: 'Total employees' },
  { key: 'completedProjects', label: 'Completed projects' },
  { key: 'inProgressProjects', label: 'In-progress projects' },
  { key: 'pendingProjects', label: 'Pending projects' },
] as const;

type StatusKey = 'completed' | 'in-progress' | 'pending';

const formatDate = (value?: string) => {
  if (!value) {
    return 'Not set';
  }

  const parsedDate = new Date(`${value}T00:00:00`);
  if (Number.isNaN(parsedDate.getTime())) {
    return value;
  }

  return parsedDate.toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
  });
};

const getStatusKeyForMetric = (metricKey: string): StatusKey | null => {
  if (metricKey === 'completedProjects') return 'completed';
  if (metricKey === 'inProgressProjects') return 'in-progress';
  if (metricKey === 'pendingProjects') return 'pending';
  return null;
};

const getProjectsByStatus = (projects: Project[], statusKey: StatusKey) => {
  if (statusKey === 'completed') {
    return projects.filter((project) => project.status && project.status.toLowerCase().includes('complete'));
  }

  if (statusKey === 'in-progress') {
    return projects.filter((project) => project.status && project.status.toLowerCase().includes('active'));
  }

  return projects.filter((project) => project.status && project.status.toLowerCase().includes('pending'));
};

function AnalyticsPage() {
  const navigate = useNavigate();
  const [analytics, setAnalytics] = useState<CompanyAnalytics | null>(null);
  const [projectsByStatus, setProjectsByStatus] = useState<Record<StatusKey, Project[]>>({
    completed: [],
    'in-progress': [],
    pending: [],
  });
  const [statusLoading, setStatusLoading] = useState<Record<StatusKey, boolean>>({
    completed: false,
    'in-progress': false,
    pending: false,
  });
  const [statusError, setStatusError] = useState<Record<StatusKey, string | null>>({
    completed: null,
    'in-progress': null,
    pending: null,
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [expandedStatus, setExpandedStatus] = useState<StatusKey | null>(null);

  useEffect(() => {
    const loadAnalytics = async () => {
      try {
        setLoading(true);
        setError(null);
        setAnalytics(await getCompanyAnalytics());
      } catch (loadError) {
        setError(loadError instanceof Error ? loadError.message : 'Failed to load analytics');
      } finally {
        setLoading(false);
      }
    };

    loadAnalytics();
  }, []);

  const handleMetricClick = async (metricKey: string) => {
    const statusKey = getStatusKeyForMetric(metricKey);
    if (!statusKey) {
      return;
    }

    if (expandedStatus === statusKey) {
      setExpandedStatus(null);
      return;
    }

    if (projectsByStatus[statusKey].length > 0 || (analytics && analytics[metricKey as keyof CompanyAnalytics] === 0)) {
      setExpandedStatus(statusKey);
      return;
    }

    try {
      setStatusLoading((current) => ({ ...current, [statusKey]: true }));
      setStatusError((current) => ({ ...current, [statusKey]: null }));
      const projects = await getProjects();
      const filteredProjects = getProjectsByStatus(projects, statusKey);
      setProjectsByStatus((current) => ({ ...current, [statusKey]: filteredProjects }));
      setExpandedStatus(statusKey);
    } catch (loadError) {
      setStatusError((current) => ({
        ...current,
        [statusKey]: loadError instanceof Error ? loadError.message : `Failed to load ${statusKey} projects`,
      }));
    } finally {
      setStatusLoading((current) => ({ ...current, [statusKey]: false }));
    }
  };

  const renderProjectsList = (statusKey: StatusKey) => {
    const projects = projectsByStatus[statusKey];
    const isLoading = statusLoading[statusKey];
    const hasError = statusError[statusKey];

    return (
      <section className="analytics-project-list" style={{ padding: '22px', border: '1px solid #dbe3ef', borderRadius: '8px', background: '#f8fafc', marginBottom: '24px' }}>
        <div className="analytics-project-list-header" style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '16px', gap: '16px' }}>
          <h2 className="analytics-project-list-title" style={{ margin: 0, color: '#172033' }}>
            {statusKey === 'completed' ? 'Completed projects' : statusKey === 'in-progress' ? 'In-progress projects' : 'Pending projects'}
          </h2>
          <button
            type="button"
            className="analytics-project-hide-button"
            onClick={() => setExpandedStatus(null)}
            style={{ border: '1px solid #3b4a66', background: '#1f2937', color: '#93c5fd', borderRadius: '6px', padding: '8px 12px', cursor: 'pointer' }}
          >
            Hide list
          </button>
        </div>

        {isLoading && <p className="analytics-project-content">Loading {statusKey === 'completed' ? 'completed projects' : statusKey === 'in-progress' ? 'in-progress projects' : 'pending projects'}...</p>}
        {hasError && <p className="analytics-project-content analytics-project-error" role="alert" style={{ color: '#c62828' }}>{hasError}</p>}

        {!isLoading && !hasError && projects.length === 0 && (
          <p className="analytics-project-content">No {statusKey === 'completed' ? 'completed projects' : statusKey === 'in-progress' ? 'in-progress projects' : 'pending projects'} available.</p>
        )}

        {!isLoading && !hasError && projects.length > 0 && (
          <div className="analytics-project-items" style={{ display: 'grid', gap: '12px' }}>
            {projects.map((project) => (
              <div key={project.id} className="analytics-project-item" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: '16px', padding: '14px 16px', border: '1px solid #dbe3ef', borderRadius: '8px', background: '#fff' }}>
                <div>
                  <h3 className="analytics-project-name" style={{ margin: '0 0 8px', color: '#172033', fontSize: '18px' }}>{project.name}</h3>
                  <div className="analytics-project-meta" style={{ display: 'flex', gap: '18px', flexWrap: 'wrap', color: '#334155', fontSize: '14px' }}>
                    <span><strong>Start:</strong> {formatDate(project.startDate)}</span>
                    <span><strong>End:</strong> {formatDate(project.endDate)}</span>
                  </div>
                </div>

                <button
                  type="button"
                  className="analytics-project-button"
                  onClick={() => navigate(`/projects/${project.id}`)}
                  style={{ border: '1px solid #3b4a66', borderRadius: '6px', background: '#1f2937', color: '#93c5fd', padding: '9px 14px', cursor: 'pointer', fontWeight: '600' }}
                >
                  View more details
                </button>
              </div>
            ))}
          </div>
        )}
      </section>
    );
  };

  return (
    <main className="analytics-page" style={{ padding: '0 20px 40px' }}>
      <div className="analytics-header" style={{ marginBottom: '28px' }}>
        <p className="analytics-kicker" style={{ margin: '0 0 6px', color: '#64748b', fontSize: '13px', fontWeight: '700', letterSpacing: '0.08em', textTransform: 'uppercase' }}>Company overview</p>
        <h1 className="analytics-title" style={{ margin: 0, color: '#172033' }}>Analytics</h1>
        <p className="analytics-subtitle" style={{ color: '#64748b' }}>A live view of your organization and project portfolio.</p>
      </div>

      {loading && <p className="analytics-state">Loading company analytics...</p>}
      {error && <p className="analytics-state analytics-error" role="alert" style={{ padding: '14px', border: '1px solid #f44336', color: '#c62828' }}>{error}</p>}

      {!loading && !error && analytics && (
        <>
          <section className="analytics-metrics" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '16px', marginBottom: '28px' }}>
            {metricCards.map((metric) => {
              const statusKey = getStatusKeyForMetric(metric.key);
              const isInteractive = Boolean(statusKey);

              return (
                <article
                  key={metric.key}
                  className="analytics-metric-card"
                  style={{
                    padding: '20px',
                    border: '1px solid #dbe3ef',
                    borderRadius: '8px',
                    background: '#fff',
                    cursor: isInteractive ? 'pointer' : 'default',
                    transition: 'all 0.2s ease',
                    boxShadow: isInteractive && expandedStatus === statusKey ? '0 8px 20px rgba(37, 99, 235, 0.08)' : 'none',
                  }}
                  onClick={isInteractive ? () => handleMetricClick(metric.key) : undefined}
                >
                  <p className="analytics-metric-label" style={{ margin: '0 0 14px', color: '#64748b', fontSize: '14px' }}>{metric.label}</p>
                  <strong className="analytics-metric-value" style={{ color: '#172033', fontSize: '32px' }}>{analytics[metric.key]}</strong>
                </article>
              );
            })}
          </section>

          {expandedStatus && renderProjectsList(expandedStatus)}

          <section className="analytics-status-panel" style={{ padding: '22px', border: '1px solid #dbe3ef', borderRadius: '8px', background: '#f8fafc' }}>
            <h2 className="analytics-status-title" style={{ marginTop: 0, color: '#172033' }}>Project status</h2>
            {[
              ['Completed', analytics.completedProjects],
              ['In progress', analytics.inProgressProjects],
              ['Pending', analytics.pendingProjects],
            ].map(([label, value]) => (
              <div key={label} className="analytics-status-row" style={{ display: 'flex', justifyContent: 'space-between', padding: '12px 0', borderBottom: '1px solid #dbe3ef', color: '#334155' }}>
                <span>{label}</span>
                <strong>{value}</strong>
              </div>
            ))}
          </section>
        </>
      )}
    </main>
  );
}

export default AnalyticsPage;