package com.royvanrijn.triatorium.bot;

import com.royvanrijn.triatorium.ExplosionMove;
import com.royvanrijn.triatorium.PlacementMove;
import com.royvanrijn.triatorium.board.Board;
import com.royvanrijn.triatorium.board.Triangle;

public interface TriatoriumBot {

    String getName();

    PlacementMove pickMove(int playerId, Board board);

    ExplosionMove evaluateExplosion(int playerId, Board board, Triangle triangle);
}
