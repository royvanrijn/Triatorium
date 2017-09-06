package com.royvanrijn.triatorium.board;

import com.royvanrijn.triatorium.Move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Board {

    public static final int MAX_WIDTH = 6;

    // Smaller board for testing purposes:
    //public static final int[] SHAPE_EDGE = new int[] { 2, 1, 0, 0, 0, 1, 2 };
    public static final int[] SHAPE_EDGE = new int[] { 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2 };

    private static final int PLAYER_AMOUNT = 2;

    private Map<Integer, Triangle> boardTriangles = new HashMap<>();

    /**
     * Holds the current explosions that need to be resolved
     */
    private final List<Triangle> pendingExplosions = new ArrayList<>();

    public Board() {

        List<Integer> startP1 = Arrays.asList(
                CoordinateHash.hash(0, 2),
                CoordinateHash.hash(0, 3)
        );
        List<Integer> startP2 = Arrays.asList(
                CoordinateHash.hash(SHAPE_EDGE.length - 1, 2),
                CoordinateHash.hash(SHAPE_EDGE.length - 1, 3)
        );

        //Visit all the triangles and create a boardTriangles:
        Set<Integer> toVisit = new HashSet<>();
        toVisit.add(startP1.get(0));
        while(toVisit.size() > 0) {
            Integer coordinate = toVisit.iterator().next();
            toVisit.remove(coordinate);

            int startPosition = -1;
            if(startP1.contains(coordinate)) {
                startPosition = 0;
            } else if(startP2.contains(coordinate)) {
                startPosition = 1;
            }
            List<Integer> neighbourKeys = calculateNeighbours(coordinate);
            boardTriangles.put(coordinate, new Triangle(startPosition, neighbourKeys, coordinate));

            neighbourKeys.removeAll(boardTriangles.keySet());
            toVisit.addAll(neighbourKeys);
        }
    }

    public void reset() {
        for(Map.Entry<Integer, Triangle> boardEntry : boardTriangles.entrySet()) {
            boardEntry.getValue().reset();
        }
    }

    public int[] calculateScore() {
        int[] scores = new int[2];

        //Loop over all the triangles:
        for(Map.Entry<Integer, Triangle> boardEntry : boardTriangles.entrySet()) {
            Triangle triangle = boardEntry.getValue();
            if(!triangle.isExploded()) {
                // A score, but for whom?
                int scoreOwner = findScoreOwner(triangle, new ArrayList<>());
                scores[scoreOwner]++;
            }
        }
        return scores;
    }

    private int findScoreOwner(Triangle triangle, List<Integer> taboo) {
        if(triangle.getTokens().size() > 0) {
            return triangle.getTokens().get(0);
        } else {
            for(int neighbourHash:triangle.getNeighbourKeys()) {
                Triangle neighbour = boardTriangles.get(neighbourHash);
                if(!neighbour.isExploded() && !taboo.contains(neighbourHash)) {
                    taboo.add(neighbourHash);
                    int deeperScoreOwner = findScoreOwner(neighbour, taboo);
                    if(deeperScoreOwner != -1) {
                        return deeperScoreOwner;
                    }
                }
            }
        }
        return -1;
    }

    public void apply(int playerId, Move move) {

        Triangle triangle = boardTriangles.get(move.getLocationHash());
        if(triangle.getTokens().size() < 3) {
            if(!pendingExplosions.isEmpty()) {
                throw new IllegalArgumentException("There are pending explosions, resolve these first!");
            }

            // Placement:
            triangle.addToken(playerId);

            // If we make three, add explosion to stack:
            if(triangle.getTokens().size() == 3) {
                pendingExplosions.add(triangle);
            }

        } else {

            // Get first explosion that needs to be resolved:
            final Triangle toExplode = pendingExplosions.get(0);

            // Check this explosion is the correct triangle:
            if(!triangle.equals(toExplode)) {
                throw new IllegalArgumentException("Wrong triangle to explode, incorrect ordering?");
            }

            //Remove and explode:
            pendingExplosions.remove(0);
            triangle.explode();

            for(int ptr = 0; ptr < move.getNeighboursToReceive().size(); ptr++) {
                Triangle neighbour = boardTriangles.get(move.getNeighboursToReceive().get(ptr));
                if(neighbour.getTokens().size() == 3) {
                    throw new IllegalArgumentException("Should never happen!? Placing third!");
                }
                neighbour.addToken(move.getTokenDistribution().get(ptr));

                // Might cause another explosion:
                if(neighbour.getTokens().size() == 3) {
                    pendingExplosions.add(neighbour);
                }

            }
        }
    }

    public Triangle getNextPendingExplosion() {
        return pendingExplosions.get(0);
    }

    public boolean hasPendingExplosions() {
        return pendingExplosions.size() > 0;
    }


    public List<Move> generateAllExplosionMoves(final Triangle explodingTriangle) {
        List<Move> moves = new ArrayList<>();

        List<Integer> tokens = explodingTriangle.getTokens();

        List<Integer> neighboursToPlace = new ArrayList<>();
        for(Integer hash : explodingTriangle.getNeighbourKeys()) {
            Triangle explodeTo = boardTriangles.get(hash);
            if(!explodeTo.isExploded() && explodeTo.getTokens().size() < 3) {
                neighboursToPlace.add(hash);
            }
        }
        moves.addAll(generateExplosionMovesForTriangle(explodingTriangle.getCoordinate(), neighboursToPlace, tokens));

        return moves;
    }

    private List<Move> generateExplosionMovesForTriangle(final int hash, final List<Integer> neighboursToPlace, final List<Integer> tokens) {
        int[] colorCount = new int[PLAYER_AMOUNT];
        for(int i:tokens) {
            colorCount[i]++;
        }
        // Last placed token is owner of the explosion (and may decide the fate):
        int owner = tokens.get(tokens.size()-1);
        List<Move> moves = new ArrayList<>();
        recExplosionMoves(moves, hash, owner, neighboursToPlace, colorCount, new ArrayList());

        return moves;
    }

    /**
     * Recursive method to generate all the possible ways to explode.
     *
     * @param generatedMoves
     * @param hash
     * @param owner
     * @param neighboursToPlace
     * @param tokensLeft
     * @param tokens
     */
    private void recExplosionMoves(List<Move> generatedMoves, int hash, int owner, final List<Integer> neighboursToPlace, int[] tokensLeft, List<Integer>
            tokens) {
        if(tokens.size() == neighboursToPlace.size()) {
            Move move = new Move(hash, owner, new ArrayList<>(tokens), new ArrayList<>(neighboursToPlace));
            generatedMoves.add(move);
        } else {
            for(int tokenColor = 0; tokenColor < tokensLeft.length; tokenColor++) {
                if(tokensLeft[tokenColor] > 0) {
                    tokensLeft[tokenColor]--;
                    tokens.add(tokenColor);
                    recExplosionMoves(generatedMoves, hash, owner, neighboursToPlace, tokensLeft, tokens);
                    tokens.remove(tokens.size() - 1);
                    tokensLeft[tokenColor]++;
                }
            }
        }
    }

    public List<Move> generatePlacementMoves(int forPlayer) {
        Set<Integer> toPlace = new HashSet<>();
        for(Map.Entry<Integer, Triangle> boardEntry : boardTriangles.entrySet()) {
            Triangle triangle = boardEntry.getValue();

            if(!triangle.isExploded() && triangle.getTokens().size() < 3) {
                // Can add to current square:
                if(triangle.getStartSquare() == forPlayer) {
                    // Can add because this is our start triangle:
                    toPlace.add(boardEntry.getKey());
                } else if(triangle.getTokens().contains(forPlayer) && triangle.getTokens().size() < 3) {
                    // Can add because we already are on this triangle:
                    toPlace.add(boardEntry.getKey());
                } else {
                    // Can add because we have a neighbour with token:
                    for(Integer neighbourKey : triangle.getNeighbourKeys()) {
                        if(boardTriangles.get(neighbourKey).getTokens().contains(forPlayer)) {
                            toPlace.add(boardEntry.getKey());
                        }
                    }
                }
            }
        }

        List<Move> moves = new ArrayList<>();
        for(Integer hash:toPlace) {
            moves.add(new Move(hash));
        }
        return moves;
    }

    private List<Integer> calculateNeighbours(int hash) {
        int x = CoordinateHash.getX(hash);
        int y = CoordinateHash.getY(hash);

        List<Integer> allNeighbours = new ArrayList<>();

        // All the neighbours:
        allNeighbours.add(CoordinateHash.hash(x - 1, y));
        allNeighbours.add(CoordinateHash.hash(x + 1, y));
        if (x % 2 == y % 2) {
            allNeighbours.add(CoordinateHash.hash(x, y + 1));
        } else {
            allNeighbours.add(CoordinateHash.hash(x, y - 1));
        }

        // Check each neighbour for boundaries:
        List<Integer> validNeighbours = new ArrayList<>();
        for(Integer neighbour : allNeighbours) {

            if(CoordinateHash.getX(neighbour) < 0 || CoordinateHash.getX(neighbour) >= Board.SHAPE_EDGE.length) {
                // Filter out this point, not on the boardTriangles
                continue;
            }
            int minY = Board.SHAPE_EDGE[CoordinateHash.getX(neighbour)];
            if(CoordinateHash.getY(neighbour) < minY || CoordinateHash.getY(neighbour) >= Board.MAX_WIDTH - minY) {
                // Filter out this point, not on the boardTriangles
                continue;
            }
            validNeighbours.add(neighbour);
        }
        return validNeighbours;
    }

    public Triangle get(int hash) {
        return boardTriangles.get(hash);
    }
}
