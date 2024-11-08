import React, { useContext, useState } from 'react';
import logo from './logo.svg';
import './App.css';
import Board from './Board/Board'

import Login from './Login/Login'
import TopBar from './TopBar/TopBar'
import { v4 as uuidv4 } from 'uuid';

type UserData = {
  userid: string,
}

function App() {

  const sessionID: string = uuidv4();
  const [userId, setUserId] = useState('');
  const [loggedIn, setLogInStatus] = useState(false);

  return (
    <div className="App">
          <pre className='title-ascii'>{`
 $$$$$$\                                                      $$\\           $$\\   $$\\ 
$$  __$$\\                                                     $$ |          $$ |  $$ |
$$ /  \\__| $$$$$$\\  $$$$$$$\\  $$$$$$$\\   $$$$$$\\   $$$$$$$\\ $$$$$$\\         $$ |  $$ |
$$ |      $$  __$$\\ $$  __$$\\ $$  __$$\\ $$  __$$\\ $$  _____|\\_$$  _|        $$$$$$$$ |
$$ |      $$ /  $$ |$$ |  $$ |$$ |  $$ |$$$$$$$$ |$$ /        $$ |          \\_____$$ |
$$ |  $$\\ $$ |  $$ |$$ |  $$ |$$ |  $$ |$$   ____|$$ |        $$ |$$\\             $$ |
\\$$$$$$  |\\$$$$$$  |$$ |  $$ |$$ |  $$ |\\$$$$$$$\\ \\$$$$$$$\\   \\$$$$  |            $$ |
 \\______/  \\______/ \\__|  \\__|\\__|  \\__| \\_______| \\_______|   \\____/             \\__|
      `}      </pre>

      {loggedIn ? (<Board sessionID={sessionID}></Board>) : (<Login loggedIn={loggedIn} setLoggedIn={setLogInStatus} setUserId={setUserId}></Login>)}
    </div>
  );
}

export default App;

