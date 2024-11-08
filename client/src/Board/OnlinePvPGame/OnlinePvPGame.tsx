import './SVG.css';
import Column from '../Column/Column'

type props = {
  handleCircleClick: (columnIndex: number) => void;
  yCoordinates: number[];
  board: string[][];
}

const OnlinePvPGame: React.FC<props> = ({handleCircleClick, yCoordinates, board}) => {
  return (
    <svg
        className='Board'
        style={{ width: '100%', height: 'auto', maxWidth: '700px' }}
        viewBox="0 0 280 240"
        xmlns="http://www.w3.org/2000/svg">
        <rect width="280" height="240" fill="#3E3E3E" rx="10" />
        <g>
            <g onClick={() => handleCircleClick(0)}>
                <Column x={20} yCoordinates={yCoordinates} xCoordinates={0} board={board} />
            </g>
            <g onClick={() => handleCircleClick(1)}>
                <Column x={60} yCoordinates={yCoordinates} xCoordinates={1} board={board} />
            </g>
            <g onClick={() => handleCircleClick(2)}>
                <Column x={100} yCoordinates={yCoordinates} xCoordinates={2} board={board} />
            </g>
            <g onClick={() => handleCircleClick(3)}>
                <Column x={140} yCoordinates={yCoordinates} xCoordinates={3} board={board} />
            </g>
            <g onClick={() => handleCircleClick(4)}>
                <Column x={180} yCoordinates={yCoordinates} xCoordinates={4} board={board} />
            </g>
            <g onClick={() => handleCircleClick(5)}>
                <Column x={220} yCoordinates={yCoordinates} xCoordinates={5} board={board} />
            </g>
            <g onClick={() => handleCircleClick(6)}>
                <Column x={260} yCoordinates={yCoordinates} xCoordinates={6} board={board} />
            </g>
        </g>
    </svg>
  );
}

export default OnlinePvPGame;
