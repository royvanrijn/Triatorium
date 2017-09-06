package com.royvanrijn.triatorium;

import com.royvanrijn.triatorium.board.Board;
import com.royvanrijn.triatorium.bot.TriatoriumBot;
import com.royvanrijn.triatorium.bot.genetic.WeightedBot;

import java.util.Arrays;

import static com.royvanrijn.triatorium.Printer.printBoard;

public class TriatoriumRunner {

    public static void main(String[] args) {
        new TriatoriumRunner().run();
    }

    private void run() {
        Board board = new Board();

        TriatoriumBot[] bots = new TriatoriumBot[] {
                new WeightedBot(
                        "[0.548816328305037, 0.36739710078649124, 0.7765398582051127, 0.7013435012480118, 0.5260708306229479, 0.30660145088093127, 0.665348588997382, 0.4940524893871776, 0.06428687000687505, 0.8055092056422369, 0.6876236268533625, 0.5584179923460777, 0.6732218252267883, 0.5914030297348284, 0.5355293029993418, 0.41166306842929556, 0.7596551045073996, 0.4852711352625003, 0.3566979059981924, 0.2147816342766672, 0.71642160053895, 0.2321618778473583, 0.8607650673992382, 0.19646273234257428, 0.10030818400824726, 0.017168119474335275, 0.5625279821908419, 0.9557404778927265, 0.9204087683170856, 0.27329178992082515]",
                        "[0.7996708093668603, 0.09907656856606073, 0.9660228039387293, 0.6241339229918725, 0.7746403114914899, 0.46152709666480174, 0.4862049678554752, 0.8090714906383666, 0.1943735206901651, 0.39485509078899017, 0.2979856058928402, 0.5205097470799356, 0.3745063623960133, 0.2270869222673889, 0.31578050874590546, 0.5028061257061393, 0.5613898114619409, 0.717168984687564, 0.913273718717486, 0.6749927507104336, 0.017402503194211105, 0.05132283087051981, 0.41363260252346334, 0.5336343519958248]"
                ),
                new WeightedBot(
                        "[0.92442458950426, 0.6443468764092356, 0.7481876825514686, 0.3566717090540469, 9.145757879007732E-4, 0.33067157982419537, 0.14497147477473515, 0.8879987840131557, 0.6667470832812646, 0.23313551950183653, 0.4569923046861344, 0.8494092297587272, 0.5984601989857661, 0.30878281253603557, 0.019134681734079617, 0.7586272250559021, 0.16705784828345493, 0.23911429705650833, 0.6643609242251685, 0.34647269384746504, 0.4617419124560179, 0.7012647048149284, 0.9127600524427691, 0.9206362597707268, 0.08377851309762263, 0.20415020863195954, 0.1662207692441181, 0.4112365938764627, 0.4956017238131897, 0.524979774200449]",
                        "[0.15828277001154512, 0.023251539708203817, 0.12718029256505325, 0.2877200225690357, 0.8141642153805726, 0.9513425324173803, 0.8038578961733532, 0.5024314543238512, 0.03975031465024115, 0.3987288314662669, 0.8534603219530744, 0.429629982290337, 0.04363659971138334, 0.856351337511634, 0.7229754991230302, 0.5161952559061361, 0.7284537423276524, 0.24273873478862862, 0.6564896149378308, 0.8957721534961292, 0.5659449873286422, 0.003880367808674845, 0.15703024361471696, 0.6463996334195571]"
                )
        };

        Triatorium triatorium = new Triatorium();

        for(int i = 0; i<1000;i++) {
            fight(board, triatorium, bots, (i%2));
            board.reset();
        }
    }

    long w1 = 0;
    long w2 = 0;
    long t = 0;
    long p1 = 0;
    long p2 = 0;

    public void fight(Board board, Triatorium triatorium, TriatoriumBot[] bots, int startWith) {

        triatorium.playGameWithBots(board, bots, startWith);

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
}
