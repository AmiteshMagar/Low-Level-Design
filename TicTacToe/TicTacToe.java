import java.util.*;

enum GameState {
	DRAW, OPEN, VICTORY
}

class Board {

	private ArrayList<ArrayList<Character>> board = new ArrayList<>();
	private Integer boardSize;

	private Integer availableMoves;

	public Board(Integer boardSize){
		this.boardSize = boardSize;
		for(int i=0; i<boardSize; i++){
			board.add(new ArrayList<Character>());
			for(int j=0; j<boardSize; j++) board.get(i).add(' ');
		}

		availableMoves = boardSize * boardSize;
	}

	public void printBoard() {
		for(int i =0; i<boardSize; i++){
			ArrayList<Character> row = board.get(i);

			for(int j=0; j<boardSize; j++){
				System.out.print(row.get(j));
				if(j != board.size()-1){
					System.out.print(" | ");
				}
			}
			System.out.println("");
			for(int j=0; j<boardSize-1; j++){
				System.out.print("----");
			} System.out.print("-");
			System.out.println();
		}
	}

	public Integer getBoardSize(){
		return boardSize;
	}

	public Boolean makeMove(Character symbol, Integer x, Integer y){
		if(x<0 || x>=boardSize || y<0 || y>=boardSize) {
			return false;
		}

		if(board.get(x).get(y) != ' '){
			return false;
		}

		board.get(x).set(y, symbol);
		availableMoves--;
		return true;
	}

	public GameState checkGameState(Character symbol, Integer x, Integer y){
		if(x<0 || x>=boardSize || y<0 || y>=boardSize){
			return GameState.OPEN;
		}

		Boolean isWon = true;

		// check if victory by row
		for(int i=0; i<boardSize; i++){
			if(board.get(i).get(y) != symbol){
				isWon = false;
				break;
			}
		}

		if(isWon) return GameState.VICTORY;
		else isWon = true;

		// check if victory by column
		for(int i=0; i<boardSize; i++){
			if(board.get(x).get(i) != symbol){
				isWon = false;
				break;
			}
		}

		if(isWon) return GameState.VICTORY;
		else isWon = true;

		// diag anti-diag victory
		if(x == y) {
			for(int i=0; i<boardSize; i++){
				if(board.get(i).get(i) != symbol){
					isWon = false;
					break;
				}
			}

			if(isWon) return GameState.VICTORY;
			else isWon = true;
		}


		if (x+y == board.size()-1) {
			for(int i=0; i<boardSize; i++){
				if(board.get(boardSize-1 - i).get(i) != symbol){
					isWon = false;
					break;
				}
			}

			if(isWon) return GameState.VICTORY;
			else isWon = true;
		}

		if(availableMoves == 0){
			return GameState.DRAW;
		}

		return GameState.OPEN;
	}



}

class Player {

	private String name;
	private Character symbol;

	public Player(String name, Character symbol){
		this.name = name;
		this.symbol = symbol;
	}

	public String getName() {
		return name;
	}

	public Character getSymbol(){
		return symbol;
	}

}

class Game {

	private Player player1;
	private Player player2;
	private Board board;

	public Game(Player player1, Player player2, Integer boardSize){

		this.player1 = player1;
		this.player2 = player2;

		this.board = new Board(boardSize);
	}

	public void runGame(){

		Scanner sc = new Scanner(System.in);

		GameState gameState = GameState.OPEN;
		Player currentPlayer = player1;

		board.printBoard();

		while(gameState == GameState.OPEN){

			System.out.println("Enter the x and y co-ordinates of your move " + currentPlayer.getName());
			Integer x = sc.nextInt();
			Integer y = sc.nextInt();

			if (!board.makeMove(currentPlayer.getSymbol(), x, y)){
				System.out.println("illegal move");
				continue;
			}

			board.printBoard();

			gameState = board.checkGameState(currentPlayer.getSymbol(), x, y);
			if(gameState == GameState.OPEN){
				currentPlayer = (currentPlayer == player1)? player2 : player1;
			}
		}

		if(gameState == GameState.VICTORY){
			System.out.println("Congrats! "+ currentPlayer.getName() + " is victorious!");
		} else if(gameState == GameState.DRAW) {
			System.out.println("This game was draw");
		}

		sc.close();
	}

}


public class TicTacToe {

	public static void main(String args[]) throws Exception {

		Player player1 = new Player("Amitesh", 'X');
		Player player2 = new Player("Shreyank", 'O');

		Integer boardSize = 3;

		Game game = new Game(player1, player2, boardSize);

		game.runGame();

	}

}
