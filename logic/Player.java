package logic;

import java.util.*;

public class Player
{
    char move;
    boolean isWinner;
    boolean isComputer;
    Board board;
    Game game;
    public Player(boolean isComputer, Board board, Game game) {
        isWinner = false;
        this.isComputer = isComputer;
        this.board = board;
        this.game = game;
    }
    
    public Move getMove(boolean isComputer, Board board, Game game, int level) {
        if(isComputer == false) {
            System.out.print("Please enter your move [row, column]");
            Scanner sc = new Scanner(System.in);
            int row = sc.nextInt();
		    int col = sc.nextInt();
            Move move = new Move(row, col);
            return move;
        }
        else {
            if(level == 0) return strategyEasy(game);
            else if(level==1) return strategyHardFutureLookAheadRecursive(board, game, new Move(-1,-1), 1, game.legalMoves);
            else return strategyAI(board, game);
        }
    }
    
    private Move strategyEasy(Game game) {
        Random rand = new Random();
        int selection;
        do {
            selection = rand.nextInt(game.legalMoves.size()); // there is always 6 moves available until end game
        } while (game.legalMoves.get(selection).row == -1);
        return game.legalMoves.get(selection);
    }
    
    private Move strategyAI(Board board, Game game) {
        /*
        if(!q_table.contains(board)):
            q_table[board] = np.zeros(len(env.actionSpace))
        actionIndex = np.argmax(q_table[board])
        if (game.legalMoves[actionIndex].row == -1):
            return strategyEasy(game); //cannot find good move
        action = game.legalMoves[actionIndex]
        return action;*/
        return strategyEasy(game); //dummy return until qtable is implemented
    }
    
    private Move strategyHard(Board board, Game game) {
        for(int windowLen=4; windowLen>=2; windowLen--) {
            for(Move move : game.legalMoves) {
                if(move.row != -1) { // n/a spot
                    if(game.checkHorizontal(board, move, windowLen, true) || game.checkVertical(board, move, windowLen, true) || 
                    game.checkDiagonal(board, move, windowLen, true, false) || game.checkDiagonal(board, move, windowLen, true, true)) {
                        return move;
                    }
                }
            }
        }
        return strategyEasy(game); //cannot find good move
    }
    
    private Move strategyHardFutureLookAheadRecursive(Board board, Game game, Move prevMove, int lookAhead, List<Move> legalMoves) {
        Board copyBoard = new Board();
        copyBoard.copyBoard(board.getBoard());
        List<Move> copyLegalMoves = new ArrayList<Move>();
        for(Move move : legalMoves) copyLegalMoves.add(move);
        
        lookAhead++; //counter for stopping after looking ahead 3 moves (3 is hardcoded as seen below)
        
        for(int windowLen=4; windowLen>=2; windowLen--) {
            for(Move counterMove : copyLegalMoves) {
                if(counterMove.row != -1 && !(counterMove.row == prevMove.row && counterMove.col == prevMove.col)) { // n/a spot
                    if(game.checkHorizontal(copyBoard, counterMove, windowLen, true) || game.checkVertical(copyBoard, counterMove, windowLen, true) || 
                        game.checkDiagonal(copyBoard, counterMove, windowLen, true, false) || game.checkDiagonal(copyBoard, counterMove, windowLen, true, true)) {
                        
                        if(lookAhead == 3) return prevMove;
                        
                        //Sets the stone and updates legal moves left on 'pretend' copies of board matrix and legal moves list
                        copyBoard.setStone(counterMove, (game.activePlayer+1)%2);
                        if(counterMove.row != 0) copyLegalMoves.set(counterMove.col, new Move(counterMove.row-1, counterMove.col));
		                else copyLegalMoves.set(counterMove.col, new Move(-1, -1));
                        
                        return strategyHardFutureLookAheadRecursive(copyBoard, game, counterMove, lookAhead, copyLegalMoves);
                        }
                } }
            }
        
        if(prevMove.row != -1 && prevMove.col != -1) return prevMove; //recursive func starts with dummy previous move (-1, -1)
        else return strategyEasy(game); //cannot find good move
    }
}
