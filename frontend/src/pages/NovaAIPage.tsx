import { useEffect, useState } from 'react';
import { getProjects, type Project } from '../services/projectApi';

interface ChatMessage {
  id: number;
  role: 'user' | 'assistant';
  content: string;
}

interface RagResponse {
  data?: {
    answer?: string;
  };
  answer?: string;
}

const API_BASE_URL = 'http://localhost:8080/api/v1';
const TOP_K = 3;

function NovaAIPage() {
  const [projects, setProjects] = useState<Project[]>([]);
  const [selectedProjectId, setSelectedProjectId] = useState('');
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [query, setQuery] = useState('');
  const [loadingProjects, setLoadingProjects] = useState(true);
  const [sending, setSending] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const loadProjects = async () => {
      try {
        setLoadingProjects(true);
        const projectList = await getProjects();
        setProjects(projectList);
        if (projectList.length > 0) {
          setSelectedProjectId(projectList[0].id);
        }
      } catch (loadError) {
        setError(loadError instanceof Error ? loadError.message : 'Failed to load projects.');
      } finally {
        setLoadingProjects(false);
      }
    };

    loadProjects();
  }, []);

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const trimmedQuery = query.trim();

    if (!trimmedQuery || !selectedProjectId || sending) {
      return;
    }

    const userMessage: ChatMessage = {
      id: Date.now(),
      role: 'user',
      content: trimmedQuery,
    };

    setMessages((currentMessages) => [...currentMessages, userMessage]);
    setQuery('');
    setError(null);
    setSending(true);

    try {
      const response = await fetch(`${API_BASE_URL}/rag/query`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          projectId: selectedProjectId,
          query: trimmedQuery,
          topK: TOP_K,
        }),
      });

      const responseBody = (await response.json()) as RagResponse;
      if (!response.ok) {
        throw new Error(responseBody.data?.answer || 'The RAG request failed.');
      }

      const answer = responseBody.data?.answer || responseBody.answer;
      if (!answer) {
        throw new Error('The RAG service returned an empty answer.');
      }

      setMessages((currentMessages) => [
        ...currentMessages,
        { id: Date.now() + 1, role: 'assistant', content: answer },
      ]);
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : 'Unable to reach the RAG service.');
    } finally {
      setSending(false);
    }
  };

  return (
    <main style={{ maxWidth: '900px', margin: '0 auto', padding: '24px 20px' }}>
      <header style={{ marginBottom: '24px' }}>
        <p style={{ margin: '0 0 6px', color: '#2563eb', fontWeight: '700', letterSpacing: '0.08em', textTransform: 'uppercase', fontSize: '12px' }}>
          Nova AI
        </p>
        <h1 style={{ margin: 0, color: '#172033', fontSize: '32px' }}>Ask your project knowledge base</h1>
      </header>

      <section style={{ border: '1px solid #dbe3ef', borderRadius: '8px', backgroundColor: '#f8fafc', padding: '18px' }}>
        <label htmlFor="nova-project" style={{ display: 'block', marginBottom: '8px', color: '#334155', fontWeight: '600' }}>
          Project
        </label>
        <select
          id="nova-project"
          value={selectedProjectId}
          onChange={(event) => setSelectedProjectId(event.target.value)}
          disabled={loadingProjects || sending || projects.length === 0}
          style={{ width: '100%', padding: '11px 12px', border: '1px solid #cbd5e1', borderRadius: '6px', backgroundColor: 'white', color: '#172033', fontSize: '15px' }}
        >
          {loadingProjects && <option>Loading projects...</option>}
          {!loadingProjects && projects.length === 0 && <option>No projects available</option>}
          {projects.map((project) => (
            <option key={project.id} value={project.id}>{project.name} ({project.id})</option>
          ))}
        </select>
      </section>

      <section aria-live="polite" style={{ minHeight: '360px', margin: '20px 0', padding: '20px', border: '1px solid #dbe3ef', borderRadius: '8px', backgroundColor: 'white' }}>
        {messages.length === 0 ? (
          <p style={{ margin: 0, color: '#64748b', textAlign: 'center', paddingTop: '130px' }}>Ask a question about the selected project.</p>
        ) : (
          messages.map((message) => (
            <div key={message.id} style={{ display: 'flex', justifyContent: message.role === 'user' ? 'flex-end' : 'flex-start', marginBottom: '14px' }}>
              <div style={{ maxWidth: '78%', padding: '12px 14px', borderRadius: '8px', backgroundColor: message.role === 'user' ? '#2563eb' : '#eef2f7', color: message.role === 'user' ? 'white' : '#172033', whiteSpace: 'pre-wrap', lineHeight: 1.5 }}>
                {message.content}
              </div>
            </div>
          ))
        )}
        {sending && <p style={{ margin: '8px 0 0', color: '#64748b' }}>Nova AI is thinking...</p>}
      </section>

      {error && <p role="alert" style={{ margin: '0 0 14px', padding: '12px', border: '1px solid #fecaca', borderRadius: '6px', backgroundColor: '#fef2f2', color: '#b91c1c' }}>{error}</p>}

      <form onSubmit={handleSubmit} style={{ display: 'flex', gap: '10px', alignItems: 'stretch' }}>
        <label htmlFor="nova-query" style={{ position: 'absolute', width: '1px', height: '1px', padding: 0, margin: '-1px', overflow: 'hidden', clip: 'rect(0, 0, 0, 0)', whiteSpace: 'nowrap', border: 0 }}>
          Ask a question
        </label>
        <input
          id="nova-query"
          value={query}
          onChange={(event) => setQuery(event.target.value)}
          placeholder="Ask a question..."
          disabled={sending || !selectedProjectId}
          style={{ flex: 1, minWidth: 0, padding: '13px 14px', border: '1px solid #cbd5e1', borderRadius: '6px', fontSize: '15px' }}
        />
        <button type="submit" disabled={sending || !selectedProjectId || !query.trim()} style={{ padding: '0 22px', border: 0, borderRadius: '6px', backgroundColor: sending || !selectedProjectId || !query.trim() ? '#94a3b8' : '#172033', color: 'white', fontWeight: '700', cursor: sending || !selectedProjectId || !query.trim() ? 'not-allowed' : 'pointer' }}>
          {sending ? 'Sending...' : 'Send'}
        </button>
      </form>
    </main>
  );
}

export default NovaAIPage;
