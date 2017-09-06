package com.royvanrijn.triatorium;

import com.royvanrijn.triatorium.board.CoordinateHash;

import java.util.Collections;
import java.util.List;

public class ExplosionMove {

    private int locationHash;

    private List<Integer> tokenDistribution;
    private List<Integer> neighboursToReceive;

    public ExplosionMove(int locationHash, List<Integer> tokenDistribution, List<Integer> neighboursToReceive) {
        this.locationHash = locationHash;
        this.tokenDistribution = tokenDistribution;
        this.neighboursToReceive = Collections.unmodifiableList(neighboursToReceive);
    }

    public int getLocationHash() {
        return locationHash;
    }

    public List<Integer> getTokenDistribution() {
        return tokenDistribution;
    }

    public List<Integer> getNeighboursToReceive() {
        return neighboursToReceive;
    }

    @Override
    public String toString() {
        return "Explosion: " + location() + ", " + neighboursToReceive.toString() + ", " +
                tokenDistribution.toString();
    }

    private String location() {
        return "(" + CoordinateHash.getX(locationHash) +", "+CoordinateHash.getY(locationHash) + ")";
    }
}
