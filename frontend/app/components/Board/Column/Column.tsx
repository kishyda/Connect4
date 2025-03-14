import React from 'react';

import './Column.css';

type CircleColumnProps = {
  x: number;
  yCoordinates: number[];
  xCoordinates: number;
  board: string[][];
};

const fillColor = (board: string[][], x: number, y: number): string => {
  if (board[x][y] === ' ') {
    return "#D1D1D1";
  } else if (board[x][y] === 'O') {
    return "red";
  } else {
    return "blue";
  }

}

const CircleColumn: React.FC<CircleColumnProps> = ({ x, yCoordinates, xCoordinates, board }) => (
  <>
    {yCoordinates.map((y, index) => (
      <circle className={'circle'} key={index} cx={x} cy={y} r="15" fill={fillColor(board, xCoordinates, 5-index)} />
    ))}
  </>
);

export default CircleColumn
