package com.royvanrijn.triatorium.bot;

import java.util.Collections;
import java.util.List;

import com.royvanrijn.triatorium.ExplosionMove;
import com.royvanrijn.triatorium.PlacementMove;
import com.royvanrijn.triatorium.board.Board;
import com.royvanrijn.triatorium.board.Triangle;

/**
 * Always try to explode the opponent, placement is random
 */
public class GreedyBot implements TriatoriumBot {

    private final int myId;

    public GreedyBot(int id) {
        this.myId = id;
    }

    @Override
    public String getName() {
        return "GreedyBot";
    }

    @Override
    public ExplosionMove evaluateExplosion(final Board board, final Triangle triangle) {

        List<ExplosionMove> possibleExplosionMoves = board.generateAllExplosionMoves(triangle);

        // Shuffle:
        Collections.shuffle(possibleExplosionMoves);

        // Sort the moves: Best move is the one where we distribute the most:
        Collections.sort(possibleExplosionMoves, (o1, o2) ->
                (int)((o2.getTokenDistribution().stream().filter(o->o==myId).count()) -
                        (o1.getTokenDistribution().stream().filter( o-> o==myId).count()))
        );

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

        Collections.sort(placementMoves,
                (o1, o2) ->
                        (int)(board.get(o2.getLocationHash()).getTokens().stream().filter(o->o!=myId).count() -
                                board.get(o1.getLocationHash()).getTokens().stream().filter(o->o!=myId).count()));

        return placementMoves.get(0);
    }
}
