package com.royvanrijn.triatorium;

import com.royvanrijn.triatorium.board.Board;
import com.royvanrijn.triatorium.board.CoordinateHash;
import com.royvanrijn.triatorium.board.Triangle;

import static com.royvanrijn.triatorium.board.Board.MAX_WIDTH;
import static com.royvanrijn.triatorium.board.Board.SHAPE_EDGE;

public class Printer {

    /**
     * Long ugly method to print the board in ASCII art

     * @param board
     */
    public static void printBoard(Board board) {
        for(int x = 0; x < SHAPE_EDGE.length; x++) {

            if(SHAPE_EDGE[x]==0 && SHAPE_EDGE[x-1] == 0) {
                System.out.print("|");
            } else {
                System.out.print(" ");
            }
            outer:
            for(int y = 0; y < MAX_WIDTH; y++) {

                if(y >= SHAPE_EDGE[x] && y < MAX_WIDTH-SHAPE_EDGE[x]) {

                    Triangle triangle = board.get(CoordinateHash.hash(x, y));

                    String printTriangle;
                    if(triangle.isExploded()) {
                        printTriangle = "XX";
                    } else {
                        printTriangle = "";
                        for(int i:triangle.getTokens()) {
                            if(i != -1) {
                                printTriangle += i;
                            }
                        }
                        if(printTriangle.length() == 3) {
                            // EXPLODING!
                            printTriangle = "$$";
                        } else while(printTriangle.length() < 2) {
                            printTriangle += "`";
                        }
                    }

                    if(y%2==x%2) {
                        System.out.print("__/" + printTriangle);
                    } else {
                        System.out.print(printTriangle + "\\__");
                    }
                    if(y < MAX_WIDTH-SHAPE_EDGE[x]-1 || y == MAX_WIDTH-1) {
                        System.out.print("|");
                    }
                } else if(x > 0 && y >= SHAPE_EDGE[x-1] && y < MAX_WIDTH-SHAPE_EDGE[x-1]) {

                    if(y > MAX_WIDTH/2) {
                        System.out.print("|");
                    }
                    // Check if we need to print endings:
                    if(y%2==x%2) {
                        System.out.print("__/``");
                    } else {
                        System.out.print("``\\__");
                    }
                    if(y < MAX_WIDTH/2) {
                        System.out.print("|");
                    }
                } else {
                    System.out.print("      ");
                }

            }
            System.out.println();
        }
        // Final line:
        System.out.print(" ");
        for(int y = 0; y < MAX_WIDTH; y++) {
            if(y >= SHAPE_EDGE[SHAPE_EDGE.length-1] && y < MAX_WIDTH-SHAPE_EDGE[SHAPE_EDGE.length-1]) {
                if(y > MAX_WIDTH/2) {
                    System.out.print("|");
                }
                // Check if we need to print endings:
                if(y%2==SHAPE_EDGE.length%2) {
                    System.out.print("__/``");
                } else {
                    System.out.print("``\\__");
                }
                if(y < MAX_WIDTH/2) {
                    System.out.print("|");
                }
            } else {
                System.out.print("      ");
            }
        }
        System.out.println();
        System.out.println();
    }

}
