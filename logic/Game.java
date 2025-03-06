package logic;

import java.util.Random;
import java.lang.Math;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {
    Board board = new Board();
    Player[] players = new Player[2];
    public int activePlayer;
    boolean p2p; // player to player or player vs computer
    List<Move> legalMoves = new ArrayList<Move>();
    public int level;

    private int winner;
    private boolean gameOver;

    public Game(boolean p2p, int level) {
        this.p2p = p2p;
        this.level = level;
        initGame();
        // runGameLoop();
    }

    public int getWinner() {
        return winner;
    }

    public int getActivePlayer() { // whose turn is it
        return activePlayer;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setP2P(boolean p2p) {
        this.p2p = p2p;
    }

    public boolean getGameOver() {
        return gameOver;
    }

    void initGame() {
        winner = -1;
        gameOver = false;
        activePlayer = 0;
        players[0] = new Player(false, board, this);
        boolean isComputer = p2p ? false : true;
        players[1] = new Player(isComputer, board, this);
        Collections.addAll(legalMoves, new Move(5, 0), new Move(5, 1), new Move(5, 2), new Move(5, 3), new Move(5, 4),
                new Move(5, 5), new Move(5, 6));
    }

    private Move gameTurn(int activePlayer) {
        boolean isComputer = p2p || activePlayer % 2 == 0 ? false : true;
        Move move = players[activePlayer].getMove(isComputer, board, this, level);
        while (!checkLegalMove(board, move)) {
            move = players[activePlayer].getMove(isComputer, board, this, level);
        }
        board.setStone(move, activePlayer);
        if (move.row != 0)
            legalMoves.set(move.col, new Move(move.row - 1, move.col));
        else {
            legalMoves.set(move.col, new Move(-1, -1)); // spot not available anymore
        }
        return move;
    }

    public boolean checkHorizontal(Board board, Move move, int windowLen, boolean testFlag) {
        boolean horizontalCheck = false;
        int row = move.row; // only check the row of the last move
        for (int j = Math.max(0, move.col - (windowLen - 1)); j <= Math.min(move.col,
                board.N_OF_COLS - windowLen); j++) {
            if (j + windowLen <= board.N_OF_COLS) {
                Character[] arr = Arrays.copyOfRange(board.getBoard()[row], j, j + windowLen);
                List<Character> consecutiveStones = new ArrayList<Character>();
                Collections.addAll(consecutiveStones, arr);
                if (consecutiveStones.size() == windowLen)
                    horizontalCheck = checkIfConnected(consecutiveStones, windowLen, testFlag);
                if (horizontalCheck == true)
                    return horizontalCheck;
            }
        }
        return horizontalCheck;
    }

    public boolean checkVertical(Board board, Move move, int windowLen, boolean testFlag) {
        boolean verticalCheck = false;
        List<Character> consecutiveStones = new ArrayList<Character>();
        int col = move.col; // only check the col of the last move
        for (int i = Math.max(0, move.row - (windowLen - 1)); i <= Math.min(move.row,
                board.N_OF_ROWS - windowLen); i++) {
            for (int row = 0; row < windowLen; row++) { // copyOfRange does not allow slicing along 2nd dim
                consecutiveStones.add(board.getBoard()[row + i][col]);
            }
            if (consecutiveStones.size() == windowLen)
                verticalCheck = checkIfConnected(consecutiveStones, windowLen, testFlag);
            if (verticalCheck == true)
                return verticalCheck;
            consecutiveStones.clear();
        }
        return verticalCheck;
    }

    public boolean checkDiagonal(Board board, Move move, int windowLen, boolean testFlag, boolean reverseFlag) {
        boolean diagonalCheck = false;

        // Right Diagonal (bottom-left to top-right)
        if (Math.abs(move.row - move.col) <= 3 && !reverseFlag) { // otherwise right diagonal does not exist
            for (int i = Math.max(0, move.row - windowLen + 1); i <= move.row; i++) {
                for (int j = Math.max(0, move.col - windowLen + 1); j <= move.col; j++) {
                    List<Character> consecutiveStones = new ArrayList<>();

                    // Collect stones along the bottom-left to top-right diagonal
                    for (int k = 0; k < windowLen; k++) {
                        if (i + k < board.N_OF_ROWS && j + k < board.N_OF_COLS) {
                            consecutiveStones.add(board.getBoard()[i + k][j + k]);
                        }
                    }

                    if (consecutiveStones.size() == windowLen)
                        diagonalCheck = checkIfConnected(consecutiveStones, windowLen, testFlag);
                    if (diagonalCheck)
                        return true;
                }
            }
        }

        // Left Diagonal (bottom-right to top-left)
        if (move.row + move.col <= 8 && move.row + move.col > 2 && reverseFlag) { // otherwise left diagonal does not
                                                                                  // exist
            for (int i = Math.max(0, move.row - windowLen + 1); i <= move.row; i++) {
                for (int j = Math.min(board.N_OF_COLS - 1, move.col + windowLen - 1); j >= move.col; j--) {
                    List<Character> consecutiveStones = new ArrayList<>();

                    // Collect stones along the bottom-right to top-left diagonal
                    for (int k = 0; k < windowLen; k++) {
                        if (i + k < board.N_OF_ROWS && j - k >= 0) {
                            consecutiveStones.add(board.getBoard()[i + k][j - k]);
                        }
                    }

                    if (consecutiveStones.size() == windowLen)
                        diagonalCheck = checkIfConnected(consecutiveStones, windowLen, testFlag);
                    if (diagonalCheck)
                        return true;
                }
            }
        }

        return diagonalCheck;
    }

    public boolean checkLegalMove(Board board, Move move) {
        // carry down in gravity
        while (move.row < board.N_OF_ROWS - 1 && board.getBoard()[move.row + 1][move.col] == '_')
            move.row++;

        if (board.getBoard()[move.row][move.col] != '_' ||
        // || (move.row < board.N_OF_ROWS-1 && board.board[move.row+1][move.col] == '_')
        // ||
        // move.row >= board.N_OF_ROWS ||
                move.col >= board.N_OF_COLS) {
            return false;
        }
        return true;
    }

    public void runGameLoop() {
        Move move;
        while (gameOver != true) {
            board.printBoard();
            move = gameTurn(activePlayer);
            if (checkIfWin(move) == true) {
                System.out.println(getWinner());
                break;
            }
            activePlayer = (activePlayer + 1) % 2;
        }
    }

    public void step(Move move, int activePlayer) { // Human player, move already checked for validity
        board.setStone(move, activePlayer);
        // update legal moves list
        if (move.row != 0)
            legalMoves.set(move.col, new Move(move.row - 1, move.col));
        else
            legalMoves.set(move.col, new Move(-1, -1)); // spot not available anymore
        checkIfWin(move); // updates winner and gameOver variables
    }

    public Move step(int activePlayer) { // CPU player, move will be decided automatically and not inputted
        Move move = players[activePlayer].getMove(true, board, this, level);
        while (!checkLegalMove(board, move)) {
            move = players[activePlayer].getMove(true, board, this, level);
        }
        board.setStone(move, activePlayer);
        // update legal moves list
        if (move.row != 0)
            legalMoves.set(move.col, new Move(move.row - 1, move.col));
        else
            legalMoves.set(move.col, new Move(-1, -1)); // spot not available anymore
        checkIfWin(move); // updates winner and gameOver variables
        return move;
    }

    private boolean checkIfConnected(List<Character> consecutiveStones, int windowLen, boolean testFlag) {
        int left = 0;
        int sum = 0;
        int prevSum = 0;
        // left < right for 2 pointer algorithm
        while (left < windowLen && !(Math.abs(sum) < Math.abs(prevSum))) { // 4 for Connect 4
            prevSum = sum;
            if (consecutiveStones.get(left) == 'X')
                sum++;
            else if (consecutiveStones.get(left) == 'O')
                sum--;
            left++;
        }
        if (testFlag) {
            // testFlag=true, computer is testing a good strategy move hypothetically
            int blanks = 0;
            for (Character elem : consecutiveStones)
                if (elem == '_')
                    blanks++;
            // if list is full with same elements with a single blank spot, fill that spot
            if ((blanks == 1) && (sum == windowLen - 1 || sum == -1 * (windowLen - 1)))
                return true;
        }
        if (sum == windowLen || sum == -1 * windowLen)
            return true;
        else
            return false;
    }

    private boolean checkIfWin(Move move) {
        if (checkHorizontal(board, move, 4, false) == true) {
            winner = activePlayer;
            gameOver = true;
        } else if (checkVertical(board, move, 4, false) == true) {
            winner = activePlayer;
            gameOver = true;
        } else if (checkDiagonal(board, move, 4, false, false) == true) {
            winner = activePlayer;
            gameOver = true;
        } else if (checkDiagonal(board, move, 4, false, true) == true) {
            winner = activePlayer;
            gameOver = true;
        } else if (legalMoves.stream().allMatch(x -> x.row == -1))
            gameOver = true; // Draw, no legal moves left
        return gameOver;
    }

}
