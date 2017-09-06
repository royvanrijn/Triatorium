package com.royvanrijn.triatorium.bot;

import com.royvanrijn.triatorium.Move;
import com.royvanrijn.triatorium.board.Board;
import com.royvanrijn.triatorium.board.Triangle;

public interface TriatoriumBot {

    String getName();

    Move pickMove(Board board);

    Move evaluateExplosion(Board board, Triangle triangle);
}
