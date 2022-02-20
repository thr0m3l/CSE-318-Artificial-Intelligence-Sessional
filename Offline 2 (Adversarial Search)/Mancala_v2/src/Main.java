public class Main {

    public static void main(String[] args) {
	    Board board = new Board();
        Player p1 = new Player(1, 1, 10, board, 1 );
        Player p2 = new Player(2, 2, 10, board, 2 );
        int move;
        while (!board.gameOver()){
            System.out.println(board);
            move = p1.nextMove();
            board.move(1, move);
            System.out.println(board);
            move = p2.nextMove();
            board.move(2, move);
        }
    }
}
