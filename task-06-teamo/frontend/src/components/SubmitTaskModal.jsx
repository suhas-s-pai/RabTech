import React, { useState } from 'react';
import API from '../api/api';
import { X, Send, Github } from 'lucide-react';

const SubmitTaskModal = ({ task, isOpen, onClose, onTaskSubmitted }) => {
  const [githubUrl, setGithubUrl] = useState('');
  const [submissionComment, setSubmissionComment] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  if (!isOpen || !task) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      await API.put(`/tasks/${task.id}/submit`, {
        githubUrl,
        submissionComment,
      });

      setGithubUrl('');
      setSubmissionComment('');
      onTaskSubmitted();
      onClose();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to submit task');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
          <h2 style={{ fontSize: '1.25rem', fontWeight: 700, display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Send color="#4f46e5" size={22} />
            Submit Work for "{task.title}"
          </h2>
          <button onClick={onClose} className="btn-outline" style={{ border: 'none', padding: '0.2rem' }}>
            <X size={20} />
          </button>
        </div>

        {error && (
          <div style={{ background: '#fee2e2', color: '#b91c1c', padding: '0.75rem', borderRadius: '8px', marginBottom: '1rem', fontSize: '0.875rem' }}>
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
              <Github size={16} />
              GitHub Repository URL *
            </label>
            <input
              type="url"
              placeholder="https://github.com/suhas-s-pai/RabTech/tree/main/task-06-teamo"
              value={githubUrl}
              onChange={(e) => setGithubUrl(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label>Submission Comments / Notes</label>
            <textarea
              rows="4"
              placeholder="Explain completed features, key implementation details, test coverage, and instructions for review..."
              value={submissionComment}
              onChange={(e) => setSubmissionComment(e.target.value)}
            />
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
            <button type="button" onClick={onClose} className="btn btn-secondary">
              Cancel
            </button>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'Submitting...' : 'Submit Work'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default SubmitTaskModal;

