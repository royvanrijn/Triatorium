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

public class WeightedWithLocationGAOptimizer {

    public static void main(String[] args) {
        new WeightedWithLocationGAOptimizer().run();
    }

    private static final int POOL_SIZE = 200;
    private static final int GAMES_PER_ROUND = 20;

    private List<BotHolder> botPool = new ArrayList<>();

    private void run() {

        Triatorium triatorium = new Triatorium();
        Board board = new Board();

        // Fill with random bots:
        for(int i = 0; i < POOL_SIZE; i++) {
            WeightedWithLocationBot bot = new WeightedWithLocationBot(
                    "[0.9900741660654488, 0.492108088958257, 0.8578240833450914, 0.017698985775946974, 0.7115048431767246, 0.47207027873855145, 0.9083461027546154, 0.7695581848498954, 0.9103321778822326, 0.6161005398700057, 0.33696570664879033, 0.24533908959887507, 0.7930209331920696, 0.20730448251892708, 0.06561614559380335, 0.05569375299550894, 0.9260921635791294, 0.6958516808373251, 0.35656804908042594, 0.00899148687247775, 0.4658265668229038, 0.8400704666454879, 0.9321762060249628, 0.44770450809190265, 0.3800542152708486, 0.3283973190406435, 0.598033357039976, 0.5136464101823762, 0.12831447462464096, 0.6592546746848329]",
                    "[0.6086227059565822, 0.001329244933025775, 0.8445905561929508, 0.058619103081099966, 0.9820267871208848, 0.9051449245859059, 0.12343547415918266, 0.6451188533664886, 0.006435267670388267, 0.37502000053493345, 0.6773834438952229, 0.8205599832433649, 0.06182376972646064, 0.5742923780344179, 0.03810265837751503, 0.3416721284371832, 0.9217734828226178, 0.8769083293387638, 0.8921988012494374, 0.867740491003343, 0.5673870482292014, 0.009953463713367583, 0.2866855166457787, 0.08361396460535175]",
                    "[0.47615831600065117, 0.0847228691358537, 0.9680449440884844, 0.0594713371805603, 0.6521206119960752, 0.22005834395849444, 0.7427361030564111, 0.5982254737285567, 0.7179969535992426, 0.8895363246142761, 0.9714762894971873, 0.6528629688541204, 0.5862624721943307, 0.5393653144645908, 0.8945736517128912, 0.06297979163218137, 0.8103480253319414, 0.9154443923044014, 0.5532882323755176, 0.09889605052956907, 0.804512447995253, 0.7843792672645229, 0.8377341850102472, 0.596226900666671, 0.9648690444475245, 0.5410985414131683, 0.05525043071143343, 0.06299722273670771, 0.16235110183162804, 0.7385643769524814, 0.1391247394324615, 0.29899474587960195, 0.0068354173674941565, 0.051296739014600634, 0.9939727974659864, 0.3392666055907485, 0.5161520398486467, 0.4630120063297004, 0.8123973220979497, 0.16414781478237395, 0.8288470481208378, 0.20094922622661648, 0.49149788808804284, 0.6784911791307995, 0.7323143099011621, 0.6606946332986149, 0.727997108915455, 0.540785790803238, 0.6812748011808331, 0.6389550581839671, 0.9771947779571967, 0.25203060342622174, 0.6411178870818426, 0.49612268456770003, 0.22365598773966078, 0.6950181972728258, 0.7214979828216417, 0.10832308921349088, 0.4304854022789548, 0.9547919760969049, 0.017319398053219692, 0.8825883187835974, 0.6092345247499678, 0.18909839260061212, 0.49732604511388245, 0.02422064485315023, 0.6839346178036276, 0.6325166001311375, 0.6927638110078602, 0.5324003456830465, 0.7104429745365818, 0.0639192383560504, 0.12209767202156607, 0.7786400897813344, 0.5562353388726157, 0.2829385783301608, 0.6823146342257043, 0.22672343569739117, 0.8459732694055359, 0.37516107284599354, 0.5682058177762132, 0.077446583023568, 0.8346198656024948, 0.8465183888769366, 0.4391938403350182, 0.5699306315011783, 0.882572735046663, 0.9648038093261833, 0.0856107669978663, 0.8728048915269353, 0.3762288982716223, 0.9960227018252772, 0.3356064250055726, 0.7626399489581065, 0.26259086457911507, 0.8735690584512519, 0.24052307489146552, 0.7404758644284506, 0.8479690800481826, 0.17442067052009147, 0.1303364319897391, 0.012162105814355062]"
            );
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
            System.out.println("Bot " + x + " scored " + b1.fitness);
        }

        Collections.sort(botPool);
        for(int  i = 0; i<POOL_SIZE/2; i++) {
            System.out.println("Survivor " + (i+1) + ": "+botPool.get(i).fitness);
        }

        // The bottom half gets filled with top half mutated:
        for(int i = POOL_SIZE/2; i < POOL_SIZE; i++) {
            WeightedWithLocationBot weightedBot = botPool.get(i).bot;

            copyWeights(botPool.get(i - POOL_SIZE/2).bot, weightedBot);
            for(int f = 0; f < 3 + random.nextInt(10); f++) {
                updateRandomField(weightedBot);
            }
        }

        System.out.println("End of round");
        testBot(board, triatorium, botPool.get(0).bot);
        System.out.println(Arrays.toString(botPool.get(0).bot.getExplosionWeights()));
        System.out.println(Arrays.toString(botPool.get(0).bot.getPlacementWeights()));
        System.out.println(Arrays.toString(botPool.get(0).bot.getLocationWeights()));

    }

    private class BotHolder implements Comparable<BotHolder> {

        private long fitness = 0;
        private WeightedWithLocationBot bot;

        BotHolder(WeightedWithLocationBot bot) {
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
