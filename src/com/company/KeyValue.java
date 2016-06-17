package com.company;

/**
 * Created by dmitr on 6/16/2016.
 */
public class KeyValue {
    public int Left;
    public int Right;

    public KeyValue(int left, int right) {
        Left = left;
        Right = right;
    }

    @Override
    public String toString() {
        return String.format("%d %d%n", Left, Right);
    }
}
