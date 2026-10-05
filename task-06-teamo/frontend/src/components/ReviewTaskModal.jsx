import React, { useState } from 'react';
import API from '../api/api';
import { X, CheckCircle2, AlertCircle, ExternalLink, Github } from 'lucide-react';

const ReviewTaskModal = ({ task, isOpen, onClose, onTaskReviewed }) => {
  const [feedback, setFeedback] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  if (!isOpen || !task) return null;

  const handleAction = async (actionType) => {
    setLoading(true);
    setError('');

    try {
      const endpoint = actionType === 'APPROVE' ? `/tasks/${task.id}/approve` : `/tasks/${task.id}/request-changes`;
      await API.put(endpoint, { feedback });

      setFeedback('');
      onTaskReviewed();
      onClose();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to complete review');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content" style={{ maxWidth: '640px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.25rem' }}>
          <h2 style={{ fontSize: '1.25rem', fontWeight: 700 }}>Review Submission: "{task.title}"</h2>
          <button onClick={onClose} className="btn-outline" style={{ border: 'none', padding: '0.2rem' }}>
            <X size={20} />
          </button>
        </div>

        {error && (
          <div style={{ background: '#fee2e2', color: '#b91c1c', padding: '0.75rem', borderRadius: '8px', marginBottom: '1rem', fontSize: '0.875rem' }}>
            {error}
          </div>
        )}

        <div style={{ background: '#f8fafc', padding: '1rem', borderRadius: '8px', border: '1px solid #e2e8f0', marginBottom: '1.25rem' }}>
          <p style={{ fontSize: '0.875rem', color: '#64748b', marginBottom: '0.5rem' }}>
            Submitted by <strong>{task.assignedTo?.name}</strong> on {task.submittedAt ? new Date(task.submittedAt).toLocaleString() : 'N/A'}
          </p>

          <div style={{ marginBottom: '0.75rem' }}>
            <strong style={{ fontSize: '0.85rem', color: '#334155' }}>GitHub Repository:</strong>
            <div style={{ marginTop: '0.25rem' }}>
              <a
                href={task.githubUrl}
                target="_blank"
                rel="noopener noreferrer"
                style={{ display: 'inline-flex', alignItems: 'center', gap: '0.4rem', color: '#4f46e5', fontWeight: 600, wordBreak: 'break-all' }}
              >
                <Github size={16} />
                {task.githubUrl}
                <ExternalLink size={14} />
              </a>
            </div>
          </div>

          <div>
            <strong style={{ fontSize: '0.85rem', color: '#334155' }}>Submission Notes:</strong>
            <p style={{ marginTop: '0.25rem', fontSize: '0.9rem', color: '#0f172a', whiteSpace: 'pre-wrap' }}>
              {task.submissionComment || 'No comments provided.'}
            </p>
          </div>
        </div>

        <div className="form-group">
          <label>Manager Review Feedback / Comments</label>
          <textarea
            rows="3"
            placeholder="Add comments or instructions for the employee..."
            value={feedback}
            onChange={(e) => setFeedback(e.target.value)}
          />
        </div>

        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
          <button type="button" onClick={onClose} className="btn btn-secondary" disabled={loading}>
            Cancel
          </button>
          <button
            type="button"
            onClick={() => handleAction('REQUEST_CHANGES')}
            className="btn btn-warning"
            disabled={loading}
          >
            <AlertCircle size={16} />
            Request Changes
          </button>
          <button
            type="button"
            onClick={() => handleAction('APPROVE')}
            className="btn btn-success"
            disabled={loading}
          >
            <CheckCircle2 size={16} />
            Approve Task
          </button>
        </div>
      </div>
    </div>
  );
};

export default ReviewTaskModal;

