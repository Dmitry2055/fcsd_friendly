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

        if (depth <= 1){
            // do sequentially
            return match(start, end);
        }

        List<KeyValue> joinedRes = new ArrayList<>();   //  the result
        List<FriendlyDataMatcher> tasks = new ArrayList<>();    //  task pool
        int tasksNo = depth + ((int) Math.pow(end - start, POW));   //  number of tasks
        tasksNo = Math.min(tasksNo, MAX_TASKS); //  adjust the number of tasks
        int depth = (end-start) / tasksNo;  //  size of one task

        log.log(Level.INFO, String.format("Number of tasks: %d", tasksNo));


        for (int i = start; i < end-start; i += depth){
            int newStart = i;
            int newEnd = i+depth-1;
            FriendlyDataMatcher task = new FriendlyDataMatcher(data, newStart, newEnd, 1);
            task.fork();
            tasks.add(task);
        }

        for (FriendlyDataMatcher t :
                tasks) {
            joinedRes.addAll(t.join());
        }

        return joinedRes;
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
