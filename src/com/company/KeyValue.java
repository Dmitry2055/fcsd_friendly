package com.company;

/**
 * Created by dmitr on 6/16/2016.
 */
public class KeyValue {
    public int left;
    public int right;

    public KeyValue(int left, int right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return String.format("%d %d", left, right);
    }
}
