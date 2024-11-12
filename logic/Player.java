package logic;
import java.util.Scanner;
import java.util.Random;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.BufferedReader;
import java.util.Map;


public class Player
{
    char move;
    boolean isWinner;
    boolean isComputer;
    Board board;
    Game game;
    Map<Character[][], double[]> q_table = new HashMap<>();
    public Player(boolean isComputer, Board board, Game game) {
        isWinner = false;
        this.isComputer = isComputer;
        this.board = board;
        this.game = game;
        if(game.level == 2) {
        q_table = readQTableDictionary();
        }
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
    

    public static Map<Character[][], double[]> readQTableDictionary() {
    	String filePath = "qtable.txt";
        Map<Character[][], double[]> dictionary = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Remove any extra whitespace and handle line continuations
                line = line.trim();

                // If line contains a grid key, parse it
                if (line.startsWith("[[") && line.endsWith("]")) {
                    Character[][] grid = parseGrid(line);
                    line = br.readLine().trim(); // Move to the next line for the values

                    if (line != null && line.startsWith("[")) {
                        double[] values = parseValues(line);
                        dictionary.put(grid, values);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dictionary;
    }

    // Parses a grid from a string in the format [[_, _, _, _, _, _, _], ...]
    public static Character[][] parseGrid(String gridString) {
        gridString = gridString.replaceAll("[\\[\\]]", ""); // Remove brackets
        String[] rows = gridString.split("],\\["); // Split by rows

        List<Character[]> gridList = new ArrayList<>();
        for (String row : rows) {
            String[] elements = row.split(",\\s*"); // Split each row by commas
            Character[] charRow = new Character[elements.length];
            for (int i = 0; i < elements.length; i++) {
                charRow[i] = elements[i].charAt(0); // Convert string to Character
            }
            gridList.add(charRow);
        }

        return gridList.toArray(new Character[0][0]);
    }

    // Parses a line of double values in the format [value1, value2, ...]
    public static double[] parseValues(String valuesString) {
        valuesString = valuesString.replaceAll("[\\[\\]]", ""); // Remove brackets
        String[] values = valuesString.split(",\\s*"); // Split by commas
        double[] result = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = Double.parseDouble(values[i]); // Convert each to double
        }
        return result;
    }
    
    private Move strategyAI(Board board, Game game) {
        if (!(q_table.containsKey(board.getBoard()))) q_table.put(board.getBoard(), new double[game.legalMoves.size()]);
        int actionIndex = 0;
        double q_value = 0.0;
        for(int i=0; i<q_table.get(board.getBoard()).length; i++) { //find argmax of qtable value
            if(q_table.get(board.getBoard())[i] > q_value) actionIndex = i;
        }
        if (game.legalMoves.get(actionIndex).row == -1) return strategyEasy(game); //cannot find good move
        Move action = game.legalMoves.get(actionIndex);
        return action;
        //return strategyEasy(game); //dummy return until qtable is implemented
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
