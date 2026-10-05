import React, { useState, useEffect } from 'react';
import API from '../api/api';
import StatCard from '../components/StatCard';
import TaskCard from '../components/TaskCard';
import CreateTaskModal from '../components/CreateTaskModal';
import ReviewTaskModal from '../components/ReviewTaskModal';
import { Plus, ListTodo, Send, CheckCircle2, Clock, PlayCircle, AlertCircle } from 'lucide-react';

const ManagerDashboard = () => {
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('ALL');

  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [selectedTaskToReview, setSelectedTaskToReview] = useState(null);

  const fetchTasks = async () => {
    setLoading(true);
    try {
      const res = await API.get('/tasks');
      setTasks(res.data);
    } catch (err) {
      console.error('Failed to load manager tasks', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTasks();
  }, []);

  const totalTasks = tasks.length;
  const pendingReviewCount = tasks.filter(t => t.status === 'SUBMITTED').length;
  const approvedCount = tasks.filter(t => t.status === 'APPROVED').length;
  const inProgressCount = tasks.filter(t => t.status === 'IN_PROGRESS').length;

  const filteredTasks = tasks.filter(task => {
    if (filter === 'ALL') return true;
    return task.status === filter;
  });

  return (
    <div className="container">
      {/* Dashboard Header */}
      <div className="tasks-header">
        <div>
          <h1 style={{ fontSize: '1.75rem', fontWeight: 800, color: '#0f172a' }}>Manager Dashboard</h1>
          <p style={{ color: '#64748b' }}>Create tasks, assign developers, and review code submissions.</p>
        </div>

        <button onClick={() => setIsCreateOpen(true)} className="btn btn-primary">
          <Plus size={20} />
          Create New Task
        </button>
      </div>

      {/* Stat Cards */}
      <div className="stats-grid">
        <StatCard title="Total Created" count={totalTasks} icon={ListTodo} color="#4f46e5" bgColor="#eef2ff" />
        <StatCard title="Pending Review" count={pendingReviewCount} icon={Send} color="#d97706" bgColor="#fffbeb" />
        <StatCard title="In Progress" count={inProgressCount} icon={PlayCircle} color="#3b82f6" bgColor="#eff6ff" />
        <StatCard title="Approved" count={approvedCount} icon={CheckCircle2} color="#10b981" bgColor="#ecfdf5" />
      </div>

      {/* Filter Tabs */}
      <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1.5rem', overflowX: 'auto', paddingBottom: '0.5rem' }}>
        {['ALL', 'SUBMITTED', 'IN_PROGRESS', 'ASSIGNED', 'APPROVED', 'CHANGES_REQUESTED'].map((statusKey) => (
          <button
            key={statusKey}
            onClick={() => setFilter(statusKey)}
            className={`btn ${filter === statusKey ? 'btn-primary' : 'btn-outline'}`}
            style={{ fontSize: '0.85rem', padding: '0.4rem 0.9rem' }}
          >
            {statusKey === 'ALL' ? 'All Tasks' : statusKey.replace('_', ' ')}
            {statusKey === 'SUBMITTED' && pendingReviewCount > 0 && (
              <span style={{ background: '#ef4444', color: '#fff', borderRadius: '99px', padding: '0.1rem 0.4rem', fontSize: '0.7rem' }}>
                {pendingReviewCount}
              </span>
            )}
          </button>
        ))}
      </div>

      {/* Task List */}
      {loading ? (
        <div style={{ textAlign: 'center', padding: '3rem', color: '#64748b' }}>Loading tasks...</div>
      ) : filteredTasks.length === 0 ? (
        <div style={{ background: '#ffffff', padding: '3rem', borderRadius: '12px', textAlign: 'center', border: '1px solid #e2e8f0' }}>
          <ListTodo size={48} color="#cbd5e1" style={{ marginBottom: '1rem' }} />
          <h3 style={{ fontSize: '1.1rem', fontWeight: 600, color: '#334155' }}>No tasks found</h3>
          <p style={{ color: '#64748b', fontSize: '0.9rem', marginTop: '0.25rem' }}>
            {filter === 'ALL' ? 'Get started by creating your first task above.' : `No tasks matching status "${filter}".`}
          </p>
        </div>
      ) : (
        <div className="task-grid">
          {filteredTasks.map((task) => (
            <TaskCard
              key={task.id}
              task={task}
              userRole="MANAGER"
              onReview={(t) => setSelectedTaskToReview(t)}
            />
          ))}
        </div>
      )}

      {/* Modals */}
      <CreateTaskModal
        isOpen={isCreateOpen}
        onClose={() => setIsCreateOpen(false)}
        onTaskCreated={fetchTasks}
      />

      <ReviewTaskModal
        task={selectedTaskToReview}
        isOpen={!!selectedTaskToReview}
        onClose={() => setSelectedTaskToReview(null)}
        onTaskReviewed={fetchTasks}
      />
    </div>
  );
};

export default ManagerDashboard;

