import './SVG.css';
import Column from '../Column/Column'
import SVG from '../SVG/SVG';
import { useState, useEffect } from 'react';
import WinScreen from '../WinScreen/WinScreen';

type props = {
    sessionID: string
}

const OnlinePvPGame: React.FC<props> = ({sessionID}) => {

    const yCoordinates = [20, 60, 100, 140, 180, 220];
    const [board, setBoard] = useState<string[][]>(Array.from({ length: 7 }, () => Array.from({ length: 6 }, () => ' ')));
    const [displayWinningScreen, setDisplayWinningScreen] = useState(false);
    const [gotWinner, setGotWinner] = useState(false);
    const [winner, setWinner] = useState(-1);
    const [handleCircleClickCalled, setHandleCircleCLickCalled] = useState<number>(1);
    const [readyToTakeCircleClick, setReadyToTakeCircleClick] = useState<boolean>(true);
    const [getOpponentMove, setGetOpponentMove] = useState<boolean>(true);

    useEffect(() => {
            // Ensure sessionID is available before making the request
            if (!sessionID) {
              console.error('Session ID is not available.');
              return;
            }

            fetch('http://localhost:8080/InitGame/PvAI', {
                method: "POST",
                headers: {
                  'Content-Type': 'application/json', // Specifies the content type
                },
                body: JSON.stringify({
                  sessionID: sessionID,
                }),
            }).then(response => {
                if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json(); 
            }).then(data => {
                console.log('Response Data:', data);
            }).catch(error => {
                console.error('Error during fetch:', error); 
            });
    }, []);

    useEffect(() => {
    }, [getOpponentMove])

    const getMove = async () => {
        await fetch(`http://localhost:8080/game/OnlinePvP/GetMove`, {
            method: "POST",
            headers: {
                'Content-Type': 'application/json', // Indicates the content type
            },
            body: JSON.stringify({
                sessionID: sessionID, 
            })
        });
    }

    const sendMove = async (column: number, row: number) => {
        await fetch(`http://localhost:8080/game/OnlinePvP/PostMove`, {
            method: "POST",
            headers: {
                'Content-Type': 'application/json', // Indicates the content type
            },
            body: JSON.stringify({
                col: column,
                row: row,
                sessionID: sessionID, 
            })
        }).then(response => {
            if (!response.ok) {
                throw new Error("Network responseajlsdkfajskdfjaslkdfjaksldfjlk was not ok " + response.statusText);
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
        for (let i = 0; i < 6; i++) {
            if (board[column][i] === ' ' && gotWinner === false && readyToTakeCircleClick) {
                console.log("column:" + column + "row:" + i);
                sendMove(column, i).then(_ => {});
                newBoard[column][i] = 'O';
                setBoard(newBoard);
                setHandleCircleCLickCalled(handleCircleClickCalled + 1);
                setReadyToTakeCircleClick(false);
                break;
            } 
        }
    };

    return (
      <div>
            {displayWinningScreen && gotWinner ? (
            <WinScreen message={"Player " + winner + " won"} onClose={() => setDisplayWinningScreen(false)} />
            ) : null}
            <SVG handleCircleClick={handleCircleClick} yCoordinates={yCoordinates} board={board} />
        </div>
    );
}

export default OnlinePvPGame;
