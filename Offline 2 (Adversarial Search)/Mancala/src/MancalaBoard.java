import java.util.ArrayList;

public class MancalaBoard {
    private static final int TOTAL_BINS = 6;
    private static final int STONES_PER_BIN = 4;

    public int opMoves = 0;
    public int plMoves = 0;

    private ArrayList<Integer> lowerBin;
    private ArrayList<Integer> upperBin;
    private int lowerStorage;
    private int upperStorage;

    public MancalaBoard(){
        this.lowerBin = new ArrayList<>();
        this.upperBin = new ArrayList<>();
        this.upperStorage = 0;
        this.lowerStorage = 0;

        for(int i = 0; i < TOTAL_BINS; i++){
            lowerBin.add(STONES_PER_BIN);
            upperBin.add(STONES_PER_BIN);
        }

    }

    public MancalaBoard(MancalaBoard b){
        this.lowerBin = new ArrayList<>();
        this.upperBin = new ArrayList<>();
        this.upperStorage = b.getUpperStorage();
        this.lowerStorage = b.getLowerStorage();

        for(int i = 0; i < b.getUpperBin().size(); i++){
            this.upperBin.add(b.getUpperBin().get(i));
            this.lowerBin.add(b.getLowerBin().get(i));
        }
    }

    public void printBoard(){
        System.out.println("-----------------------");

        for(int i = 0; i < TOTAL_BINS; i++){
            System.out.print(upperBin.get(i) + " ");
        }
        System.out.print("\n");

        System.out.println("------------------------");

        for(int i = 0; i < TOTAL_BINS; i++){
            System.out.print(lowerBin.get(i) + " ");
        }
        System.out.print("\n");
        System.out.println("-------------------------");

        System.out.println("Upper storage: " + upperStorage);
        System.out.println("Lower storage: " + lowerStorage);
    }

    public int getTotalLowerBinStones(){
        int sum = 0;
        for (int i = 0; i < TOTAL_BINS; i++) {
            sum += lowerBin.get(i);
        }
        return sum;
    }

    public int getTotalUpperBinStones(){
        int sum = 0;
        for (int i = 0; i < TOTAL_BINS; i++) {
            sum += upperBin.get(i);
        }
        return sum;
    }

    public ArrayList<Integer> getPlayerBin(boolean isOpponent){
        if (isOpponent){
            return upperBin;
        } else {
            return lowerBin;
        }
    }

    public ArrayList<Integer> getOpponentBin(boolean isOpponent){
        if (isOpponent){
            return lowerBin;
        } else {
            return upperBin;
        }
    }

    public boolean move(int pos, boolean isOp){
        boolean freeturn = false;
        int myst, opst;
        ArrayList<Integer> playerBin, opponentBin;

        if (isOp){
            myst = getUpperStorage();
            opst = getLowerStorage();
            playerBin = getUpperBin();
            opponentBin = getLowerBin();

        } else {
            opst = getUpperStorage();
            myst = getLowerStorage();
            opponentBin = getUpperBin();
            playerBin = getLowerBin();
        }

        if (playerBin.get(pos) == 0){
//            System.err.println("INVALID MOVE!");
            return false;
        }

        if (isOp){
            opMoves++;
        } else {
            plMoves++;
        }

        int nStones = playerBin.get(pos);
        playerBin.set(pos, 0);
        int newPos = pos;
        ArrayList<Integer> currentBin = playerBin;

        for(int i = 0; i < nStones; i++){

            //still at playerBin, yet to move to opponentBin
            if (currentBin.equals(playerBin)){
                if (isOp){
                    newPos--;
                    if(newPos == -1){
                        //moves to opponentBin

                        newPos = 0;
                        myst++;
                        i++;
                        currentBin = opponentBin;

                    }
                } else {
                    newPos++;

                    if (newPos == 6){
                        newPos = 5;
                        myst++;
                        i++;
                        currentBin = opponentBin;
                    }
                }

                if (currentBin.get(newPos) == 0 && i == nStones - 1){
                    //Steal
//                    System.out.println("Stolen stones ------");
                    myst += opponentBin.get(newPos) + 1;
                    opponentBin.set(newPos, 0);
                    continue;
                }
            } else {
                if (isOp){
                    newPos++;

                    if (newPos == 6){
                        newPos = 5;
                        currentBin = playerBin;
                    }
                } else {
                    newPos--;
                    if (newPos == -1){
                        newPos = 0;
                        currentBin = playerBin;
                    }
                }
            }

            if (nStones == i){
//                System.out.println("Free turn-----------");

                if (isOp){
                    setUpperStorage(myst);
                    setLowerStorage(opst);
                } else {
                    setLowerStorage(myst);
                    setUpperStorage(opst);
                }

                return true;
            }

            int val = currentBin.get(newPos);
            currentBin.set(newPos, val+1);

        }

        if (isOp){
            setUpperStorage(myst);
            setLowerStorage(opst);
        } else {
            setLowerStorage(myst);
            setUpperStorage(opst);
        }

        return false;


    }

    

    public boolean gameOver(){
        boolean over_upper = true, over_lower = true;
        for (int i = 0; i < TOTAL_BINS; i++) {
            if(upperBin.get(i) != 0 ){
                over_upper = false;
                break;
            }

        }

        for (int i = 0; i < TOTAL_BINS; i++) {
            if(lowerBin.get(i) != 0 ){
                over_lower = false;
                break;
            }

        }
        if(over_lower && over_upper){
            if(over_upper)
                for(int i = 0; i < TOTAL_BINS; i++){
                    lowerStorage += lowerBin.get(i);
                }
            else
                for(int i = 0; i < TOTAL_BINS; i++){
                    upperStorage += upperBin.get(i);
                }

            return true;
        }
        else {
            return false;
        }
    }



    //GETTERS AND SETTERS

    public ArrayList<Integer> getLowerBin() {
        return lowerBin;
    }

    public void setLowerBin(ArrayList<Integer> lowerBin) {
        this.lowerBin = lowerBin;
    }

    public ArrayList<Integer> getUpperBin() {
        return upperBin;
    }

    public void setUpperBin(ArrayList<Integer> upperBin) {
        this.upperBin = upperBin;
    }

    public int getLowerStorage() {
        return lowerStorage;
    }

    public void setLowerStorage(int lowerStorage) {
        this.lowerStorage = lowerStorage;
    }

    public int getUpperStorage() {
        return upperStorage;
    }

    public void setUpperStorage(int upperStorage) {
        this.upperStorage = upperStorage;
    }
    
    public static int getTotalBins(){
        return TOTAL_BINS;
    }
}