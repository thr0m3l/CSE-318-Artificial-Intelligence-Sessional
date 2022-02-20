import java.util.Scanner;

public class Player {
    private int playerNo;
    private int otherPlayer;
    private int playerType;
    private Board tempBoard;
    private Heuristic heuristic;

    //for heuristic
    private int W1, W2, W3, W4;
    private int additionalMoves;
    private int depth;
    private Board board;

    public Player(int playerNo, int playerType, int depth, Board board, int heuristic) {
        W1 = 30;
        W2 = 20;
        W3 = 5;
        W4 = 5;


        this.playerNo = playerNo;
        this.playerType = playerType;
        this.board = board;
        this.heuristic = new Heuristic(heuristic, board, W1, W2, W3, W4);
        this.heuristic.setPlayer(this);

        if (playerNo == 1) otherPlayer = 2;
        else otherPlayer = 1;


        this.depth = depth;

        additionalMoves = 0;
    }

    public int nextMove(){
        if (playerType == 1){
            Scanner sc = new Scanner(System.in);
            int move = -1;

            while (true){
                System.out.println("Your move: ");
                move = sc.nextInt();

                if (move > 0 && move <= board.getBINS_PER_ROW()) break;
                else System.err.println("WRONG MOVE!");
            }

            return move;

        } else if (playerType == 2) {
            //AI
            Pair bin = minimax(new Board(this.board), this.depth, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
            System.out.println("AI Selected: " + bin.second);
            return bin.second;
        }
        return -6969;
    }

    public Pair minimax(Board board, int depth, boolean isMax, int alpha, int beta){
        if (depth == 0 || board.gameOver()){
//            System.out.println("#DEBUG---Heuristic value: " + heuristic.evaluate(board));
            return new Pair(heuristic.evaluate(board), -1);
        }

        int successor = -1;

        Pair currentValue;
        if (isMax){

            int bestValue = Integer.MIN_VALUE;

            for(int i = 1; i <= board.getBINS_PER_ROW(); i++){
                Board copyBoard = new Board(board);

                if (board.getBin()[this.playerNo][i] > 0){
                    int turn = copyBoard.move(this.playerNo, i);

                    if (turn == this.playerNo){
                        this.additionalMoves++;
                        currentValue = minimax(copyBoard, depth-1, true, alpha, beta);
                        this.additionalMoves--;
                    } else {
                        currentValue = minimax(copyBoard, depth-1, false, alpha, beta);
                    }

                    if (currentValue.first > bestValue){
                        bestValue = currentValue.first;
                        successor = i;
//                        System.out.println("Updated to " + successor + " for bestVal = " + bestValue);
                    }

                    alpha = Math.max(bestValue, alpha);

                    if (beta <= alpha){
                        break;
                    }

                }
            }

            return new Pair(bestValue, successor);


        } else {

            int bestValue = Integer.MAX_VALUE;

            for(int i = 1; i <= board.getBINS_PER_ROW(); i++){
                Board copyBoard = new Board(board);
                int turn = -1;
                if (board.getBin()[playerNo][i] > 0){
                    turn = copyBoard.move(playerNo, i);
                }
                
                if (turn == this.otherPlayer){
                    additionalMoves--;
                    currentValue = minimax(copyBoard, depth-1, false, alpha, beta);
                    additionalMoves++;
                } else {
                    currentValue = minimax(copyBoard, depth-1, true, alpha, beta);
                }

                bestValue = Math.min(bestValue, currentValue.first);
                beta = Math.min(bestValue, beta);

                if (beta <= alpha){
                    break;
                }
            }
            return new Pair(bestValue, -1);
        }

    }

    public int getPlayerNo() {
        return playerNo;
    }

    public void setPlayerNo(int playerNo) {
        this.playerNo = playerNo;
    }

    public int getOtherPlayer() {
        return otherPlayer;
    }

    public void setOtherPlayer(int otherPlayer) {
        this.otherPlayer = otherPlayer;
    }

    public int getPlayerType() {
        return playerType;
    }

    public void setPlayerType(int playerType) {
        this.playerType = playerType;
    }

    public int getW1() {
        return W1;
    }

    public void setW1(int w1) {
        W1 = w1;
    }

    public int getW2() {
        return W2;
    }

    public void setW2(int w2) {
        W2 = w2;
    }

    public int getW3() {
        return W3;
    }

    public void setW3(int w3) {
        W3 = w3;
    }

    public int getW4() {
        return W4;
    }

    public void setW4(int w4) {
        W4 = w4;
    }

    public int getAdditionalMoves() {
        return additionalMoves;
    }

    public void setAdditionalMoves(int additionalMoves) {
        this.additionalMoves = additionalMoves;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
