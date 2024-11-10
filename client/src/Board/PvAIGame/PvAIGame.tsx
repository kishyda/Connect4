import Column from '../Column/Column'
import { useEffect, useState } from 'react';
import WinScreen from '../WinScreen/WinScreen';
import SVG from '../SVG/SVG';

type sessionId = {
    sessionID: string;
}

const OfflineAIGame: React.FC<sessionId> = ({sessionID}) => {

    const yCoordinates = [20, 60, 100, 140, 180, 220];
    const [board, setBoard] = useState<string[][]>(Array.from({ length: 7 }, () => Array.from({ length: 6 }, () => ' ')));
    const [gotWinner, setGotWinner] = useState<boolean>(false);
    const [displayWinningScreen, setDisplayWinningScreen] = useState<boolean>(true);
    const [winner, setWinner] = useState<number>(-1);
    const [handleCircleClickCalled, setHandleCircleCLickCalled] = useState<number>(1);
    const [readyToTakeCircleClick, setReadyToTakeCircleClick] = useState<boolean>(true);

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

    const sendGame = async (x: number, y: number) => {
        await fetch(`http://localhost:8080/game/PvAI/PlayerMove`, {
            method: "POST",
            headers: {
                'Content-Type': 'application/json', // Indicates the content type
            },
            body: JSON.stringify({
                x: x,
                y: y,
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

    const getAIMove = async () => {
        await fetch(`http://localhost:8080/game/PvAI/CPUMove`, {
            method: "POST",
            headers: {
                'Content-Type': 'application/json', // Indicates the content type
            },
            body: JSON.stringify({
                sessionID: sessionID, 
            })
        }).then(response => {
            if (!response.ok) {
                throw new Error("Network response alskdfjaksfjlk jwas not ok " + response.statusText);
            }
            return response.json();
        }).then(response => {
            console.log(response);
            if (response.winner !== -1) {
                const newBoard = board.map(row => [...row]);
                console.log("THIS IS GET AI MOVE", "column: ", response.x, "row: ", response.y, response.winner);
                newBoard[response.x][5 - response.y] = 'X';
                setBoard(newBoard);;
                setGotWinner(true);
                setWinner(response.winner);
                console.log("GOT WINNER YAY");
                setReadyToTakeCircleClick(true);
                return;
            }
            if (response.x === -1 || response.y === -1) {
                return;
            }
            const newBoard = board.map(row => [...row]);
            console.log("THIS IS GET AI MOVE", "column: ", response.x, "row: ", response.y, response.winner);
            newBoard[response.x][5 - response.y] = 'X';
            setBoard(newBoard);
            setReadyToTakeCircleClick(true);
        });
        setReadyToTakeCircleClick(true);
        return true;
    }

    const handleCircleClick = (column: number) => {
        const newBoard = board.map(row => [...row]);
        for (let i = 0; i < 6; i++) {
            if (board[column][i] === ' ' && gotWinner === false && readyToTakeCircleClick) {
                console.log("column:" + column + "row:" + i);
                sendGame(column, i).then(_ => {});
                newBoard[column][i] = 'O';
                setBoard(newBoard);
                setHandleCircleCLickCalled(handleCircleClickCalled + 1);
                setReadyToTakeCircleClick(false);
                break;
            } 
        }
    };
    
    useEffect(() => {
        if (!gotWinner && board) {
            getAIMove().then();  // Trigger AI move after board update
        }
    }, [handleCircleClickCalled]);  // Only run when `board` or `gotWinner` changes

    return (
        <div>
            {displayWinningScreen && gotWinner ? (
            <WinScreen message={"Player " + winner + " won"} onClose={() => setDisplayWinningScreen(false)} />
            ) : null}
        <SVG handleCircleClick={handleCircleClick} yCoordinates={yCoordinates} board={board} />
        </div>
  );
}

export default OfflineAIGame;
