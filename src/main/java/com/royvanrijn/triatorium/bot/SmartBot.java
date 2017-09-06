package com.royvanrijn.triatorium.bot;

import com.royvanrijn.triatorium.ExplosionMove;
import com.royvanrijn.triatorium.PlacementMove;
import com.royvanrijn.triatorium.board.Board;
import com.royvanrijn.triatorium.board.CoordinateHash;
import com.royvanrijn.triatorium.board.Triangle;

import java.util.Collections;
import java.util.List;

/**
 * Assign a score to each triangle where we can place a token.
 * Move randomly when scores are equal, try to move towards the middle where possible.
 *
 * In case of explosion, favor a (shuffled/random) distribution where we have the most tokens.
 */
public class SmartBot implements TriatoriumBot {

    @Override
    public String getName() {
        return "SmartBot";
    }

    @Override
    public ExplosionMove evaluateExplosion(final int myId, final Board board, final Triangle triangle) {

        List<ExplosionMove> possibleExplosionMoves = board.generateAllExplosionMoves(triangle);

        // Shuffle:
        Collections.shuffle(possibleExplosionMoves);

        // Sort the moves: Best move is the one where *we* distribute the most:
        Collections.sort(possibleExplosionMoves, (o1, o2) ->
                (int)((o2.getTokenDistribution().stream().filter(o->o==myId).count()) -
                        (o1.getTokenDistribution().stream().filter( o-> o==myId).count()))
        );

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

        Collections.sort(placementMoves,
                (o1, o2) -> {
                    final Triangle t1 = board.get(o1.getLocationHash());
                    final Triangle t2 = board.get(o2.getLocationHash());

                    int s1 = scoreForTriangle(myId, t1);
                    int s2 = scoreForTriangle(myId, t2);

                    if(s1 != s2) {
                        return s2 - s1;
                    }

                    // If the score is equal, pick the one closest to the center:
                    int x1 = CoordinateHash.getX(t1.getLocationHash());
                    int x2 = CoordinateHash.getX(t2.getLocationHash());

                    return myId == 1 ? (x1 - x2) : -(x1 - x2);
                }
        );

        return placementMoves.get(0);
    }

    private int scoreForTriangle(final int myId, final Triangle triangle) {
        int opponents = (int) triangle.getTokens().stream().filter(o -> o != myId).count();
        int ours = triangle.getTokens().size() - opponents;

        String id = "" + opponents + "" + ours;
        switch(id) {
            case "20" : return 10;
            case "11" : return 9;
            case "10" : return 8;
            case "00" : return 7;
            case "01" : return 6;
            case "02" : return 5;
            default: return 0;
        }
    }
}
