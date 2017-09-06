package com.royvanrijn.triatorium.bot;

import com.royvanrijn.triatorium.Move;
import com.royvanrijn.triatorium.board.Board;
import com.royvanrijn.triatorium.board.Triangle;

import java.util.Collections;
import java.util.List;

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
    public Move evaluateExplosion(final Board board, final Triangle triangle) {

        List<Move> possibleExplosionMoves = board.generateAllExplosionMoves(triangle);

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
    public Move pickMove(final Board board) {
        List<Move> placementMoves = board.generatePlacementMoves(myId);
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
