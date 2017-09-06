package com.royvanrijn.triatorium;

import com.royvanrijn.triatorium.board.Board;
import com.royvanrijn.triatorium.board.Triangle;
import com.royvanrijn.triatorium.bot.TriatoriumBot;

public class Triatorium {

    public void playGameWithBots(Board board, TriatoriumBot[] bots, int startWith) {

        int currentPlayer = startWith;

        while(board.generatePlacementMoves(currentPlayer).size() > 0) {

            //printBoard(board);

            final TriatoriumBot bot = bots[currentPlayer];

            // Bot gets to pick a move using the board:
            PlacementMove placementMove = bot.pickMove(currentPlayer, board);

            if(placementMove == null) {
                throw new IllegalArgumentException("Player didn't return move while board says it is possible.");
            }

            // Apply the move to the board:
            board.applyPlacement(currentPlayer, placementMove);

            // While there are pending explosions, handle these:
            while(board.hasPendingExplosions()) {

                 Triangle triangle = board.getNextPendingExplosion();

                 // Player that placed the last token on the triangle gets to resolve the explosion:
                 int playerToDistribute = triangle.getTokens().get(2);

                 final TriatoriumBot explosionBot = bots[playerToDistribute];
                 ExplosionMove move = explosionBot.evaluateExplosion(playerToDistribute, board, triangle);

                 board.applyExplosion(playerToDistribute, move);
             }

            // Next player:
            currentPlayer = (currentPlayer+1)%2;
        }
    }
}
