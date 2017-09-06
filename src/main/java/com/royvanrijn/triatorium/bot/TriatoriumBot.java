package com.royvanrijn.triatorium.bot;

import com.royvanrijn.triatorium.ExplosionMove;
import com.royvanrijn.triatorium.PlacementMove;
import com.royvanrijn.triatorium.board.Board;
import com.royvanrijn.triatorium.board.Triangle;

public interface TriatoriumBot {

    String getName();

    PlacementMove pickMove(Board board);

    ExplosionMove evaluateExplosion(Board board, Triangle triangle);
}
