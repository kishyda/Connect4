import React, { useEffect, useState } from 'react';
import Column from './Column/Column';
import Buttons from './Buttons/Buttons';
import Title from './Title/Title';
import SVG from './SVG/SVG';

import './Board.css'
import LocalPvPGameBoard from './LocalPvPGame/LocalPvPGame';
import OfflineAIGame from './PvAIGame/PvAIGame'; 
import OnlinePvPGame from './OnlinePvPGame/OnlinePvPGame';

type sessionID = {
  sessionID: string
}

const Board: React.FC<sessionID> = ( {sessionID} ) => {

    const [gameMode, setGameMode] = useState<string>("");
    const [isLocalPvPGame, setLocalPvPGame] = useState<boolean>(false);
    const [isPvAIGame, setPvAIGame] = useState<boolean>(false);
    const [isOnlinePvPGame, setOnlinePvPGame] = useState<boolean>(false);

    useEffect(() => {
        if(gameMode === "OnlinePvP") {
            setOnlinePvPGame(true);
            setLocalPvPGame(false);
            setPvAIGame(false);
        } else if (gameMode === "LocalPvP") {
            setLocalPvPGame(true);
            setOnlinePvPGame(false);
            setPvAIGame(false);
        } else if (gameMode === "PvAI") {
            setPvAIGame(true);
            setLocalPvPGame(false);
            setOnlinePvPGame(false);
        }
    }, [gameMode]);

    return (
        <div style={{ display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center' }}>
            <div>
                <Buttons setGameMode={setGameMode}></Buttons>
            </div>
            {isLocalPvPGame && 
            <div>
                <LocalPvPGameBoard sessionID={sessionID}></LocalPvPGameBoard>
            </div>}
            {isOnlinePvPGame && 
            <div>
                <OnlinePvPGame sessionID={sessionID}></OnlinePvPGame>
            </div>}
            {isPvAIGame && 
            <div>
                <OfflineAIGame sessionID={sessionID}></OfflineAIGame>
            </div>}
        </div>
    );
};

export default Board;
