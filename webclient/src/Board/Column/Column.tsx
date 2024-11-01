import React from 'react';

type CircleColumnProps = {
  x: number;
  yCoordinates: number[];
  xCoordinates: number;
  board: number[][];
};

const fillColor = (board: number[][], x: number, y: number): string => {
  if (board[x][y] === 0) {
    return "white";
  } else if (board[x][y] === 1) {
    return "red";
  } else {
    return "blue";
  }

}

const CircleColumn: React.FC<CircleColumnProps> = ({ x, yCoordinates, xCoordinates, board }) => (
  <>
    {yCoordinates.map((y, index) => (
      <circle key={index} cx={x} cy={y} r="15" fill={fillColor(board, xCoordinates, 5-index)} />
    ))}
  </>
);

export default CircleColumn;
