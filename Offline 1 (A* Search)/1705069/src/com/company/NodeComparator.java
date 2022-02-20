package com.company;

import java.util.Comparator;

    public class NodeComparator implements Comparator<Node> {

        // Overriding compare()method of Comparator
        // for descending order of cgpa
        public int compare(Node s1, Node s2) {
            if ((s1.getH()+s1.getG()) > (s2.getH() + s2.getG()))
                return 1;
            else if ((s1.getH()+s1.getG()) < (s2.getH() + s2.getG()))
                return -1;
            return 0;
        }


    }

