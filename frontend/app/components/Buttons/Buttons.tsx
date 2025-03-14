import React from 'react';
import './Buttons.css'; // Import the CSS file
import { redirect, useNavigate } from 'react-router';

// Define the type for the props

const Buttons: React.FC = () => {
    const navigate = useNavigate()
    return (
        <div className="button-container">
            <button className="button" onClick={() => navigate("/onlinegame")}>
            &lt;Online PvP&gt;
            </button>
            <button className="button" onClick={() => navigate("/aigame")}>
            &lt;PvAI&gt;
            </button>
            <button className="button" onClick={() => navigate("/localgame")}>
            &lt;Local PvP&gt;
            </button>
        </div>
    );
};

export default Buttons;

