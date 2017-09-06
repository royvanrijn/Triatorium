package com.royvanrijn.triatorium;

import com.royvanrijn.triatorium.board.Benchmark;
import com.royvanrijn.triatorium.board.Board;
import com.royvanrijn.triatorium.bot.TriatoriumBot;

import static com.royvanrijn.triatorium.Printer.printBoard;

public class TriatoriumRunner {

    public static void main(String[] args) {
        new TriatoriumRunner().run();
    }

    private void run() {
        Board board = new Board();

        Benchmark benchmark = new Benchmark();
        TriatoriumBot[] bots = new TriatoriumBot[] {
                benchmark.getBenchmark().get(4),
                benchmark.getBenchmark().get(0),
        };

        Triatorium triatorium = new Triatorium(false);

        /*long before = System.currentTimeMillis();
        for(int i = 0; i<500;i++) {
            fight(board, triatorium, (i % 2), benchmark.getBenchmark().get(4), benchmark.getBenchmark().get(0));
            board.reset();
            fight(board, triatorium, (i % 2), benchmark.getBenchmark().get(0), benchmark.getBenchmark().get(4));
            board.reset();
        }*/

        //System.out.println("Took: " + (System.currentTimeMillis() - before));

        fight(board, new Triatorium(true), 0, benchmark.getBenchmark().get(0), benchmark.getBenchmark().get(4));
        fight(board, new Triatorium(true), 0, benchmark.getBenchmark().get(4), benchmark.getBenchmark().get(0));

    }

    public void fight(Board board, Triatorium triatorium, int startWith, TriatoriumBot... bots) {

        triatorium.playGameWithBots(board, startWith, bots);

        int[] scores = board.calculateScore();

        System.out.println(bots[0].getName() + ": " + scores[0]);
        System.out.println(bots[1].getName() + ": " + scores[1]);

        printBoard(board);

        board.reset();

    }
}
