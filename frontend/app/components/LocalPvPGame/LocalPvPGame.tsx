import './LocalPvPGame.css'

import { useEffect, useState } from 'react';
import Board from '../Board/Board';
import WinScreen from '../WinScreen/WinScreen';
import { URL } from '~/util/Url';

const LocalPvPGameBoard: React.FC = () => {


    const yCoordinates = [20, 60, 100, 140, 180, 220];
    const [board, setBoard] = useState<string[][]>(Array.from({ length: 7 }, () => Array.from({ length: 6 }, () => ' ')));
    const [turn, setTurn] = useState<number>(1);
    const [winner, setWinner] = useState<number>(-1);
    const [gotWinner, setGotWinner] = useState<boolean>(false);
    const [displayWinningScreen, setDisplayWinningScreen] = useState<boolean>(true);

    useEffect(() => {
        fetch(`${URL}/InitGame/LocalPvP`, {
            method: "GET",
            credentials: "include"
        }).then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
        });
        fetch(`${URL}/test`, {
            method: 'GET',
            credentials: 'include'  // Ensure cookies are sent with cross-origin requests
        });
    }, []);

    const sendGame = async (x: number, y: number) => {
        await fetch(`${URL}/game/LocalPvP`, {
            method: "POST",
            credentials: "include",
            headers: {
                'Content-Type': 'application/json', // Indicates the content type
            },
            body: JSON.stringify({
                col: x,
                row: y,
            })
        }).then(response => {
            if (!response.ok) {
                throw new Error("Network response was not ok " + response.statusText);
            }
            return response.json();
        }).then(response => {
            console.log(response);
            if (response.winner !== -1) {
                setGotWinner(true);
                setWinner(response.winner);
                console.log("GOT WINNER YAY");
            }
        });
        return true;
    }

    const handleCircleClick = (column: number) => {
        const newBoard = board.map(row => [...row]);
        for (let i = 0; i < 7; i++) {
            if (board[column][i] === ' ' && turn === 1 && gotWinner === false) {
                console.log("column:" + column + "row:" + i);
                sendGame(column, i).then(result => { if (result === false) { console.log(result); return; } });
                newBoard[column][i] = 'O';
                setTurn(2);
                break;
            } else if (board[column][i] === ' ' && turn === 2 && gotWinner === false) {
                console.log("column:" + column + "row:" + i);
                sendGame(column, i).then(result => { if (!result) { return; } });
                newBoard[column][i] = 'X';
                setTurn(1);
                break;
            }
        }
        setBoard(newBoard);
    };

    return (
        <div>
            {displayWinningScreen && gotWinner ? (
            <WinScreen message={"Player " + winner + " won"} onClose={() => setDisplayWinningScreen(false)} />
            ) : null}
        <Board styles="" handleCircleClick={handleCircleClick} yCoordinates={yCoordinates} board={board} />
        </div>
  );
}

export default LocalPvPGameBoard;
