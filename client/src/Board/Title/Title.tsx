import React from 'react';
import './Title.css';

const Connect4Title = () => {
  return (
    <div className="connect4-title-container">
      <pre className="title-ascii">
      {`
       .d8888b.   .d88888b.  888b    888 888b    888 8888888888 .d8888b. 88888888888  d8888  
d88P  Y88b d88P" "Y88b 8888b   888 8888b   888 888       d88P  Y88b    888     d8P888  
888    888 888     888 88888b  888 88888b  888 888       888    888    888    d8P 888  
888        888     888 888Y88b 888 888Y88b 888 8888888   888           888   d8P  888  
888        888     888 888 Y88b888 888 Y88b888 888       888           888  d88   888  
888    888 888     888 888  Y88888 888  Y88888 888       888    888    888  8888888888 
Y88b  d88P Y88b. .d88P 888   Y8888 888   Y8888 888       Y88b  d88P    888        888  
 "Y8888P"   "Y88888P"  888    Y888 888    Y888 8888888888 "Y8888P"     888        888
      `}
      </pre>
    </div>
  );
};

export default Connect4Title;

