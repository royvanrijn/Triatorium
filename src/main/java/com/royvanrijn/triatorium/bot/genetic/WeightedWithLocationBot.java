package com.royvanrijn.triatorium.bot.genetic;

import com.royvanrijn.triatorium.ExplosionMove;
import com.royvanrijn.triatorium.PlacementMove;
import com.royvanrijn.triatorium.board.Board;
import com.royvanrijn.triatorium.board.CoordinateHash;
import com.royvanrijn.triatorium.board.Triangle;
import com.royvanrijn.triatorium.bot.TriatoriumBot;

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
public class WeightedWithLocationBot implements TriatoriumBot {

    private double[] explosionWeights = new double[10 * 3];
    private double[] placementWeights = new double[6 * 4];
    private double[] locationWeights = new double[Board.SHAPE_EDGE.length * Board.MAX_WIDTH];

    public WeightedWithLocationBot() {
    }

    public WeightedWithLocationBot(String inputExplosion, String inputPlacement, String inputLocation) {

        Double[] ed = Arrays.stream(inputExplosion.replace("[","").replace("]","").split(", ")).map(s -> Double.parseDouble(s)).collect(Collectors.toList()).toArray(new Double[0]);
        for(int i = 0; i< explosionWeights.length; i++) {
            explosionWeights[i] = ed[i];
        }
        Double[] pd = Arrays.stream(inputPlacement.replace("[","").replace("]","").split(", ")).map(s -> Double.parseDouble(s)).collect(Collectors.toList()).toArray(new Double[0]);
        for(int i = 0; i< placementWeights.length; i++) {
            placementWeights[i] = pd[i];
        }
        Double[] ld = Arrays.stream(inputLocation.replace("[","").replace("]","").split(", ")).map(s -> Double.parseDouble(s)).collect(Collectors.toList()).toArray(new Double[0]);
        for(int i = 0; i< locationWeights.length; i++) {
            locationWeights[i] = ld[i];
        }
    }

    public double[] getExplosionWeights() {
        return explosionWeights;
    }

    public double[] getPlacementWeights() {
        return placementWeights;
    }

    public double[] getLocationWeights() {
        return locationWeights;
    }

    @Override
    public String getName() {
        return "WeightedBotWithLocation";
    }

    @Override
    public ExplosionMove evaluateExplosion(final int myId, final Board board, final Triangle triangle) {

        List<ExplosionMove> possibleExplosionMoves = board.generateAllExplosionMoves(triangle);

        // Shuffle:
        Collections.shuffle(possibleExplosionMoves);

        // Sort the moves: Best move is the one where *we* distribute the most:
        Collections.sort(possibleExplosionMoves, (o1, o2) -> {

            final Triangle t1 = board.get(o1.getLocationHash());
            final Triangle t2 = board.get(o2.getLocationHash());

            double s1 = scoreExplosionMove(myId, o1, board);
            double s2 = scoreExplosionMove(myId, o2, board);

            if(s1 != s2) {
                return Double.compare(s1, s2);
            }
            // Consider equal?
            return 0;
        });

        return possibleExplosionMoves.get(0);
    }

    @Override
    public PlacementMove pickMove(final int myId, final Board board) {
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

                    double s1 = scorePlacementMove(myId, t1, board);
                    double s2 = scorePlacementMove(myId, t2, board);

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

    private double scorePlacementMove(final int myId, Triangle triangle, Board board) {
        double score = 0.0;

        //0-5
        score += placementWeights[tokensAsId(myId, triangle.getTokens())];
        int offset = 6;
        for(int neightbour : triangle.getNeighbourKeys()) {
            score += placementWeights[offset + tokensAsId(myId, board.get(neightbour).getTokens())];
            offset += 6;
        }

        score += locationWeights[hashToId(triangle.getLocationHash())];
        return score;
    }

    private double scoreExplosionMove(final int myId, ExplosionMove move, Board board) {

        double score = 0.0;

        int offset = 0;
        for(int i = 0; i< move.getNeighboursToReceive().size(); i++) {
            int neightbour = move.getNeighboursToReceive().get(i);
            int token = move.getTokenDistribution().get(i);

            List<Integer> tokens = new ArrayList<>(board.get(neightbour).getTokens());
            tokens.add(token);

            score += explosionWeights[offset + tokensAsId(myId, tokens)];

            offset += 10;
        }

        score += locationWeights[hashToId(move.getLocationHash())];
        return score;
    }

    private int hashToId(int locationHash) {
        return (CoordinateHash.getX(locationHash) * Board.MAX_WIDTH) + CoordinateHash.getY(locationHash);
    }


    /**
     * For explosions and placement:
     * 2 0 = 0
     * 1 1 = 1
     * 1 0 = 2
     * 0 0 = 3
     * 0 1 = 4
     * 0 2 = 5
     *
     * Only for explosions:
     * 2 1 = 6
     * 1 2 = 7
     * 3 0 = 8
     * 0 3 = 9
     *
     */
    private static final int[][] LOOKUP = new int[][] {
            {3,4,5,9},
            {2,1,7,-1},
            {0,6,-1,-1},
            {8,-1,-1,-1}
    };


    private int tokensAsId(final int myId, final List<Integer> tokens) {

        int opponents = 0;
        int ours = 0;
        for(int token:tokens) {
            if(token != myId) {
                opponents++;
            } else {
                ours++;
            }
        }
        return LOOKUP[opponents][ours];
    }
}
