package com.company;

import java.util.Objects;
import java.util.Scanner;

public class Node {
    private int f, g, h;
    private int boardSize;
    private int arr[][];
    private Node parent;
    private int heuristic;

    Node(int n, int h, int [][]mat){
        boardSize = n;
        parent = null;
        heuristic = h;

        arr = new int[boardSize][boardSize];

        for(int i = 0; i < boardSize; i++){
            for(int j = 0; j < boardSize; j++){
                arr[i][j] = mat[i][j];
            }
        }
    }

    @Override
    public String toString() {
        String output = "";
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (arr[i][j] != 0)
                    output += arr[i][j] + " ";
                else
                    output += "* ";
            }

            output += "\n";
        }
        return output;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Node)) return false;
        Node b = (Node) obj;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if(this.arr[i][j] != b.arr[i][j]){
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        for (int i = 0; i< boardSize; i++){
            for(int j =0 ; j< boardSize ; j++){
                hash += (j+31)*(i+31)* Objects.hashCode(arr[i][j]);
            }
        }
        return hash;
    }

    public boolean isGoal(){
        int k = 0;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                k++;
                if (arr[i][j] != 0 && arr[i][j] != k)
                    return false;
            }
        }
        return true;
    }

    public int nInversion(){
        int n = boardSize * boardSize;
        int inversion = 0, count = 0;
        int oneD[] = new int[n];

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                oneD[count++] = arr[i][j];
            }
        }

        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (oneD[i] > oneD[j] && (oneD[j]) != 0)
                    inversion++;
            }
        }
        return inversion;
    }

    public Pair<Integer, Integer> getBlankPos(){
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (arr[i][j] == 0) {
                    return new Pair<>(i, j);
                }
            }
        }
        return new Pair<>(-1,-1);
    }

    public boolean isSolvable(){
        int inversion = nInversion();

        System.out.println("Inversion count: " + inversion);

        if (boardSize % 2 == 1) {
            System.out.println("Boardsize is odd");

            if (inversion % 2 == 1) {
                System.err.println("NOT SOLVABLE");
                return false;
            } else {
                System.out.println("SOLVABLE");
                return true;
            }
        } else {
            System.out.println("Boardsize is even");

            int blankX = getBlankPos().getKey();


            System.out.println("Zero is in the " + blankX + " row");

            if ((inversion + blankX) % 2 == 0) {
                System.err.println("NOT SOLVABLE");
                return false;
            } else {
                System.out.println("SOLVABLE");
                return true;
            }
        }

    }

    private int getHammingDistance(){
        int dist = 0, k = 0;
        for (int i = 0; i < this.boardSize; i++) {
            for (int j = 0; j < this.boardSize; j++) {
                k++;
                if (this.arr[i][j] != k && this.arr[i][j] != 0) {
                    dist++;
                }
            }
        }
        return dist;
    }

    private int getManhattanDistance(Node node) {
        int row, col;
        int k, dist;
        k = dist = 0;

        for (int i = 0; i < this.boardSize; i++) {
            for (int j = 0; j < node.boardSize; j++) {
                k++;
                if (node.arr[i][j] != k && node.arr[i][j] != 0) {
                    row = (node.arr[i][j] - 1) / node.boardSize;
                    col = (node.arr[i][j] - 1) % node.boardSize;

                    dist += Math.abs(i - row) + Math.abs(j - col);
                }
            }
        }

        return dist;
    }

    private int getLinearConflict() {
        int lc = 0;
        int dist = 0;
        //Horizontal Conflict
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                Pair pos  = Graph.getGoalNode().get(arr[i][j]);

                if((Integer) pos.getKey() != i || arr[i][j] == 0)
                    continue;

                for (int k = j+1; k < boardSize; k++) {
                    if(Graph.getGoalNode().get(arr[i][k]).getKey() != i)
                        continue;

                    if(arr[i][k] != 0 &&  arr[i][k] < arr[i][j]){
                        lc++;
                    }
                }
            }
        }


        //Vertical Conflict
        for (int j = 0; j < boardSize; j++) {
            for (int i = 0; i < boardSize; i++) {
                Pair pos = Graph.getGoalNode().get(arr[i][j]);
                if((Integer) pos.getValue() != j || arr[i][j] == 0)
                    continue;

                for (int k = i+1; k < boardSize; k++) {
                    if(Graph.getGoalNode().get(arr[k][j]).getKey() != j)
                        continue;

                    if(arr[k][j] != 0 &&
                            arr[k][j] < arr[i][j]){
                        lc++;
                    }
                }

            }
        }
        lc = getManhattanDistance(this) + 2 * lc;
        return lc;
    }

    public int getHeuristic() {
        if (heuristic == 1) {
            return getHammingDistance();
        } else if (heuristic == 2) {
            return getManhattanDistance(this);
        } else {
            return getLinearConflict();
        }
    }


    public int getF() {
        return f;
    }

    public void setF(int f) {
        this.f = f;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    public int[][] getArr() {
        return arr;
    }

    public void setArr(int[][] arr) {
        this.arr = arr;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }


    public void setHeuristic(int heuristic) {
        this.heuristic = heuristic;
    }

    public void scan(){
        Scanner sc = new Scanner(System.in);

        for(int i = 0; i < boardSize; i++){
            for(int j = 0; j < boardSize; j++){
                arr[i][j]=sc.nextInt();
            }
        }

    }
}
