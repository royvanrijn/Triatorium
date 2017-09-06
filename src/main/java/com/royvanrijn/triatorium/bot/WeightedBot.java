package com.royvanrijn.triatorium.bot;

import com.royvanrijn.triatorium.ExplosionMove;
import com.royvanrijn.triatorium.PlacementMove;
import com.royvanrijn.triatorium.board.Board;
import com.royvanrijn.triatorium.board.CoordinateHash;
import com.royvanrijn.triatorium.board.Triangle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Assign a score to each triangle where we can place a token.
 * Move randomly when scores are equal, try to move towards the middle where possible.
 *
 * In case of explosion, favor a (shuffled/random) distribution where we have the most tokens.
 */
public class WeightedBot implements TriatoriumBot {


    //[0.15828277001154512, 0.023251539708203817, 0.12718029256505325, 0.2877200225690357, 0.8141642153805726, 0.9513425324173803, 0.8038578961733532, 0.5024314543238512, 0.03975031465024115, 0.3987288314662669, 0.8534603219530744, 0.429629982290337, 0.04363659971138334, 0.856351337511634, 0.7229754991230302, 0.5161952559061361, 0.7284537423276524, 0.24273873478862862, 0.6564896149378308, 0.8957721534961292, 0.5659449873286422, 0.003880367808674845, 0.15703024361471696, 0.6463996334195571]
    //[0.92442458950426, 0.6443468764092356, 0.7481876825514686, 0.3566717090540469, 9.145757879007732E-4, 0.33067157982419537, 0.14497147477473515, 0.8879987840131557, 0.6667470832812646, 0.23313551950183653, 0.4569923046861344, 0.8494092297587272, 0.5984601989857661, 0.30878281253603557, 0.019134681734079617, 0.7586272250559021, 0.16705784828345493, 0.23911429705650833, 0.6643609242251685, 0.34647269384746504, 0.4617419124560179, 0.7012647048149284, 0.9127600524427691, 0.9206362597707268, 0.08377851309762263, 0.20415020863195954, 0.1662207692441181, 0.4112365938764627, 0.4956017238131897, 0.524979774200449]

    private final int myId;

    private double[] explosionWeights = new double[10 * 3];
    private double[] placementWeights = new double[6 * 4];

    public WeightedBot(int id) {
        this.myId = id;
    }

    public WeightedBot(int id, String inputExplosion, String inputPlacement) {
        this(id);

        Double[] ed = Arrays.stream(inputExplosion.replace("[","").replace("]","").split(", ")).map(s -> Double.parseDouble(s)).collect(Collectors.toList()).toArray(new Double[0]);
        Double[] pd = Arrays.stream(inputPlacement.replace("[","").replace("]","").split(", ")).map(s -> Double.parseDouble(s)).collect(Collectors.toList()).toArray(new Double[0]);
        for(int i = 0; i< explosionWeights.length; i++) {
            explosionWeights[i] = ed[i];
        }
        for(int i = 0; i< placementWeights.length; i++) {
            placementWeights[i] = pd[i];
        }
    }

    public double[] getExplosionWeights() {
        return explosionWeights;
    }

    public double[] getPlacementWeights() {
        return placementWeights;
    }

    @Override
    public String getName() {
        return "WeightedBot";
    }

    @Override
    public ExplosionMove evaluateExplosion(final Board board, final Triangle triangle) {

        List<ExplosionMove> possibleExplosionMoves = board.generateAllExplosionMoves(triangle);

        // Shuffle:
        Collections.shuffle(possibleExplosionMoves);

        // Sort the moves: Best move is the one where *we* distribute the most:
        Collections.sort(possibleExplosionMoves, (o1, o2) -> {

            final Triangle t1 = board.get(o1.getLocationHash());
            final Triangle t2 = board.get(o2.getLocationHash());

            double s1 = scoreExplosionMove(o1, board);
            double s2 = scoreExplosionMove(o2, board);

            if(s1 != s2) {
                return Double.compare(s1, s2);
            }

            // If the score is equal, pick the one closest to the center:
            int x1 = CoordinateHash.getX(t1.getLocationHash());
            int x2 = CoordinateHash.getX(t2.getLocationHash());

            return myId == 1 ? (x1 - x2) : -(x1 - x2);
        });

        return possibleExplosionMoves.get(0);
    }

    @Override
    public PlacementMove pickMove(final Board board) {
        List<PlacementMove> placementMoves = board.generatePlacementMoves(myId);
        if(placementMoves.size() == 0) {
            return null;
        }

        // Shuffle:
        Collections.shuffle(placementMoves);

        // Sort:
        Collections.sort(placementMoves,
                (o1, o2) -> {
                    final Triangle t1 = board.get(o1.getLocationHash());
                    final Triangle t2 = board.get(o2.getLocationHash());

                    double s1 = scorePlacementMove(t1, board);
                    double s2 = scorePlacementMove(t2, board);

                    if(s1 != s2) {
                        return Double.compare(s1, s2);
                    }

                    // If the score is equal, pick the one closest to the center:
                    int x1 = CoordinateHash.getX(t1.getLocationHash());
                    int x2 = CoordinateHash.getX(t2.getLocationHash());

                    return myId == 1 ? (x1 - x2) : -(x1 - x2);
                }
        );

        return placementMoves.get(0);
    }

    private double scorePlacementMove(Triangle triangle, Board board) {
        double score = 0.0;

        //0-5
        score += placementWeights[tokensAsId(triangle.getTokens())];
        int offset = 6;
        for(int neightbour : triangle.getNeighbourKeys()) {
            score += placementWeights[offset + tokensAsId(board.get(neightbour).getTokens())];
            offset += 6;
        }
        return score;
    }

    private double scoreExplosionMove(ExplosionMove move, Board board) {

        double score = 0.0;

        int offset = 0;
        for(int i = 0; i< move.getNeighboursToReceive().size(); i++) {
            int neightbour = move.getNeighboursToReceive().get(i);
            int token = move.getTokenDistribution().get(i);

            List<Integer> tokens = new ArrayList<>(board.get(neightbour).getTokens());
            tokens.add(token);

            score += explosionWeights[offset + tokensAsId(tokens)];

            offset += 10;
        }
        return score;
    }


    private int tokensAsId(final List<Integer> tokens) {
        int opponents = (int) tokens.stream().filter(o -> o != myId).count();
        int ours = tokens.size() - opponents;

        String move = "" + opponents + "" + ours;
        switch(move) {
            case "20" : return 0;
            case "11" : return 1;
            case "10" : return 2;
            case "00" : return 3;
            case "01" : return 4;
            case "02" : return 5;
            // For explosions:
            case "21" : return 6;
            case "12" : return 7;
            case "30" : return 8;
            case "03" : return 9;
            default:    {
                throw new IllegalArgumentException("wtf: " + move);
            }
        }
    }
}
