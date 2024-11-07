import java.util.Random;
import java.lang.Math;

public class Board
{
    static int N_OF_ROWS = 6;
    static int N_OF_COLS = 7;
    char[] stones = {'O', 'X'};
    //int[] stones = {-1, 1}
    private Character[][] boardMatrix = new Character[N_OF_ROWS][N_OF_COLS];
    public Board() {
        initBoard();
    }
    
    void initBoard() {
        for(int i=0; i<N_OF_ROWS; i++) {
            for(int j=0; j<N_OF_COLS; j++) {
                boardMatrix[i][j] = '_';
            }
        }
    }
    
    void copyBoard(Character[][] copyFromBoard) {
        for(int i=0; i<N_OF_ROWS; i++) {
            for(int j=0; j<N_OF_COLS; j++) {
                boardMatrix[i][j] = copyFromBoard[i][j];
            }
        }
    }
    
    public Character[][] getBoard() {
        return boardMatrix;
    }
    
    public void setStone(Move move, int playerNo) {
        boardMatrix[move.row][move.col] = stones[playerNo%2];
    }
    
    /*public Character[][] transposeBoard() {
    Character[][] transposedBoard = new Character[N_OF_COLS][N_OF_ROWS];
    for (int i = 0; i < N_OF_ROWS; i++) {
        for (int j = 0; j < N_OF_COLS; j++) {
            transposedBoard[j][i] = boardMatrix[i][j];
        }
    }
    return transposedBoard;
    }*/
    
    public void printBoard() {
        System.out.println(" 0123456");
        for(int i=0; i<N_OF_ROWS; i++) {
            System.out.print(i);
            for(int j=0; j<N_OF_COLS; j++) {
                System.out.print(boardMatrix[i][j]);
            }
            System.out.print("\n");
        }
    }
}