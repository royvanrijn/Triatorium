package com.royvanrijn.triatorium;

import com.royvanrijn.triatorium.board.CoordinateHash;

public class PlacementMove {

    private int locationHash;

    public PlacementMove(int locationHash) {
        this.locationHash = locationHash;
    }

    public int getLocationHash() {
        return locationHash;
    }

    @Override
    public String toString() {
        return "Placement: " + location();
    }

    private String location() {
        return "(" + CoordinateHash.getX(locationHash) +", "+CoordinateHash.getY(locationHash) + ")";
    }
}
