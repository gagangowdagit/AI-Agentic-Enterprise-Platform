import { Link } from 'react-router-dom';

function HomePage() {
  const sections = [
    { name: 'Projects', path: '/projects', description: 'Plan, track, and manage active work.' },
    { name: 'Documents', path: '/documents', description: 'Upload and explore project knowledge.' },
    { name: 'Departments', path: '/departments', description: 'Organize teams and ownership.' },
    { name: 'Nova AI', path: '/nova-ai', description: 'Ask questions across your knowledge base.' },
    { name: 'Analytics', path: '/analytics', description: 'Understand company-wide performance.' },
  ];

  return (
    <main className="home-page">
      <header className="home-hero">
        <p className="home-eyebrow">Enterprise workspace</p>
        <h1>
        AI Agentic Enterprise Platform
        </h1>
        <p>One place to coordinate projects, people, documents, and intelligent decisions.</p>
      </header>
      <section className="home-section-grid" aria-label="Platform sections">
        {sections.map((section) => (
          <Link
            key={section.path}
            to={section.path}
            className="home-section-card"
          >
            <h2>{section.name}</h2>
            <p>{section.description}</p>
          </Link>
        ))}
      </section>
    </main>
  );
}

export default HomePage;