import { useEffect, useState } from 'react';
import ButtonSet from './ChooseDifficulty';
import Board from '../Board/Board';
import WinScreen from '../WinScreen/WinScreen';
import { URL } from '~/util/Url';

const AIGame: React.FC = () => {

    const yCoordinates = [20, 60, 100, 140, 180, 220];
    const [board, setBoard] = useState<string[][]>(Array.from({ length: 7 }, () => Array.from({ length: 6 }, () => ' ')));
    const [gotWinner, setGotWinner] = useState<boolean>(false);
    const [displayWinningScreen, setDisplayWinningScreen] = useState<boolean>(true);
    const [winner, setWinner] = useState<number>(-1);
    const [handleCircleClickCalled, setHandleCircleCLickCalled] = useState<number>(1);
    const [readyToTakeCircleClick, setReadyToTakeCircleClick] = useState<boolean>(true);

    const [difficulty, setDifficulty] = useState<number>(-1);
    const [difficultySet, setDifficultySet] = useState<boolean>(false);

    useEffect(() => {
        fetch(`${URL}/InitGame/PvAI`, {
            method: "POST",
            credentials: "include",
            headers: {
              'Content-Type': 'application/json', // Specifies the content type
            },
            body: JSON.stringify({
              difficulty: difficulty,
            }),
        }).then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
        });
    }, [difficulty]);

    const sendGame = async (x: number, y: number) => {
        await fetch(`${URL}/game/PvAI/PlayerMove`, {
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
        if (readyToTakeCircleClick === true) {
            return
        }
        await new Promise(resolve => setTimeout(resolve, 1000));  // Delay of 500ms
        await fetch(`${URL}/game/PvAI/CPUMove`, {
            method: "GET",
            credentials: "include",
        }).then(response => {
            if (!response.ok) {
                console.log(response);
                throw new Error("Network response alskdfjaksfjlk jwas not ok " + response.statusText);
            }
            return response.json();
        }).then(response => {
            console.log(response);
            if (response.winner !== -1) {
                const newBoard = board.map(row => [...row]);
                console.log("THIS IS GET AI MOVE", "column: ", response.col, "row: ", response.col, response.winner);
                newBoard[response.col][5 - response.row] = 'X';
                setBoard(newBoard);;
                setGotWinner(true);
                setWinner(response.winner);
                console.log("GOT WINNER YAY");
                setReadyToTakeCircleClick(true);
                return;
            }
            if (response.col === -1 || response.row === -1) {
                return;
            }
            const newBoard = board.map(row => [...row]);
            console.log("THIS IS GET AI MOVE", "column: ", response.col, "row: ", response.row, response.winner);
            newBoard[response.col][5 - response.row] = 'X';
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
    }, [handleCircleClickCalled, readyToTakeCircleClick, board]);  // Only run when `board` or `gotWinner` changes

    return (
        <div>
            {displayWinningScreen && gotWinner ? (
            <WinScreen message={"Player " + winner + " won"} onClose={() => setDisplayWinningScreen(false)} />
            ) : null}
            {!difficultySet ? (
            <ButtonSet setDifficulty={setDifficulty} setDifficultySet={setDifficultySet}></ButtonSet>) : null}
            <Board styles="" handleCircleClick={handleCircleClick} yCoordinates={yCoordinates} board={board} />
        </div>
  );
}

export default AIGame;
