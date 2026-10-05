import React, { useState, useEffect } from 'react';
import API from '../api/api';
import { X, PlusCircle } from 'lucide-react';

const CreateTaskModal = ({ isOpen, onClose, onTaskCreated }) => {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [deadline, setDeadline] = useState('');
  const [assignedToId, setAssignedToId] = useState('');
  const [employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (isOpen) {
      // Fetch employees for assignment
      API.get('/users/employees')
        .then((res) => {
          setEmployees(res.data);
          if (res.data.length > 0) {
            setAssignedToId(res.data[0].id);
          }
        })
        .catch(() => setError('Failed to load employee list'));
    }
  }, [isOpen]);

  if (!isOpen) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      // Format deadline to ISO string if needed
      const formattedDeadline = deadline.length === 16 ? `${deadline}:00` : deadline;

      await API.post('/tasks', {
        title,
        description,
        deadline: formattedDeadline,
        assignedToId: Number(assignedToId),
      });

      setTitle('');
      setDescription('');
      setDeadline('');
      onTaskCreated();
      onClose();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create task');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
          <h2 style={{ fontSize: '1.25rem', fontWeight: 700, display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <PlusCircle color="#4f46e5" size={22} />
            Create & Assign New Task
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
            <label>Task Title *</label>
            <input
              type="text"
              placeholder="e.g. Implement JWT Authentication"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label>Task Description *</label>
            <textarea
              rows="4"
              placeholder="Provide clear specifications, requirements, and acceptance criteria..."
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label>Assign To Employee *</label>
            <select value={assignedToId} onChange={(e) => setAssignedToId(e.target.value)} required>
              {employees.length === 0 && <option value="">No employees available</option>}
              {employees.map((emp) => (
                <option key={emp.id} value={emp.id}>
                  {emp.name} ({emp.email})
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label>Deadline *</label>
            <input
              type="datetime-local"
              value={deadline}
              onChange={(e) => setDeadline(e.target.value)}
              required
            />
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
            <button type="button" onClick={onClose} className="btn btn-secondary">
              Cancel
            </button>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'Creating...' : 'Create Task'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CreateTaskModal;

