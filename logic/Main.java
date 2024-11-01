public class Main
{
	public static void main(String[] args) {
		System.out.println("Connect 4");
		Game game = new Game(false, 0); //p2p flag=false, 1=hard level 0=easy level
		
		//game.runGameLoop(); //uncomment to start automatic terminal loop
		
		//example use for step method: loop while game is not over, input a move (CPU will play automatically)
		//while(!game.getGameOver()) {
		//    game.step(new Move(5, 0)); //instead of (5,0), your move goes here
		//}
	}
}
