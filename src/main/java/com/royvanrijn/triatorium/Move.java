package com.royvanrijn.triatorium;

import com.royvanrijn.triatorium.board.CoordinateHash;

import java.util.Collections;
import java.util.List;

public class Move {

    private int locationHash;

    private int ownerPlayerId;
    private List<Integer> tokenDistribution;
    private List<Integer> neighboursToReceive;

    public Move(int locationHash) {
        this.locationHash = locationHash;
    }

    public Move(int locationHash, int ownerPlayerId, List<Integer> tokenDistribution, List<Integer> neighboursToReceive) {
        this.locationHash = locationHash;
        this.ownerPlayerId = ownerPlayerId;
        this.tokenDistribution = tokenDistribution;
        this.neighboursToReceive = Collections.unmodifiableList(neighboursToReceive);
    }

    public int getOwnerPlayerId() {
        return ownerPlayerId;
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
        if(getNeighboursToReceive() == null) {
            return "Placement: " + location();
        }
        return "Explosion: " + ownerPlayerId + ", " + location() + ", " + neighboursToReceive.toString() + ", " +
                tokenDistribution.toString();
    }

    private String location() {
        return "(" + CoordinateHash.getX(locationHash) +", "+CoordinateHash.getY(locationHash) + ")";
    }
}
