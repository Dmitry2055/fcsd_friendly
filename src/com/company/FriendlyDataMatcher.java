package com.company;

import jdk.nashorn.internal.runtime.logging.DebugLogger;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.logging.Level;

/**
 * Created by dmitr on 6/12/2016.
 */
public class FriendlyDataMatcher extends RecursiveAction {


    private static DebugLogger log;
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
    protected void compute() {

        if (depth <= 1){
            // do sequential
            match(start, end);
        }else{
            // do parallel
            int pivot = ((int) (start + (end - start)/2));
            depth--;
            FriendlyDataMatcher left = new FriendlyDataMatcher(data, start, pivot, depth);  //  left part is more expensive
            left.fork();
            FriendlyDataMatcher right = new FriendlyDataMatcher(data, pivot + 1, end, 1);
            right.compute();
            left.join();
//            FriendlyDataMatcher right = new FriendlyDataMatcher(data, pivot + 1, end, depth);
//            right.fork();
//            FriendlyDataMatcher left = new FriendlyDataMatcher(data, start, pivot, 1);  //  left part is more expensive
//            left.compute();
//            right.join();
        }

    }

    public static void findFriendlyNumbers(FriendlyData data, int depth) {

//        System.out.println("Starting matching of the friendly numbers...");
        log.log(Level.INFO, String.format("Matching friendly numbers..."));
        int length = data.length();


//        Stopwatch friendlyMatch = new Stopwatch("Friendly match");
//        friendlyMatch.start();

        ForkJoinPool.commonPool().invoke(new FriendlyDataMatcher(data, 0, length, depth));

//        friendlyMatch.stop();
//        System.out.println(friendlyMatch.getInfoMsg());

    }

    private void match(int start, int end) {

        int friendlyCount = 0;

    //    FriendlyData data = ;
        for (int i = start; i < end; i++) {
            for (int j = i + 1; j < data.length(); j++) {
                if (data.getRatio()[i] == data.getRatio()[j]){
//                    System.out.printf("%d and %d are FRIENDLY\n", data.getThe_num()[i], data.getThe_num()[j]);    //  slows down drastically
                    friendlyCount++;
                }
            }
        }
        System.out.println("Friendly count = " + friendlyCount);
    }



}
