package com.company;

import java.io.FileWriter;
import java.util.*;

public class Graph {
    private final Node start;
    private final int boardSize;
    private int heuristic;
    private PriorityQueue<Node> openList;
    private HashMap<Node , Integer> openset;
    private HashSet<Node> closedset;
    private FileWriter fileWriter;
    private static HashMap<Integer, Pair<Integer,Integer>> goalNode;
    private Stack<Node> path;

    Graph(Node start, int boardSize, int heuristic){
        this.start = start;
        this.boardSize = boardSize;
        this.heuristic = heuristic;

        setGoalNode();

        openList = new PriorityQueue<>(new NodeComparator());
        openset = new HashMap<>();
        closedset = new HashSet<>();
        path = new Stack<>();

    }

    public static HashMap<Integer, Pair<Integer, Integer>> getGoalNode() {
        return goalNode;
    }


    private void setGoalNode(){
        goalNode = new HashMap<>();
        int k = 1;
        for(int i =0 ; i< boardSize ; i++){
            for(int j=0 ; j< boardSize ; j++)
            {
                if( k >= (boardSize*boardSize)){
                    goalNode.put(0 , new Pair<>(i,j));
                    break;
                }
                goalNode.put(k , new Pair<>(i,j));
                k++;
            }
        }
    }

    private ArrayList<Node> createChildren(Node n){
        Pair<Integer, Integer> blankPosition = n.getBlankPos();
        int bx = blankPosition.key;
        int by = blankPosition.value;
        ArrayList<Node> children = new ArrayList<>();

        for(int i = -1; i < 2; i++){
            for(int j = -1; j < 2; j++){
                if (Math.abs(i) == Math.abs(j) || bx + i >= boardSize ||
                        by + j >= boardSize || bx + i < 0 || by + j < 0 ){
                    continue;
                }

                int arr[][] = new int[boardSize][boardSize];

                for(int k = 0; k < boardSize; k++){
                    for(int l = 0; l < boardSize; l++){
                        arr[k][l] = n.getArr()[k][l];
                    }
                }

                arr[bx][by] = arr[bx+i][by+j];
                arr[bx+i][by+j] = 0;

                Node child = new Node(boardSize, heuristic, arr);
                child.setH(child.getHeuristic());
                child.setG(n.getG()+1);
                child.setF(child.getG()+child.getH());
                child.setParent(n);

                children.add(child);
            }
        }
        return children;
    }

    public int search(){
        int expanded = 0;

        start.setH(start.getHeuristic());
        start.setG(0);
        start.setF(start.getH()+start.getG());
        start.setParent(null);
        openList.add(start);
        openset.put(start, start.getF());

        while (!openList.isEmpty()){
            Node current = openList.peek();
            openList.poll();

            if (current.isGoal()){
                System.out.println("Expanded: " + closedset.size());
                System.out.println("Explored: " + (closedset.size() + openList.size()));
                System.out.println("No of steps: " + current.getG());

                Node tmp = current;

                while (tmp != null){
                    path.push(tmp);
                    tmp = tmp.getParent();
                }

                return current.getG();
            } else {
                closedset.add(current);
                ArrayList<Node> children = createChildren(current);

                for(Node child : children){
                    if (!closedset.contains(child)){
                        if (openset.containsValue(child)){
                            int f = openset.get(child);
                            if (f > child.getF()){
                                openList.remove(child);
                                openset.remove(child);
                                openset.put(child, child.getF());
                            }
                        } else {
                            openList.add(child);
                            openset.put(child, child.getF());
                        }
                    }
                }
            }
        }
        return -1;
    }

    void printPath(){
        while(!path.isEmpty()){
            System.out.println(path.peek());
            path.pop();
        }
    }


}
