import React, { Dispatch, SetStateAction } from 'react';
import './ChooseDifficulty.css';

type prop = {
    setDifficulty: Dispatch<SetStateAction<number>>,
    setDifficultySet: Dispatch<SetStateAction<boolean>>,
};

const ButtonSet: React.FC<prop> = ({setDifficulty, setDifficultySet}) => {

  return (
    <div className="button-container">
      <button className="button" onClick={() => {setDifficulty(0); setDifficultySet(true);}}>
        &lt;Easy bot&gt;
      </button>
      <button className="button" onClick={() => {setDifficulty(1); setDifficultySet(true);}}>
        &lt;Hard Bot&gt;
      </button>
      <button className="button" onClick={() => {setDifficulty(1); setDifficultySet(true);}}>
        &lt;ML Bot&gt;
      </button>
    </div>
  );
};

export default ButtonSet;

