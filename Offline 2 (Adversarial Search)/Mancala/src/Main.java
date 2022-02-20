public class Main {

    public static void main(String[] args) {
       MancalaBoard board = new MancalaBoard();
       MancalaBoard copyBoard = new MancalaBoard(board);


       Heuristic h1 = new Heuristic(2, board);
       Heuristic h2 = new Heuristic(3, board);
       Player player1 = new Player(board, false, h1);
       Player player2 = new Player(board, true, h2);

       while (!board.gameOver()){
           player1.move(1);
           board.printBoard();
           player2.move(2);
           board.printBoard();
           System.out.println("Moves: " + board.plMoves + ", " + board.opMoves);
       }
    }
}
