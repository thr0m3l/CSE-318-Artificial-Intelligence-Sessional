public class Heuristic {
    private int h = 1;
    private MancalaBoard board;
    private Player player;

    public Heuristic(int h, MancalaBoard board) {
        this.h = h;
        this.board = board;
    }

    public Heuristic() {
    }

    public int evaluate(){
        if (h == 1){
            return heuristic1();
        } else if (h == 2) {
            return heuristic2();
        } else return heuristic3();
    }

    private int heuristic2(){
        int w1 = 30, w2 = 20;

        if (getPlayer().isOpponent()){
            return w1*(this.board.getLowerStorage()- board.getUpperStorage()) + w2*(board.getTotalLowerBinStones()- board.getTotalUpperBinStones());
        } else {
            return w1*(this.board.getUpperStorage()- board.getLowerStorage()) + w2*(board.getTotalUpperBinStones()- board.getTotalLowerBinStones());
        }
    }

    private int heuristic3(){
        int w1 = 30, w2 = 20, w3 = 40;

        if (getPlayer().isOpponent()){
            return w1*(board.getLowerStorage()-board.getUpperStorage())+
                    w2*(board.getTotalLowerBinStones()-board.getTotalUpperBinStones()) +
                    w3 * player.getFreeTurn();
        } else {
            return w1*(board.getUpperStorage()-board.getLowerStorage())+
                    w2*(board.getTotalUpperBinStones()-board.getTotalLowerBinStones()) +
                    w3 * player.getFreeTurn();
        }
    }



    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public MancalaBoard getBoard() {
        return board;
    }

    public void setBoard(MancalaBoard board) {
        this.board = board;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    private int heuristic1(){
        if (player.isOpponent()){
            return board.getLowerStorage()- board.getUpperStorage();
        } else {
            return board.getUpperStorage()- board.getLowerStorage();
        }
    }
}
