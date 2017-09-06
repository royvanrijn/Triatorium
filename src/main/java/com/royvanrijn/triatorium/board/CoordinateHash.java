package com.royvanrijn.triatorium.board;

public class CoordinateHash {

    private static final int OFFSET = 1000;

    public static int hash(int x, int y) {
        return (x * OFFSET) + y;
    }

    public static int getX(int hash) {
        return (hash / OFFSET);
    }

    public static int getY(int hash) {
        return (hash % OFFSET);
    }
}
