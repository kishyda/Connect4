import React from 'react';

const Title: React.FC = () => {
  return (
    <h1 style={titleStyle}>
      🎉 Connect 4: The Ultimate Challenge! 🎉
    </h1>
  );
};

// Style for the title
const titleStyle: React.CSSProperties = {
  textAlign: 'center',
  fontSize: '3rem',
  color: '#4A90E2', // Blue color
  textShadow: '2px 2px 4px rgba(0, 0, 0, 0.3)',
  margin: '20px 0',
  fontFamily: '"Arial", sans-serif',
};

export default Title;
