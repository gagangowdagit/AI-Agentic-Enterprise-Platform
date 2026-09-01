import { Link } from 'react-router-dom';

function HomePage() {
  const sections = [
    { name: 'Projects', path: '/projects' },
    { name: 'Documents', path: '/documents' },
    { name: 'Teams', path: '/teams' },
    { name: 'Nova AI', path: '/nova-ai' },
  ];

  return (
    <div>
      <h1>Home</h1>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: '20px', marginTop: '20px' }}>
        {sections.map((section) => (
          <Link
            key={section.path}
            to={section.path}
            style={{
              padding: '20px',
              border: '1px solid #ccc',
              borderRadius: '4px',
              textDecoration: 'none',
              color: 'black',
              backgroundColor: '#f5f5f5',
              textAlign: 'center',
              cursor: 'pointer',
              transition: 'background-color 0.2s',
            }}
            onMouseEnter={(e) => (e.currentTarget.style.backgroundColor = '#e0e0e0')}
            onMouseLeave={(e) => (e.currentTarget.style.backgroundColor = '#f5f5f5')}
          >
            <h2>{section.name}</h2>
          </Link>
        ))}
      </div>
    </div>
  );
}

export default HomePage;