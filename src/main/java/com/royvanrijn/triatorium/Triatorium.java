package com.royvanrijn.triatorium;

import com.royvanrijn.triatorium.board.Board;
import com.royvanrijn.triatorium.board.CoordinateHash;
import com.royvanrijn.triatorium.board.Triangle;
import com.royvanrijn.triatorium.bot.SmartBot;
import com.royvanrijn.triatorium.bot.TriatoriumBot;

import java.util.Arrays;

import static com.royvanrijn.triatorium.board.Board.MAX_WIDTH;
import static com.royvanrijn.triatorium.board.Board.SHAPE_EDGE;

public class Triatorium {

    public static void main(String[] args) {
        new Triatorium().run();
    }

    private void run() {
        Board board = new Board();
        //while(true) {
        for(int i = 0; i<1000;i++) {
            //playGameWithBots(board, new TriatoriumBot[] {new SmartBot(0), new GreedyBot(1) });
            playGameWithBots(board, new TriatoriumBot[] {new SmartBot(0), new SmartBot(1) });
            board.reset();
        }
    }

    private void playGameWithBots(Board board, TriatoriumBot[] bots) {

        int currentPlayer = 0;

        while(board.generatePlacementMoves(currentPlayer).size() > 0) {

            //printBoard(board);

            final TriatoriumBot bot = bots[currentPlayer];

            // Bot gets to pick a move using the board:
            PlacementMove placementMove = bot.pickMove(board);

            if(placementMove == null) {
                throw new IllegalArgumentException("Player didn't return move while board says it is possible.");
            }

            // Apply the move to the board:
            board.applyPlacement(currentPlayer, placementMove);

            // While there are pending explosions, handle these:
            while(board.hasPendingExplosions()) {

                 Triangle triangle = board.getNextPendingExplosion();

                 // Player that placed the last token on the triangle gets to resolve the explosion:
                 int playerToDistribute = triangle.getTokens().get(2);

                 final TriatoriumBot explosionBot = bots[playerToDistribute];
                 ExplosionMove move = explosionBot.evaluateExplosion(board, triangle);

                 board.applyExplosion(playerToDistribute, move);
             }

            // Next player:
            currentPlayer = (currentPlayer+1)%2;
        }

        int[] scores = board.calculateScore();
        p1 += scores[0];
        p2 += scores[1];

        if(scores[0] > scores[1]) {
            w1++;
        } else if(scores[1] > scores[0]) {
            w2++;
        } else {
            t++;
        }

        //[0, 0] 10052 : 10928 | 2257 : 3437 : 4306 (10000)

        System.out.println(Arrays.toString(scores)+" "+p1+" : "+p2+" | "+w1+" : "+w2 + " : "+t + " ("+(w1+w2+t)+")");

        printBoard(board);

    }

    long w1 = 0;
    long w2 = 0;
    long t = 0;
    long p1 = 0;
    long p2 = 0;

    /**
     * Long ugly method to print the board in ASCII art

     * @param board
     */
    private void printBoard(Board board) {
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
