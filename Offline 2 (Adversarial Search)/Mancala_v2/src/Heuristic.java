public class Heuristic {
    private int h = 1;
    private Board board;
    private Player player;
    private int W1, W2, W3, W4;

    public Heuristic(int h, Board board, int W1, int W2, int W3, int W4) {
        this.h = h;
        this.board = board;
        this.W1 = W1;
        this.W2 = W2;
        this.W3 = W3;
        this.W4 = W4;
    }

    public Heuristic() {
    }

    public int evaluate(Board board){
        setBoard(board);
        if (h == 1){
            return heuristic1(board);
        } else if (h == 2) {
            return heuristic2(board);
        } else return heuristic3(board);
    }

    private int heuristic1(Board board){
        return board.getStorage()[player.getPlayerNo()] - board.getStorage()[player.getOtherPlayer()];
    }

    private int heuristic2(Board board){
        int stones_on_my_side = board.rowSum(player.getPlayerNo());
        int stones_on_op_side = board.rowSum(player.getOtherPlayer());
        int myst = board.getStorage()[player.getPlayerNo()];
        int opst = board.getStorage()[player.getOtherPlayer()];

        return W1 * (myst - opst) + W2 * (stones_on_my_side - stones_on_op_side);
    }

    private int heuristic3(Board board){

        return 0;
    }



    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }


}
