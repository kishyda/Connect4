import Board from '../Board/Board';
import { useState, useEffect, useRef } from 'react';
import WinScreen from '../WinScreen/WinScreen';
import PartyForm from './Party';

type props = {
    sessionID: string
}

const OnlinePvPGame: React.FC<props> = ({sessionID}) => {

    const hasMounted = useRef(false);

    const [name, setName] = useState('');
    const [partyCode, setPartyCode] = useState('');
    const [gotGameData, setGotGameData] = useState(false);
    const [player1, setPlayer1] = useState(true);

    const createParty = () => {
        fetch('/InitGame/CreateParty', {
            method: "POST",
            headers: {
              'Content-Type': 'application/json', // Specifies the content type
            },
            body: JSON.stringify({
              sessionID: sessionID,
              partyCode: partyCode,
            }),
        }).then(response => {
            if (!response.ok) {
                console.log("there's an issue guys!");
                return;
            }
        }).catch(error => {
            console.error('Error during fetch:', error); 
        });
        console.log("CREATE PARTY", partyCode);
        setGotGameData(true);
    };

    const joinParty = () => {
        fetch('/InitGame/JoinParty', {
            method: "POST",
            headers: {
              'Content-Type': 'application/json', // Specifies the content type
            },
            body: JSON.stringify({
              sessionID: sessionID,
              partyCode: partyCode,
            }),
        }).then(response => {
            if (!response.ok) {
                console.log("there's an issue guys!");
                return;
            }
        }).catch(error => {
            console.error('Error during fetch:', error); 
        });
        setReadyToTakeCircleClick(false);
        setGetOpponentMove(getOpponentMove + 1);
        setPlayer1(false);
        console.log("JOIN PARTY", partyCode);
        setGotGameData(true);
    };

    const yCoordinates = [20, 60, 100, 140, 180, 220];
    const [board, setBoard] = useState<string[][]>(Array.from({ length: 7 }, () => Array.from({ length: 6 }, () => ' ')));
    const [displayWinningScreen, setDisplayWinningScreen] = useState(false);
    const [gotWinner, setGotWinner] = useState(false);
    const [winner, setWinner] = useState(-1);
    const [readyToTakeCircleClick, setReadyToTakeCircleClick] = useState<boolean>(true);
    const [getOpponentMove, setGetOpponentMove] = useState<number>(1);

    useEffect(() => {
        if (hasMounted.current) {
            getMove();
        } else {
            hasMounted.current = true;
        }
    }, [getOpponentMove])

    const getMove = async () => {
        if (gotGameData === false) {
            return;
        }
        console.log("GET MOVE", partyCode);
        await fetch(`/game/OnlinePvP/GetMove`, {
            method: "POST",
            headers: {
                'Content-Type': 'application/json', // Indicates the content type
            },
            body: JSON.stringify({
                sessionID: sessionID, 
                partyCode: partyCode,
            })
        }).then(response => {
            if (!response.ok) {
                console.log("there's an issue guys!");
            }
            return response.json(); 
        }).then(data => {
            const newBoard = board.map(row => [...row]);
            if (data.winner !== -1) {
                newBoard[data.col][5 - data.row] = 'O';
                setBoard(newBoard);
                setGotWinner(true);
                setDisplayWinningScreen(true);
                setWinner(data.winner);
                console.log("GOT WINNER YAY");
            }
            newBoard[data.col][5 - data.row] = 'O';
            setBoard(newBoard);
            console.log(data);
            return true;
        });
        setReadyToTakeCircleClick(true);
        getWinner().then();
        return true;
    }

    const sendMove = async (column: number, row: number) => {
        console.log("SEND MOVE PACKAGE: ", column, row, sessionID, partyCode);
        await fetch(`/game/OnlinePvP/PostMove`, {
            method: "POST",
            headers: {
                'Content-Type': 'application/json', // Indicates the content type
            },
            body: JSON.stringify({
                col: column,
                row: row,
                sessionID: sessionID, 
                partyCode: partyCode,
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
                setDisplayWinningScreen(true);
                console.log("GOT WINNER YAY");
            }
        });
        getWinner().then();
        return true;
    }

    const getWinner = async () => {
        fetch('/game/OnlinePvP/CheckWin', {
            method: "POST",
            headers: {
                'Content-Type': 'application/json', // Indicates the content type
            },
            body: JSON.stringify({
                sessionID: sessionID,
                partyCode: partyCode,
            })
        }).then(response => {
            return response.json();
        }).then(response => {
            if (response.winner !== -1) {
                setGotWinner(true);
                setWinner(response.winner);
                setDisplayWinningScreen(true);
                console.log("GOT WINNER YAY");
            }
        })
    }

    const handleCircleClick = (column: number) => {
        const newBoard = board.map(row => [...row]);
        for (let i = 0; i < 6; i++) {
            if (board[column][i] === ' ' && gotWinner === false && readyToTakeCircleClick) {
                console.log("column:" + column + "row:" + i);
                sendMove(column, 5 - i).then(_ => {});
                newBoard[column][i] = 'X';
                setBoard(newBoard);
                setReadyToTakeCircleClick(false);
                setGetOpponentMove(getOpponentMove + 1);
                break;
            } 
        }
    };

    return (
      <div>
            {displayWinningScreen && gotWinner ? (
            <WinScreen message={"Player " + winner + " won"} onClose={() => setDisplayWinningScreen(false)} />
            ) : null}
            {!gotGameData ? (
            <PartyForm name={name} partyCode={partyCode} setName={setName} setPartyCode={setPartyCode} joinParty={joinParty} createParty={createParty} ></PartyForm>) : null}
            <Board styles="" handleCircleClick={handleCircleClick} yCoordinates={yCoordinates} board={board} />
        </div>
    );
}

export default OnlinePvPGame;
