import React, { useEffect, useState } from 'react';
import Column from './Column/Column';
import Buttons from './Buttons/Buttons';
import Title from './Title/Title';
import io from 'socket.io-client';

import './Board.css'

type Board = number[][];

type Game = {
  gameId: number | null;
  playerId: number | null;
  board: number[][];
  PvAI: boolean | null;
  OnlinePvP: boolean | null;
  LocalPvP: boolean | null;
}

const Board: React.FC = () => {

  const yCoordinates = [20, 60, 100, 140, 180, 220];
  const [board, setBoard] = useState<Board>(Array.from({length: 7},()=>Array.from({length: 6}, () => 0)));
  const [turn, setTurn] = useState(1);
  const [PvAI, setPvAI] = useState<boolean>(false);
  const [OnlinePvP, setOnlinePvP] = useState<boolean>(false);
  const [LocalPvP, setLocalPvP] = useState<boolean>(false);

  const game: Game = {
    gameId: null,
    playerId: null,
    board: board,
    PvAI: PvAI,
    OnlinePvP: OnlinePvP,
    LocalPvP: LocalPvP,
  };

  const sendGame: any = async () => {
    if (game.PvAI === true) {
      const result = await fetch("OFFLINEGAME", {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json', // Indicates the content type
        },
        body: JSON.stringify(board),
      }).then(response => response.json().then((response => response)))
      return result;
    } 
    else if (game.OnlinePvP === true) {
    } else if (game.LocalPvP === true) {
    }
  }

  const handleCircleClick = (id: number) => {
    const newBoard = board.map(row  => [...row]);
    for (let i = 0; i < 7; i++) {
      if (board[id][i] === 0 && turn === 1) {
        newBoard[id][i] = 1;
        sendGame();
        setTurn(2);
        break;
      } else if (board[id][i] === 0 && turn === 2) {
        newBoard[id][i] = 2;
        sendGame();
        setTurn(1);
        break;
      }
    }
    setBoard(newBoard);
  };

  // FOR STARTING THE GAME OFFLINE 
  useEffect(() => {
    const response = fetch("/OFFLINEGAMESTART", {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json', // Indicates the content type
      },
      body: JSON.stringify(game),
    }).then(r => {
      console.log(r);
    })
  }, [LocalPvP])
  
  // FOR STARTING THE GAME ONLINE 
  useEffect(() => {
    const response = fetch("/ONLINEGAMESTART", {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json', // Indicates the content type
      },
      body: JSON.stringify(game),
    }).then(r => {
      console.log(r);
    })
  }, [OnlinePvP])

  // FOR STARTING THE GAME AGAINST AI 
  useEffect(() => {
    const response = fetch("/ONLINEGAMESTART", {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json', // Indicates the content type
      },
      body: JSON.stringify(game),
    }).then(r => {
      console.log(r);
    })
  }, [PvAI])

  return (
    <div style={{display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center'}}>
      <Title></Title>
      {(!PvAI && !OnlinePvP && !LocalPvP) ? (<Buttons setPvAI={setPvAI} setOnlinePvP={setOnlinePvP} setLocalPvP={setLocalPvP}></Buttons>) : null}
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

export default Board;
