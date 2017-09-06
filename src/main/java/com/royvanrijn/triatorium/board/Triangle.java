package com.royvanrijn.triatorium.board;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Triangle {

    private final int startPosition;

    private final List<Integer> tokens = new ArrayList<>();

    private final int coordinate;

    private boolean exploded = false;

    private List<Integer> neighbourKeys;

    public Triangle(int startSquare, List<Integer> neighbourKeys, int coordinate) {
        this.coordinate = coordinate;
        this.startPosition = startSquare;
        this.neighbourKeys = Collections.unmodifiableList(new ArrayList<>(neighbourKeys));
    }

    public boolean isExploded() {
        return exploded;
    }

    public int getStartSquare() {
        return startPosition;
    }

    public List<Integer> getTokens() {
        return Collections.unmodifiableList(tokens);
    }

    public void addToken(int playerId) {
        tokens.add(playerId);
    }

    public List<Integer> getNeighbourKeys() {
        return neighbourKeys;
    }

    public void explode() {
        exploded = true;
        tokens.clear();
    }

    public void reset() {
        exploded = false;
        tokens.clear();
    }

    public int getCoordinate() {
        return coordinate;
    }

    @Override
    public String toString() {
        return "[" + tokens + " s:" + startPosition + " e:" + (exploded?"y": "n") + "]";
    }
}
