import React, { useState, useEffect } from 'react';
import API from '../api/api';
import StatCard from '../components/StatCard';
import TaskCard from '../components/TaskCard';
import SubmitTaskModal from '../components/SubmitTaskModal';
import { ListTodo, PlayCircle, Send, CheckCircle2, AlertCircle } from 'lucide-react';

const EmployeeDashboard = () => {
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('ALL');

  const [selectedTaskToSubmit, setSelectedTaskToSubmit] = useState(null);

  const fetchTasks = async () => {
    setLoading(true);
    try {
      const res = await API.get('/tasks');
      setTasks(res.data);
    } catch (err) {
      console.error('Failed to load employee tasks', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTasks();
  }, []);

  const handleStartTask = async (taskId) => {
    try {
      await API.put(`/tasks/${taskId}/start`);
      fetchTasks();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to start task');
    }
  };

  const totalTasks = tasks.length;
  const inProgressCount = tasks.filter(t => t.status === 'IN_PROGRESS').length;
  const submittedCount = tasks.filter(t => t.status === 'SUBMITTED').length;
  const approvedCount = tasks.filter(t => t.status === 'APPROVED').length;
  const actionRequiredCount = tasks.filter(t => t.status === 'ASSIGNED' || t.status === 'CHANGES_REQUESTED').length;

  const filteredTasks = tasks.filter(task => {
    if (filter === 'ALL') return true;
    return task.status === filter;
  });

  return (
    <div className="container">
      {/* Dashboard Header */}
      <div className="tasks-header">
        <div>
          <h1 style={{ fontSize: '1.75rem', fontWeight: 800, color: '#0f172a' }}>Employee Workspace</h1>
          <p style={{ color: '#64748b' }}>View assigned tasks, work on deliverables, and submit GitHub code for review.</p>
        </div>
      </div>

      {/* Stat Cards */}
      <div className="stats-grid">
        <StatCard title="Action Required" count={actionRequiredCount} icon={ListTodo} color="#4f46e5" bgColor="#eef2ff" />
        <StatCard title="In Progress" count={inProgressCount} icon={PlayCircle} color="#3b82f6" bgColor="#eff6ff" />
        <StatCard title="Submitted (Pending Review)" count={submittedCount} icon={Send} color="#d97706" bgColor="#fffbeb" />
        <StatCard title="Approved" count={approvedCount} icon={CheckCircle2} color="#10b981" bgColor="#ecfdf5" />
      </div>

      {/* Filter Tabs */}
      <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1.5rem', overflowX: 'auto', paddingBottom: '0.5rem' }}>
        {['ALL', 'ASSIGNED', 'IN_PROGRESS', 'SUBMITTED', 'CHANGES_REQUESTED', 'APPROVED'].map((statusKey) => (
          <button
            key={statusKey}
            onClick={() => setFilter(statusKey)}
            className={`btn ${filter === statusKey ? 'btn-primary' : 'btn-outline'}`}
            style={{ fontSize: '0.85rem', padding: '0.4rem 0.9rem' }}
          >
            {statusKey === 'ALL' ? 'All Assigned Tasks' : statusKey.replace('_', ' ')}
            {statusKey === 'CHANGES_REQUESTED' && tasks.some(t => t.status === 'CHANGES_REQUESTED') && (
              <span style={{ background: '#ef4444', color: '#fff', borderRadius: '99px', padding: '0.1rem 0.4rem', fontSize: '0.7rem' }}>
                !
              </span>
            )}
          </button>
        ))}
      </div>

      {/* Task List */}
      {loading ? (
        <div style={{ textAlign: 'center', padding: '3rem', color: '#64748b' }}>Loading your tasks...</div>
      ) : filteredTasks.length === 0 ? (
        <div style={{ background: '#ffffff', padding: '3rem', borderRadius: '12px', textAlign: 'center', border: '1px solid #e2e8f0' }}>
          <ListTodo size={48} color="#cbd5e1" style={{ marginBottom: '1rem' }} />
          <h3 style={{ fontSize: '1.1rem', fontWeight: 600, color: '#334155' }}>No tasks found</h3>
          <p style={{ color: '#64748b', fontSize: '0.9rem', marginTop: '0.25rem' }}>
            {filter === 'ALL' ? 'You currently have no tasks assigned.' : `No tasks matching status "${filter}".`}
          </p>
        </div>
      ) : (
        <div className="task-grid">
          {filteredTasks.map((task) => (
            <TaskCard
              key={task.id}
              task={task}
              userRole="EMPLOYEE"
              onStart={handleStartTask}
              onSubmit={(t) => setSelectedTaskToSubmit(t)}
            />
          ))}
        </div>
      )}

      {/* Submit Task Modal */}
      <SubmitTaskModal
        task={selectedTaskToSubmit}
        isOpen={!!selectedTaskToSubmit}
        onClose={() => setSelectedTaskToSubmit(null)}
        onTaskSubmitted={fetchTasks}
      />
    </div>
  );
};

export default EmployeeDashboard;

