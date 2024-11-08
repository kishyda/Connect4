import React from 'react';
import './WinScreen.css'; // We'll define the CSS styles here

type Prop = {
    message: string,
    onClose: () => void;
}

const WinScreen: React.FC<Prop> = ({ message, onClose }) => {
  return (
    <div className="win-screen-container">
      <div className="win-screen-overlay">
        <h1>{message || 'You Win!'}</h1>
        <button onClick={onClose}>Close</button>
      </div>
    </div>
  );
};

export default WinScreen;

