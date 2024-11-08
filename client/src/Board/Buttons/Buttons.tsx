import React, { useState, Dispatch, SetStateAction } from 'react';
import './Buttons.css'; // Import the CSS file

// Define the type for the props
type Props = {
  setGameMode: Dispatch<SetStateAction<string>>;
};

const Buttons: React.FC<Props> = ({setGameMode}) => {
  return (
    <div className="button-container">
      <button className="button" onClick={() => setGameMode('OnlinePvP')}>
        &lt;Online PvP&gt;
      </button>
      <button className="button" onClick={() => setGameMode('PvAI')}>
        &lt;PvAI&gt;
      </button>
      <button className="button" onClick={() => setGameMode('LocalPvP')}>
        &lt;Local PvP&gt;
      </button>
    </div>
  );
};

export default Buttons;

