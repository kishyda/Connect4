import React from 'react';
import './TopBar.css'; // Importing the CSS file for styling

const TopBar = () => {
  return (
    <div className="topbar">
      <div className="logo">MyWebsite</div>
      <div className="nav-links">
        <a href="#home" className="nav-link">Home</a>
        <a href="#about" className="nav-link">About</a>
        <a href="#services" className="nav-link">Services</a>
        <a href="#contact" className="nav-link">Contact</a>
      </div>
      <div className="user-profile">
        <a href="#profile" className="profile-link">Profile</a>
      </div>
    </div>
  );
};

export default TopBar;

