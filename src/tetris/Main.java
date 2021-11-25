package tetris;

public class Main {
	public static void main(String[] args) 
	{
		Tetris1 game = new Tetris1();
		Thread t = new Thread(game);
		t.run();
		game.dispose();                                    
	}
}
