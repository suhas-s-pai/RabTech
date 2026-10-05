import React from 'react';
import StatusBadge from './StatusBadge';
import { Calendar, User as UserIcon, Github, MessageSquare, Play, Send, CheckSquare, ExternalLink } from 'lucide-react';

const TaskCard = ({ task, userRole, onStart, onSubmit, onReview }) => {
  const isEmployee = userRole === 'EMPLOYEE';
  const isManager = userRole === 'MANAGER';

  const formattedDeadline = task.deadline ? new Date(task.deadline).toLocaleDateString(undefined, {
    month: 'short', day: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit'
  }) : 'No deadline';

  return (
    <div className="task-card">
      <div>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.75rem' }}>
          <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: '#0f172a' }}>{task.title}</h3>
          <StatusBadge status={task.status} />
        </div>

        <p style={{ color: '#475569', fontSize: '0.9rem', marginBottom: '1rem', whiteSpace: 'pre-line' }}>
          {task.description}
        </p>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.4rem', fontSize: '0.85rem', color: '#64748b', marginBottom: '1rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
            <Calendar size={14} />
            <span>Deadline: <strong>{formattedDeadline}</strong></span>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
            <UserIcon size={14} />
            <span>Assigned To: <strong>{task.assignedTo?.name || 'Unassigned'}</strong></span>
          </div>

          {task.createdBy && (
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
              <CheckSquare size={14} />
              <span>Created By: <strong>{task.createdBy.name}</strong></span>
            </div>
          )}
        </div>

        {/* GitHub Link & Submission Details */}
        {task.githubUrl && (
          <div style={{ background: '#f8fafc', padding: '0.75rem', borderRadius: '8px', border: '1px solid #e2e8f0', marginBottom: '1rem', fontSize: '0.85rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', marginBottom: '0.25rem' }}>
              <Github size={15} color="#0f172a" />
              <a href={task.githubUrl} target="_blank" rel="noopener noreferrer" style={{ color: '#4f46e5', fontWeight: 600, wordBreak: 'break-all' }}>
                GitHub Repository <ExternalLink size={12} style={{ display: 'inline' }} />
              </a>
            </div>
            {task.submissionComment && (
              <p style={{ color: '#334155', fontStyle: 'italic', marginTop: '0.25rem' }}>
                "{task.submissionComment}"
              </p>
            )}
          </div>
        )}

        {/* Manager Feedback */}
        {task.managerFeedback && (
          <div style={{
            background: task.status === 'APPROVED' ? '#f0fdf4' : '#fef2f2',
            borderLeft: `4px solid ${task.status === 'APPROVED' ? '#10b981' : '#ef4444'}`,
            padding: '0.75rem',
            borderRadius: '4px',
            marginBottom: '1rem',
            fontSize: '0.85rem'
          }}>
            <strong style={{ color: task.status === 'APPROVED' ? '#15803d' : '#b91c1c', display: 'flex', alignItems: 'center', gap: '0.3rem' }}>
              <MessageSquare size={14} />
              Manager Feedback:
            </strong>
            <p style={{ color: '#334155', marginTop: '0.2rem' }}>{task.managerFeedback}</p>
          </div>
        )}
      </div>

      {/* Action Buttons */}
      <div style={{ borderTop: '1px solid #e2e8f0', paddingTop: '1rem', marginTop: '0.5rem', display: 'flex', justifyContent: 'flex-end', gap: '0.5rem' }}>
        {isEmployee && (task.status === 'ASSIGNED' || task.status === 'CHANGES_REQUESTED') && (
          <button onClick={() => onStart(task.id)} className="btn btn-primary btn-sm" style={{ width: '100%' }}>
            <Play size={16} />
            Start Task
          </button>
        )}

        {isEmployee && task.status === 'IN_PROGRESS' && (
          <button onClick={() => onSubmit(task)} className="btn btn-success btn-sm" style={{ width: '100%' }}>
            <Send size={16} />
            Submit Work
          </button>
        )}

        {isManager && task.status === 'SUBMITTED' && (
          <button onClick={() => onReview(task)} className="btn btn-warning btn-sm" style={{ width: '100%' }}>
            <CheckSquare size={16} />
            Review Submission
          </button>
        )}
      </div>
    </div>
  );
};

export default TaskCard;

