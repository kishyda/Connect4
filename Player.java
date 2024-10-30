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
            else return strategyHard(board, game);
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
    
    private Move strategyHard(Board board, Game game) {
        for(Move move : game.legalMoves) {
            if(move.row != -1) { // n/a spot
                for(int windowLen=4; windowLen>=2; windowLen--) {
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