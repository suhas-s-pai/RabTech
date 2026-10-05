import React from 'react';
import { Clock, PlayCircle, Send, CheckCircle2, AlertCircle } from 'lucide-react';

const StatusBadge = ({ status }) => {
  const getBadgeConfig = () => {
    switch (status) {
      case 'ASSIGNED':
        return { label: 'Assigned', class: 'badge-assigned', icon: Clock };
      case 'IN_PROGRESS':
        return { label: 'In Progress', class: 'badge-in_progress', icon: PlayCircle };
      case 'SUBMITTED':
        return { label: 'Submitted', class: 'badge-submitted', icon: Send };
      case 'APPROVED':
        return { label: 'Approved', class: 'badge-approved', icon: CheckCircle2 };
      case 'CHANGES_REQUESTED':
        return { label: 'Changes Requested', class: 'badge-changes_requested', icon: AlertCircle };
      default:
        return { label: status, class: 'badge-assigned', icon: Clock };
    }
  };

  const config = getBadgeConfig();
  const Icon = config.icon;

  return (
    <span className={`badge ${config.class}`}>
      <Icon size={14} />
      {config.label}
    </span>
  );
};

export default StatusBadge;

