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
            else if(level==1) return strategyHard(board, game);
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
}