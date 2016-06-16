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
        int tasksNo = cores + ((int) Math.pow(end - start, 0.4));   //  number of tasks
        tasksNo = tasksNo > 256 ? 256 : tasksNo;  //  adjust the number of tasks
        int depth = (end-start) / tasksNo;  //  size of one task

        log.log(Level.INFO, String.format("Number of tasks: %d", tasksNo));



        for (int i = 0; i < end-start; i += depth){
            FriendlyNumbers task = new FriendlyNumbers(i, i+depth, 1);
            task.fork();
            tasks.add(task);
        }

        for (FriendlyNumbers t :
                tasks) {
            joinedRes = FriendlyData.mergeData(joinedRes, t.join());
        }

        return  joinedRes;

    }


    //  starts the fork/join
    public static void getFriendlyNumbers(int start, int end, int depth){


        log.log(Level.INFO, String.format("start = %d end = %d cores = %d", start, end, depth));

        Stopwatch friendlyComputing = new Stopwatch("Friendly computing");
        friendlyComputing.start();

        ForkJoinPool pool = new ForkJoinPool();
        FriendlyNumbers numbers = new FriendlyNumbers(start, end, depth);
        FriendlyData data = pool.invoke(numbers);


        friendlyComputing.stop();

        System.out.println(friendlyComputing.getInfoMsg());
        log.log(Level.INFO, friendlyComputing.getInfoMsg());


        int last = end - start + 1;



        //  implement parallel match


        Stopwatch friendlyMatch = new Stopwatch("Friendly match");
        friendlyMatch.start();

        FriendlyDataMatcher.findFriendlyNumbers(data, 1);

        friendlyMatch.stop();

        System.out.println(friendlyMatch.getInfoMsg());
        log.log(Level.INFO, friendlyMatch.getInfoMsg());
    }


    //  does the computation
    private FriendlyData friendly_numbers(int start, int end) {

//        log.log(Level.INFO, String.format("friendly_numbers(): start = %d end = %d cores = %d", start, end, cores));

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

            //  fork for sums here

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

//        System.out.println(String.format("friendly_numbers(): start = %d end = %d O = %d", start, end, on));
//        log.log(Level.INFO, String.format("end of friendly_numbers(): start = %d end = %d cores = %d", start, end, cores));
        return data;
//        logger.log(Level.INFO, "Searching for friendly...");

    }


    private int gcdThing(int a, int b) {
        BigInteger b1 = BigInteger.valueOf(a);
        BigInteger b2 = BigInteger.valueOf(b);
        BigInteger gcd = b1.gcd(b2);
        return gcd.intValue();
    }

}
