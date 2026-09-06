import { useEffect, useState } from 'react';
import { getCompanyAnalytics, type CompanyAnalytics } from '../services/companyAnalyticsApi';

const metricCards = [
  { key: 'totalProjects', label: 'Total projects' },
  { key: 'totalDepartments', label: 'Total departments' },
  { key: 'totalEmployees', label: 'Total employees' },
  { key: 'completedProjects', label: 'Completed projects' },
  { key: 'inProgressProjects', label: 'In-progress projects' },
  { key: 'pendingProjects', label: 'Pending projects' },
] as const;

function AnalyticsPage() {
  const [analytics, setAnalytics] = useState<CompanyAnalytics | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

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

  return (
    <main style={{ padding: '0 20px 40px' }}>
      <div style={{ marginBottom: '28px' }}>
        <p style={{ margin: '0 0 6px', color: '#64748b', fontSize: '13px', fontWeight: '700', letterSpacing: '0.08em', textTransform: 'uppercase' }}>Company overview</p>
        <h1 style={{ margin: 0, color: '#172033' }}>Analytics</h1>
        <p style={{ color: '#64748b' }}>A live view of your organization and project portfolio.</p>
      </div>

      {loading && <p>Loading company analytics...</p>}
      {error && <p role="alert" style={{ padding: '14px', border: '1px solid #f44336', color: '#c62828' }}>{error}</p>}

      {!loading && !error && analytics && (
        <>
          <section style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '16px', marginBottom: '28px' }}>
            {metricCards.map((metric) => (
              <article key={metric.key} style={{ padding: '20px', border: '1px solid #dbe3ef', borderRadius: '8px', background: '#fff' }}>
                <p style={{ margin: '0 0 14px', color: '#64748b', fontSize: '14px' }}>{metric.label}</p>
                <strong style={{ color: '#172033', fontSize: '32px' }}>{analytics[metric.key]}</strong>
              </article>
            ))}
          </section>

          <section style={{ padding: '22px', border: '1px solid #dbe3ef', borderRadius: '8px', background: '#f8fafc' }}>
            <h2 style={{ marginTop: 0, color: '#172033' }}>Project status</h2>
            {[
              ['Completed', analytics.completedProjects],
              ['In progress', analytics.inProgressProjects],
              ['Pending', analytics.pendingProjects],
            ].map(([label, value]) => (
              <div key={label} style={{ display: 'flex', justifyContent: 'space-between', padding: '12px 0', borderBottom: '1px solid #dbe3ef', color: '#334155' }}>
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