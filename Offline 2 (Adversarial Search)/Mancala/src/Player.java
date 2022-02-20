import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class Player {

    private int depth = 5;

    private MancalaBoard board;
    private boolean isOpponent;
    private Heuristic heuristic;
    private int successor = 0;
    private int maxSuccessor = 0;

    private ArrayList<Integer> playerBin;
    private ArrayList<Integer> opponentBin;
    private int playerStorage;
    private int opponentStorage;

    private int freeTurn;
    private int captured_stones;
    private int cutoff = 12;


    public Player(MancalaBoard board, boolean op, Heuristic heuristic){
        this.board = board;
        this.isOpponent = op;
        this.heuristic = heuristic;

        this.heuristic.setPlayer(this);

        if (isOpponent){
            playerBin = this.board.getUpperBin();
            opponentBin = this.board.getLowerBin();
            opponentStorage = this.board.getLowerStorage();
            playerStorage = this.board.getUpperStorage();
        } else {
            playerBin = this.board.getLowerBin();
            opponentBin = this.board.getUpperBin();
            opponentStorage = this.board.getUpperStorage();
            playerStorage = this.board.getLowerStorage();
        }
    }

    public void move(int choice){
        if (choice == 1){
            //human input
            freeTurn = 0;
            captured_stones = 0;
//            board.printBoard();
            Scanner sc = new Scanner(System.in);
            System.out.println("Your move: ");
            while(board.move(sc.nextInt(), isOpponent));
        } else if (choice == 2){
            //AI

            Date date = new Date();
            long start = date.getTime();

            while (date.getTime() - start < 1500){
                int maxVal = Integer.MIN_VALUE;
                MancalaBoard copy = new MancalaBoard(board);
                //Make a move within 1.5s
                while (true){

                    int val = minmax(board, depth, true, Integer.MIN_VALUE, Integer.MAX_VALUE);

                    if (val > maxVal){
                        maxVal = val;
                        maxSuccessor = successor;
                    }

                    boolean turn = copy.move(successor, isOpponent);
                    if (turn) continue;
                    else break;
                }
                depth++;
                if (depth >= cutoff){
                    break;
                }
            }
            boolean turn = board.move(maxSuccessor, isOpponent);

        }
    }

    public int minmax(MancalaBoard board, int depth, boolean isMax, int alpha, int beta){
        if (depth <= 0 || board.gameOver()){
            return heuristic.evaluate();
        }

        ArrayList<Integer> playerBin = board.getPlayerBin(isOpponent);
        ArrayList<Integer> opponentBin = board.getOpponentBin(isOpponent);

        int currentValue, bestValue;

        if (isMax){
            bestValue = Integer.MIN_VALUE;

            for(int i = 0; i < MancalaBoard.getTotalBins(); i++){
                MancalaBoard copyBoard = new MancalaBoard(board);

                if (playerBin.get(i) != 0){
                    boolean turn = copyBoard.move(i, isOpponent);

                    if (turn){
                        currentValue = minmax(copyBoard, depth-1, true, alpha, beta);
                    } else {
                        currentValue = minmax(copyBoard, depth-1, false, alpha, beta);
                    }

                    if (currentValue > bestValue){
                        bestValue = currentValue;
                        successor = i;
                    }
                    alpha = Math.max(alpha, bestValue);
                }

                if (beta <= alpha){
                    break;
                }
            }

        } else {
            bestValue = Integer.MAX_VALUE;

            for(int i = 0; i < MancalaBoard.getTotalBins(); i++){
                MancalaBoard copyBoard = new MancalaBoard(board);

                if (opponentBin.get(i) != 0){

                    boolean turn = copyBoard.move(i, !isOpponent);

                    if (turn){
                        currentValue = minmax(copyBoard, depth-1 ,false, alpha, beta);
                    } else {
                        currentValue = minmax(copyBoard, depth-1, true, alpha, beta);
                    }

                    bestValue = Math.min(bestValue, currentValue);
                    beta = Math.min(beta, bestValue);
                }
                if (beta <= alpha){
                    break;
                }
            }

        }
        return bestValue;
    }

    public MancalaBoard getBoard() {
        return board;
    }

    public void setBoard(MancalaBoard board) {
        this.board = board;
    }

    public boolean isOpponent() {
        return isOpponent;
    }

    public void setOpponent(boolean opponent) {
        isOpponent = opponent;
    }

    public Heuristic getHeuristic() {
        return heuristic;
    }

    public void setHeuristic(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    public int getSuccessor() {
        return successor;
    }

    public void setSuccessor(int successor) {
        this.successor = successor;
    }

    public ArrayList<Integer> getPlayerBin() {
        return playerBin;
    }

    public void setPlayerBin(ArrayList<Integer> playerBin) {
        this.playerBin = playerBin;
    }

    public ArrayList<Integer> getOpponentBin() {
        return opponentBin;
    }

    public void setOpponentBin(ArrayList<Integer> opponentBin) {
        this.opponentBin = opponentBin;
    }

    public int getPlayerStorage() {
        return playerStorage;
    }

    public void setPlayerStorage(int playerStorage) {
        this.playerStorage = playerStorage;
    }

    public int getOpponentStorage() {
        return opponentStorage;
    }

    public void setOpponentStorage(int opponentStorage) {
        this.opponentStorage = opponentStorage;
    }

    public int getFreeTurn() {
        return freeTurn;
    }

    public void setFreeTurn(int freeTurn) {
        this.freeTurn = freeTurn;
    }

    public int getCaptured_stones() {
        return captured_stones;
    }

    public void setCaptured_stones(int captured_stones) {
        this.captured_stones = captured_stones;
    }
}
