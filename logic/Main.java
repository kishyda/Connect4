public class Main
{
	public static void main(String[] args) {
		System.out.println("Connect 4");
		Game game = new Game(false, 0); //p2p flag=false, 1=hard level 0=easy level
		
		//game.runGameLoop(); //uncomment to start automatic terminal loop
		
		//example use for step method: loop while game is not over, input a move 
		//(CPU will play automatically after you)
		//while(!game.getGameOver()) {
		//    Move move = new Move(YOUR_GET_ROW, YOUR_GET_COL); //YOUR MOVE ROW/COL GETTER METHOD GOES HERE
        //    while(!game.checkLegalMove(game.board, move, game.activePlayer)) {
		//          Move move = new Move(YOUR_GET_ROW, YOUR_GET_COL); //YOUR MOVE ROW/COL GETTER METHOD GOES HERE 
		                                        //(PREVIOUSLY SELECTED MOVE WAS NOT LEGAL)
		//    }
		//    game.step(move); 
		//}
	}
}
