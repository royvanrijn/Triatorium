package com.royvanrijn.triatorium.bot.genetic;

import com.royvanrijn.triatorium.Triatorium;
import com.royvanrijn.triatorium.board.Board;
import com.royvanrijn.triatorium.bot.SmartBot;

import java.util.Arrays;
import java.util.Random;

import static com.royvanrijn.triatorium.Printer.printBoard;

public class SimpleHillClimbOptimizer {

    public static void main(String[] args) {
        new SimpleHillClimbOptimizer().run();
    }

    private void run() {

        // Make instance of game logic:
        Triatorium triatorium = new Triatorium();

        // Make board:
        Board board = new Board();

        WeightedWithLocationBot[] bots = new WeightedWithLocationBot[2];
        bots[0] = new WeightedWithLocationBot();
        bots[1] = new WeightedWithLocationBot();

        makeRandom(bots[0]);
        makeRandom(bots[1]);

        long round = 0;
        while(true) {
            round++;
            // Optimize both, continue with winner!

            // Evaluate the bots:
            long balance = 0;
            for(int i = 0; i < 10; i++) {
                triatorium.playGameWithBots(board, i%2, bots);

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

            WeightedWithLocationBot winningBot = bots[survivor];
            WeightedWithLocationBot losingBot = bots[1 - survivor];

            System.out.println(balance + " winner is: "+survivor );

            if(round % 100 == 0) {
                testBot(board, triatorium, winningBot);

                System.out.println(Arrays.toString(bots[survivor].getExplosionWeights()));
                System.out.println(Arrays.toString(bots[survivor].getPlacementWeights()));
                System.out.println(Arrays.toString(bots[survivor].getLocationWeights()));
            }

            // Incremental change loser:
            copyWeights(winningBot, losingBot);
            for(int i = 0; i < (1 + random.nextInt(10)); i++) {
                updateRandomField(losingBot);
            }

            // And repeat.
        }

    }

    private SmartBot smartBot = new SmartBot();

    private void testBot(final Board board, final Triatorium triatorium, final WeightedWithLocationBot winningBot) {
        // Pit against known smart bot:
        long w1 = 0;
        long w2 = 0;
        long t = 0;
        long p1 = 0;
        long p2 = 0;

        for(int i = 0; i< 100; i++) {
            board.reset();
            triatorium.playGameWithBots(board, i%2, winningBot, smartBot);
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

    private void copyWeights(final WeightedWithLocationBot from, final WeightedWithLocationBot to) {
        for(int i = 0; i < to.getExplosionWeights().length; i++) {
            to.getExplosionWeights()[i] = from.getExplosionWeights()[i];
        }
        for(int i = 0; i < to.getPlacementWeights().length; i++) {
            to.getPlacementWeights()[i] = from.getPlacementWeights()[i];
        }
        for(int i = 0; i < to.getLocationWeights().length; i++) {
            to.getLocationWeights()[i] = from.getLocationWeights()[i];
        }
    }

    private final Random random = new Random();

    private void makeRandom(WeightedWithLocationBot bot) {
        for(int i = 0; i < bot.getExplosionWeights().length; i++) {
            bot.getExplosionWeights()[i] = random.nextDouble();
        }
        for(int i = 0; i < bot.getPlacementWeights().length; i++) {
            bot.getPlacementWeights()[i] = random.nextDouble();
        }
        for(int i = 0; i < bot.getLocationWeights().length; i++) {
            bot.getLocationWeights()[i] = random.nextDouble();
        }
    }

    private void updateRandomField(final WeightedWithLocationBot losingBot) {
        switch(random.nextInt(3)) {
            case 0:
                losingBot.getExplosionWeights()[random.nextInt(losingBot.getExplosionWeights().length)] = random.nextDouble();
                return;
            case 1:
                losingBot.getPlacementWeights()[random.nextInt(losingBot.getPlacementWeights().length)] = random.nextDouble();
                return;
            case 2:
                losingBot.getLocationWeights()[random.nextInt(losingBot.getLocationWeights().length)] = random.nextDouble();
            return;
        }
    }


}
