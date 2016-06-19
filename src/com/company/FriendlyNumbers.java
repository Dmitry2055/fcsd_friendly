package com.company;

import jdk.nashorn.internal.runtime.logging.DebugLogger;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Level;

/**
 * Created by dmitr on 5/30/2016.
 */
public class FriendlyNumbers extends RecursiveTask<FriendlyData> {

    private final int start;
    private final int end;
    private final int MAX_TASKS = 256;
    private final double POW = 0.4;
    private int cores;
    private static DebugLogger log;

    static {
        log = new DebugLogger("Numbers search logger", Level.INFO, false);
    }



    public FriendlyNumbers(int start, int end, int cores){
        this.start = start;
        this.end = end;
        this.cores = cores;
    }


    @Override
    protected FriendlyData compute() {

        if (cores == 1)
            return friendly_numbers(start, end);

        FriendlyData joinedRes = new FriendlyData(0);   //  the result
        List<FriendlyNumbers> tasks = new ArrayList<>();    //  task pool
        int tasksNo = cores + ((int) Math.pow(end - start, POW));   //  number of tasks
        tasksNo = Math.min(tasksNo, MAX_TASKS); //  adjust the number of tasks
        int chunkSize = (end-start) / tasksNo;  //  size of one task

//        log.log(Level.INFO, String.format("Number of tasks: %d", tasksNo));
        System.out.format("Number of tasks: %d", tasksNo);


//  fork tasks
        for (int i = start; i < end-start; i += chunkSize){
            int newStart = i;
            int newEnd = Math.min(i+chunkSize-1, end-start);
            FriendlyNumbers task = new FriendlyNumbers(newStart, newEnd, 1);
            task.fork();
            tasks.add(task);
        }
//  join tasks
        for (FriendlyNumbers t :
                tasks) {
            joinedRes = FriendlyData.mergeData(joinedRes, t.join());
        }

        return  joinedRes;
    }


    //  starts the fork/join
    public static List<KeyValue> getFriendlyNumbers(int start, int end, int cores){

//  compute
//        log.log(Level.INFO, String.format("start = %d end = %d cores = %d", start, end, cores));
        System.out.format("start = %d end = %d cores = %d", start, end, cores);
        ForkJoinPool pool = new ForkJoinPool();
        FriendlyNumbers numbers = new FriendlyNumbers(start, end, cores);

        Stopwatch friendlyComputing = new Stopwatch("Friendly computing");
        friendlyComputing.start();

        FriendlyData data = pool.invoke(numbers);

        friendlyComputing.stop();
        System.out.println(friendlyComputing.getInfoMsg());
//        log.log(Level.INFO, friendlyComputing.getInfoMsg());

//  match
        Stopwatch friendlyMatch = new Stopwatch("Friendly match");
        friendlyMatch.start();

        FriendlyDataMatcher matcher = new FriendlyDataMatcher(data, 0, data.length(), cores);
        List<KeyValue> matchedData = pool.invoke(matcher);

        friendlyMatch.stop();
        System.out.println(friendlyMatch.getInfoMsg());
//        log.log(Level.INFO, friendlyMatch.getInfoMsg());

        return matchedData;
    }


    //  does the computation
    private FriendlyData friendly_numbers(int start, int end) {

        int last = end - start + 1;
        FriendlyData data = new FriendlyData(last);
        int i, j, factor, ii, sum, done, n;
        long on = 0;

        for (i = start; i <= end; i++) {
            ii = i - start;     //	ii = quantity of numbers between start and i
            sum = 1 + i;        //	always can be divided by 1 and itself
            data.getThe_num()[ii] = i;    //	save the current number (?)
            done = i;           // upper border
            factor = 2;         // divisor

            while (factor < done) {
                if ((i % factor) == 0) {                //	if i can be divided by factor
//                    sum += (factor + (i / factor));     //	add the divisor (factor) to the sum
                    sum += factor;     //	add the divisor (factor) to the sum

//                    if ((done = i / factor) == factor){ //	if i == factor^2 (why?)
//                        sum -= factor;
//                    }
                }
                factor++;   //	try the next number as a divider
                on++;
            }

            //  join sums here

            data.getNum()[ii] = sum;  // save the sum of factors
            data.getDen()[ii] = i;    // save the i (the number)
            data.getRatio()[ii] = ((double) sum)/i;    //  save the ratio
        } // end for

        return data;
    }


    private int gcdThing(int a, int b) {
        BigInteger b1 = BigInteger.valueOf(a);
        BigInteger b2 = BigInteger.valueOf(b);
        BigInteger gcd = b1.gcd(b2);
        return gcd.intValue();
    }

}
