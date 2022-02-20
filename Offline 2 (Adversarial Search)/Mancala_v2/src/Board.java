public class Board {
    private final int BINS_PER_ROW = 6;
    private final int STONES_PER_BIN = 4;

    private int[] storage;
    private int[][] bin;


    public Board() {
        storage = new int[3];
        bin = new int[3][BINS_PER_ROW + 1];
        for (int i = 1; i <= 2; i++) {
            for (int j = 1; j <= BINS_PER_ROW; j++) { // Initially 0 stones in the mancala.
                bin[i][j] = STONES_PER_BIN;
            }
        }
    }

    public int getBINS_PER_ROW() {
        return BINS_PER_ROW;
    }

    public int getSTONES_PER_BIN() {
        return STONES_PER_BIN;
    }

    public int[] getStorage() {
        return storage;
    }

    public void setStorage(int[] storage) {
        this.storage = storage;
    }

    public int[][] getBin() {
        return bin;
    }

    public void setBin(int[][] bin) {
        this.bin = bin;
    }

    public Board(Board b){
        //Copy Constructor

        storage = new int[3];
        bin = new int[3][BINS_PER_ROW + 1];
        for (int i = 1; i <= 2; i++) {
            for (int j = 1; j <= BINS_PER_ROW; j++) { // Initially 0 stones in the mancala.
                bin[i][j] = b.getBin()[i][j];
            }
        }

        for(int i = 0; i < 3; i++){
            storage[i] = b.getStorage()[i];
        }
    }

    public int move(int playerNo, int binNo){
        int turn = -1;
        int stones = bin[playerNo][binNo];
        bin[playerNo][binNo] = 0;

        int otherPlayer;

        if (playerNo == 1) otherPlayer = 2;
        else otherPlayer = 1;

        int div = BINS_PER_ROW * 2 + 1;
        int q = Math.floorDiv(stones,div);
        int r = stones % div;

        //Each of them would get at least q (possibly 0)

        for(int i = 0; i < BINS_PER_ROW; i++){
            bin[playerNo][i] += q;
            bin[otherPlayer][i] += q;
        }

        storage[playerNo] += q;

        //special case: if the number of marbles is 13

        if (stones == 13){
            storage[playerNo] =+ 1;
            bin[playerNo][binNo] = 0;
            storage[playerNo] += bin[otherPlayer][BINS_PER_ROW-binNo+1];
            bin[otherPlayer][BINS_PER_ROW-binNo+1] = 0;
        }

        //fill own bins

        for(int i = binNo + 1; i <= BINS_PER_ROW; i++){
            if (r > 0){
                bin[playerNo][i] += 1;
                r--;

                //if the last piece ends up in an empty slot
                if (r == 0 && bin[playerNo][i] == 1
                        && bin[otherPlayer][BINS_PER_ROW-i+1] > 0){
                    storage[playerNo] += bin[otherPlayer][BINS_PER_ROW-i+1] + 1;
                    bin[playerNo][i] = 0;
                    bin[otherPlayer][BINS_PER_ROW-i+1] = 0;
                }
            }
        }

        //fill own storage
        if (r > 0){
            storage[playerNo] += 1;
            r--;
            if (r == 0) turn = playerNo;
        }

        //fill the others

        for(int i = 1; i <= BINS_PER_ROW; i++){
            if (r > 0){
                bin[otherPlayer][i] += 1;
                r--;
            }
        }

        //if still left, fill own storage from the beginning

        for(int i = 1; i <= binNo;  i++){
            if (r > 0){
                bin[playerNo][i] += 1;
                r--;
            }
        }

        //if the turn is not -1. then the currentPlayer is given another turn
        if (turn == -1){
            turn = otherPlayer;
        }

        return turn;

    }

    public boolean gameOver(){
        int p1sum = 0, p2sum = 0;
        p1sum = rowSum(1);
        p2sum = rowSum(2);
        boolean ans = false;

        if (storage[1] + storage[2] == STONES_PER_BIN * BINS_PER_ROW * 2){
            ans = true;
        } else if (p1sum == 0){
            storage[2] += p2sum;

            for(int i = 1; i <= BINS_PER_ROW; i++){
                bin[2][i] = 0;
            }
            ans =  true;
        } else if (p2sum == 0){
            storage[1] += p1sum;

            for(int i = 1; i <= BINS_PER_ROW; i++){
                bin[1][i] = 0;
            }
            ans = true;
        }

        if (ans){
            showResult();
        }

        return ans;

    }

    private int getWinner(){
        if (storage[1] > storage[2]) return 1;
        else if (storage[2] > storage[1]) return 2;
        else return 0;
    }

    private void showResult(){
        if (getWinner() == 1){
            System.out.println("Player-1 wins!");
        } else if (getWinner() == 2){
            System.out.println("Player-2 wins!");
        } else {
            System.out.println("Tie");
        }
        System.out.println(this);
    }

    public int rowSum(int row){
        int sum = 0;
        for(int i = 1; i <= BINS_PER_ROW; i++){
            sum += bin[row][i];
        }
        return sum;
    }

    @Override
    public String toString() {
        //Player2 -> Upper Row
        //In player 2 perspective his bin1 == bin6 of player1
        //Player2 storage left, player1 right

        String ans = "\t";

        //player2
        for(int i = BINS_PER_ROW; i >0; i--){
            ans += bin[2][i] + " ";
        }
        //storage
        ans += "\n   " + storage[2] + "            " + storage[1] + "\n\t";

        for(int i = 1; i <= BINS_PER_ROW; i++){
            ans += bin[1][i] + " ";
        }
        ans += "\n";

        return ans;
    }

}
