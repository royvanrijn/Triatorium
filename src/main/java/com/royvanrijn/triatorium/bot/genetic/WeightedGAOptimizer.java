package com.royvanrijn.triatorium.bot.genetic;

import com.royvanrijn.triatorium.Triatorium;
import com.royvanrijn.triatorium.board.Benchmark;
import com.royvanrijn.triatorium.board.Board;
import com.royvanrijn.triatorium.bot.SmartBot;
import com.royvanrijn.triatorium.bot.TriatoriumBot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.royvanrijn.triatorium.Printer.printBoard;

public class WeightedGAOptimizer {

    public static void main(String[] args) {
        new WeightedGAOptimizer().run();
    }

    private static final int POOL_SIZE = 20;
    private static final int GAMES_PER_ROUND = 20;

    private List<BotHolder> botPool = new ArrayList<>();

    private void run() {

        Triatorium triatorium = new Triatorium();
        Board board = new Board();

        // Fill with random bots:
        for(int i = 0; i < POOL_SIZE; i++) {
            WeightedBot bot = new WeightedBot("WeightedBot#"+i);
            makeRandom(bot);
            botPool.add(new BotHolder(bot));
        }

        Benchmark benchmark = new Benchmark();
        loop(0, triatorium, board, benchmark);
        while(true) {
            loop(POOL_SIZE/2, triatorium, board, benchmark);
        }
    }

    private void loop(final int startAt, final Triatorium triatorium, final Board board, final Benchmark benchmark) {
        // For each bot:
        for(int x = startAt; x < botPool.size(); x++) {
            botPool.get(x).resetFitness();
        }

        for(int x = startAt; x < botPool.size(); x++) {
            BotHolder b1 = botPool.get(x);

            for(TriatoriumBot bot : benchmark.getBenchmark()) {

                for(int i = 0; i < GAMES_PER_ROUND/2; i++) {
                    triatorium.playGameWithBots(board, i % 2, b1.bot, bot);
                    int[] scores = board.calculateScore();

                    b1.incrFitness(scores[0] - scores[1]);

                    board.reset();

                    // Also play the other way around to avoid fitting to one play direction (!):
                    triatorium.playGameWithBots(board, i % 2, bot, b1.bot);
                    scores = board.calculateScore();

                    b1.incrFitness(scores[1] - scores[0]);

                    board.reset();
                }
            }
            System.out.println(b1.bot.getName() + " scored " + b1.fitness);
        }

        Collections.sort(botPool);
        for(int  i = 0; i<POOL_SIZE/2; i++) {
            System.out.println("Survivor " + (i+1) + ": "+botPool.get(i).fitness);
        }

        // The bottom half gets filled with top half mutated:
        for(int i = POOL_SIZE/2; i < POOL_SIZE; i++) {
            WeightedBot weightedBot = botPool.get(i).bot;

            copyWeights(botPool.get(i - POOL_SIZE/2).bot, weightedBot);
            for(int f = 0; f<1 + random.nextInt(10); f++) {
                updateRandomField(weightedBot);
            }
        }

        System.out.println("End of round");
        testBot(board, triatorium, botPool.get(0).bot);
        System.out.println(Arrays.toString(botPool.get(0).bot.getExplosionWeights()));
        System.out.println(Arrays.toString(botPool.get(0).bot.getPlacementWeights()));

    }

    private class BotHolder implements Comparable<BotHolder> {

        private long fitness = 0;
        private WeightedBot bot;

        BotHolder(WeightedBot bot) {
            this.bot = bot;
        }

        public void resetFitness() {
            this.fitness = 0;
        }

        public void incrFitness(long newFitness) {
            this.fitness += newFitness;
        }

        @Override
        public int compareTo(final BotHolder o) {
            // Fitness, high to low:
            return Long.compare(o.fitness, fitness);
        }
    }


    private SmartBot smartBot = new SmartBot();

    private void testBot(final Board board, final Triatorium triatorium, final WeightedBot winningBot) {
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
        switch(random.nextInt(2)) {
            case 0:
                losingBot.getExplosionWeights()[random.nextInt(losingBot.getExplosionWeights().length)] = random.nextDouble();
                return;
            case 1:
                losingBot.getPlacementWeights()[random.nextInt(losingBot.getPlacementWeights().length)] = random.nextDouble();
                return;
        }
    }

}
