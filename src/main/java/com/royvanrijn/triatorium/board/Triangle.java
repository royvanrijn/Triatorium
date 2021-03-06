package com.royvanrijn.triatorium.board;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Triangle {

    private final int startPositionForPlayer;

    private final List<Integer> tokens = new ArrayList<>();

    private final int locationHash;

    private boolean exploded = false;

    private List<Integer> neighbourKeys;

    public Triangle(int startPositionForPlayer, List<Integer> neighbourKeys, int locationHash) {
        this.locationHash = locationHash;
        this.startPositionForPlayer = startPositionForPlayer;
        this.neighbourKeys = Collections.unmodifiableList(new ArrayList<>(neighbourKeys));
    }

    public boolean isExploded() {
        return exploded;
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

    public int getLocationHash() {
        return locationHash;
    }

    @Override
    public String toString() {
        return "[" + tokens + " s:" + startPositionForPlayer + " e:" + (exploded?"y": "n") + "]";
    }

    public boolean isStartSquare(final int forPlayer) {
        return startPositionForPlayer == forPlayer;
    }
}
