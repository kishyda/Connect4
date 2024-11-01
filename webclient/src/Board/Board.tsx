import React, { useEffect, useState } from 'react';
import Column from './Column/Column';
import Buttons from './Buttons/Buttons';
import './Board.css'

type Board = number[][];

type Game = {
  CPU: boolean;
  Online: boolean;
  gameId: number;
  playerId: number;
  board: number[][];
}

const App: React.FC = () => {
  const yCoordinates = [20, 60, 100, 140, 180, 220]; // common y-coordinates for each row of circles
  const [board, setBoard] = useState<Board>(Array.from({length: 7},()=>Array.from({length: 6}, () => 0)));
  const [turn, setTurn] = useState(1);

  const handleCircleClick = (id: number) => {
    const newBoard = board.map(row  => [...row]);
    for (let i = 0; i < 7; i++) {
      if (board[id][i] === 0 && turn === 1) {
        newBoard[id][i] = 1;
        setTurn(2);
        break;
      } else if (board[id][i] === 0 && turn === 2) {
        newBoard[id][i] = 2;
        setTurn(1);
        break;
      }
      // SEND TO THE SERVER THE GAME ID, PLAYER MOVE, 
      //useEffect(() => {
      //  const response = fetch("GET /").then(r => {
      //    console.log(r);
      //  })
      //})
    }
    setBoard(newBoard);
  };

  return (
    <div style={{display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center'}}>
      <div style={{}}>
        <button>Offline</button>
        <button>Online</button>
      </div>
      <svg
        className='Board'
        style={{ width: '100%', height: 'auto', maxWidth: '700px' }}
        viewBox="0 0 280 240"
        xmlns="http://www.w3.org/2000/svg">
        <rect width="280" height="240" fill="gray" rx="10" />
        <g>
          <g onClick={() => handleCircleClick(0)}>
            <Column x={20} yCoordinates={yCoordinates} xCoordinates={0} board={board}/>
          </g>
          <g onClick={() => handleCircleClick(1)}>
            <Column x={60} yCoordinates={yCoordinates} xCoordinates={1} board={board}/>
          </g>
          <g onClick={() => handleCircleClick(2)}>
            <Column x={100} yCoordinates={yCoordinates} xCoordinates={2} board={board}/>
          </g>
          <g onClick={() => handleCircleClick(3)}>
            <Column x={140} yCoordinates={yCoordinates} xCoordinates={3} board={board}/>
          </g>
          <g onClick={() => handleCircleClick(4)}>
            <Column x={180} yCoordinates={yCoordinates} xCoordinates={4} board={board}/>
          </g>
          <g onClick={() => handleCircleClick(5)}>
            <Column x={220} yCoordinates={yCoordinates} xCoordinates={5} board={board}/>
          </g>
          <g onClick={() => handleCircleClick(6)}>
            <Column x={260} yCoordinates={yCoordinates} xCoordinates={6} board={board}/>
          </g>
        </g>
      </svg>
    </div>
  );
};

export default App;
