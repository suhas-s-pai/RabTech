import React from 'react';

const StatCard = ({ title, count, icon: Icon, color, bgColor }) => {
  return (
    <div className="stat-card">
      <div className="stat-icon" style={{ backgroundColor: bgColor, color: color }}>
        <Icon size={24} />
      </div>
      <div className="stat-info">
        <h3>{count}</h3>
        <p>{title}</p>
      </div>
    </div>
  );
};

export default StatCard;

