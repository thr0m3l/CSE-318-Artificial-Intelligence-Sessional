package com.company;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        int size = sc.nextInt();
        int [][]mat = new int[size][size];

        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                mat[i][j] = sc.nextInt();
            }
        }

        Node n = new Node(size, 1, mat);
        Graph g = new Graph(n, size, 1);

        if (n.isSolvable()) {
            System.out.println("Using Hamming Distance Heuristic:");
            g.search();
            System.out.println("Using Manhattan Distance Heuristic:");
            g = new Graph(n, size, 2);
            n.setHeuristic(2);
            g.search();
            System.out.println("Using Linear Conflict Heuristic:");
            g = new Graph(n, size, 3);
            n.setHeuristic(3);
            g.search();
            g.printPath();
        } else {
            System.err.println("NOT SOLVABLE");
        }
    }
}
