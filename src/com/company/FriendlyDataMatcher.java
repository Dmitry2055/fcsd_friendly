package com.company;

import jdk.nashorn.internal.runtime.logging.DebugLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Level;

/**
 * Created by dmitr on 6/12/2016.
 */
public class FriendlyDataMatcher extends RecursiveTask<List<KeyValue>> {


    private static final int SEQ_THRESHOLD = 10_000;
    private static DebugLogger log;
    private final int MAX_TASKS = 256;
    private final double POW = 0.4;
    private int friendlyCount;
    private int start;
    private int end;
    private int depth;
    private FriendlyData data;

    static {
        log = new DebugLogger("Numbers matcher logger", Level.INFO, false);
    }

    public FriendlyDataMatcher(FriendlyData data, int start, int end, int depth) {
        this.data = data;
        this.start = start;
        this.end = end;
        this.depth = depth;
    }


    @Override
    protected List<KeyValue> compute() {

        if (end-start < SEQ_THRESHOLD)
            return match(start, end);
        else {
            FriendlyDataMatcher left = new FriendlyDataMatcher(data, start, start +((int) ((end - start) / 2)) - 1, 1);
            FriendlyDataMatcher right = new FriendlyDataMatcher(data, start + ((int) ((end - start) / 2)), end, 1);
            right.fork();
            List<KeyValue> leftRes = left.compute();
            List<KeyValue> rightRes = right.join();
            leftRes.addAll(rightRes);
            return leftRes;
        }
    }

    @Deprecated
    public static void findFriendlyNumbers(FriendlyData data, int depth) {

//        System.out.println("Starting matching of the friendly numbers...");
        log.log(Level.INFO, String.format("Matching friendly numbers..."));
        int length = data.length();

        List<KeyValue> res = ForkJoinPool.commonPool().invoke(new FriendlyDataMatcher(data, 0, length, depth));
    }

    private List<KeyValue> match(int start, int end) {

        int friendlyCount = 0;
        List<KeyValue> matches = new ArrayList<>();

        for (int i = start; i < end; i++) {
            for (int j = i + 1; j < data.length(); j++) {
                if (data.getRatio()[i] == data.getRatio()[j]){
//                    System.out.printf("%d and %d are FRIENDLY\n", data.getThe_num()[i], data.getThe_num()[j]);    //  slows down drastically
                    matches.add(new KeyValue(data.getThe_num()[i], data.getThe_num()[j]));
                    friendlyCount++;
                }
            }
        }
//        System.out.println("Friendly count = " + friendlyCount);
//        System.out.println(matches);
        return matches;
    }

}
