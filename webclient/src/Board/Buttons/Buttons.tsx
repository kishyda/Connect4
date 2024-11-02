import React, { useState, Dispatch, SetStateAction } from 'react';
import './Buttons.css'; // Import the CSS file

// Define the type for the props
type Props = {
  setPvAI: Dispatch<SetStateAction<boolean>>;
  setOnlinePvP: Dispatch<SetStateAction<boolean>>;
  setLocalPvP: Dispatch<SetStateAction<boolean>>;
};

const Buttons: React.FC<Props> = ({ setPvAI, setOnlinePvP, setLocalPvP}) => {
  return (
    <div className="button-container">
      <button className="button" onClick={() => setOnlinePvP(true)}>
        Online PVP
      </button>
      <button className="button" onClick={() => setPvAI(true)}>
        PvAI
      </button>
      <button className="button" onClick={() => setLocalPvP(true)}>
        Local PVP
      </button>
    </div>
  );
};

export default Buttons;

