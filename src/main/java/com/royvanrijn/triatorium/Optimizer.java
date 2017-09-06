package com.royvanrijn.triatorium;

import com.royvanrijn.triatorium.board.Board;
import com.royvanrijn.triatorium.bot.SmartBot;
import com.royvanrijn.triatorium.bot.TriatoriumBot;
import com.royvanrijn.triatorium.bot.WeightedBot;

import java.util.Arrays;
import java.util.Random;

import static com.royvanrijn.triatorium.Printer.printBoard;

public class Optimizer {

    public static void main(String[] args) {
        new Optimizer().run();
    }

    private void run() {

        // Make instance of game logic:
        Triatorium triatorium = new Triatorium();

        // Make board:
        Board board = new Board();

        WeightedBot[] bots = new WeightedBot[2];
        bots[0] = new WeightedBot(0);
        bots[1] = new WeightedBot(1);

        makeRandom(bots[0]);
        makeRandom(bots[1]);

        long round = 0;
        while(true) {
            round++;
            // Optimize both, continue with winner!

            // Evaluate the bots:
            long balance = 0;
            for(int i = 0; i < 100; i++) {
                triatorium.playGameWithBots(board, bots, i%2);

                int[] scores = board.calculateScore();

                balance += scores[0];
                balance -= scores[1];

                board.reset();
            }

            // Pick the winning bot:
            int survivor = 0;
            if(balance < 0) {
                survivor = 1;
            }

            WeightedBot winningBot = bots[survivor];
            WeightedBot losingBot = bots[1 - survivor];

            System.out.println(balance + " winner is: "+survivor );

            if(round % 10 == 0) {
                testBot(board, triatorium, winningBot);

                System.out.println(Arrays.toString(bots[survivor].getExplosionWeights()));
                System.out.println(Arrays.toString(bots[survivor].getPlacementWeights()));
            }

            // Incremental change loser:
            copyWeights(winningBot, losingBot);
            for(int i = 0; i < (1 + random.nextInt(10)); i++) {
                updateRandomField(losingBot);
            }

            // And repeat.
        }

    }

    private void testBot(final Board board, final Triatorium triatorium, final WeightedBot winningBot) {
        // Pit against known smart bot:
        TriatoriumBot[] testBots = new TriatoriumBot[2];
        testBots[0] = new SmartBot(0);
        testBots[1] = new WeightedBot(1);
        WeightedBot weightedBot = (WeightedBot) testBots[1];

        copyWeights(winningBot, weightedBot);

        long w1 = 0;
        long w2 = 0;
        long t = 0;
        long p1 = 0;
        long p2 = 0;

        for(int i = 0; i< 100; i++) {
            board.reset();
            triatorium.playGameWithBots(board, testBots, i%2);
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
        }
        printBoard(board);
        board.reset();
        System.out.println(p1+" : "+p2+" | "+w1+" : "+w2 + " : "+t + " ("+(w1+w2+t)+")");
    }

    private void copyWeights(final WeightedBot from, final WeightedBot to) {
        for(int i = 0; i < to.getExplosionWeights().length; i++) {
            to.getExplosionWeights()[i] = from.getExplosionWeights()[i];
        }
        for(int i = 0; i < to.getPlacementWeights().length; i++) {
            to.getPlacementWeights()[i] = from.getPlacementWeights()[i];
        }
    }

    private final Random random = new Random();

    private void makeRandom(WeightedBot bot) {
        for(int i = 0; i < bot.getExplosionWeights().length; i++) {
            bot.getExplosionWeights()[i] = random.nextDouble();
        }
        for(int i = 0; i < bot.getPlacementWeights().length; i++) {
            bot.getPlacementWeights()[i] = random.nextDouble();
        }
    }

    private void updateRandomField(final WeightedBot losingBot) {
        if(random.nextBoolean()) {
            losingBot.getExplosionWeights()[random.nextInt(losingBot.getExplosionWeights().length)] = random.nextDouble();
        } else {
            losingBot.getPlacementWeights()[random.nextInt(losingBot.getPlacementWeights().length)] = random.nextDouble();
        }
    }


}
