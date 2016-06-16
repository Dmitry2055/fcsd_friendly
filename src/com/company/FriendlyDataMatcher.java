package com.company;

import com.sun.org.apache.xml.internal.security.keys.content.*;
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

        List<KeyValue> res = new ArrayList<>();
        if (depth <= 1){
            // do sequential
            res = match(start, end);
        }else{
            // do parallel
            int pivot = ((int) (start + (end - start)/2));
            depth--;
            FriendlyDataMatcher left = new FriendlyDataMatcher(data, start, pivot, depth);  //  left part is more expensive
            left.fork();
            FriendlyDataMatcher right = new FriendlyDataMatcher(data, pivot + 1, end, 1);
            res = right.compute();
            res.addAll(left.join());
        }
        return res;
    }

    public static void findFriendlyNumbers(FriendlyData data, int depth) {

//        System.out.println("Starting matching of the friendly numbers...");
        log.log(Level.INFO, String.format("Matching friendly numbers..."));
        int length = data.length();


//        Stopwatch friendlyMatch = new Stopwatch("Friendly match");
//        friendlyMatch.start();

        List<KeyValue> res = ForkJoinPool.commonPool().invoke(new FriendlyDataMatcher(data, 0, length, depth));
        System.out.println("%n resul: %n");
        System.out.println(res);

//        friendlyMatch.stop();
//        System.out.println(friendlyMatch.getInfoMsg());

    }

    private List<KeyValue> match(int start, int end) {

        int friendlyCount = 0;
        List<KeyValue> numbers = new ArrayList<>();

    //    FriendlyData data = ;
        for (int i = start; i < end; i++) {
            for (int j = i + 1; j < data.length(); j++) {
                if (data.getRatio()[i] == data.getRatio()[j]){
                    System.out.printf("%d and %d are FRIENDLY\n", data.getThe_num()[i], data.getThe_num()[j]);    //  slows down drastically
                    numbers.add(new KeyValue(data.getNum()[i], data.getNum()[j]));
                    friendlyCount++;
                }
            }
        }
        System.out.println("Friendly count = " + friendlyCount);
        return numbers;
    }



}
