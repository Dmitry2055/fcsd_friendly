package com.company;

import jdk.nashorn.internal.runtime.logging.DebugLogger;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dmitr on 5/30/2016.
 */
public class FriendlyNumbers extends RecursiveTask<FriendlyData> {

    private final int start;
    private final int end;
    private int depth;
    private static DebugLogger log;// = Logger.getLogger("FriendlyNumbers");

    static {
        log = new DebugLogger("Numbers search logger", Level.INFO, false);
    }



    public FriendlyNumbers(int start, int end, int depth){
        this.start = start;
        this.end = end;
        this.depth = depth;
    }


    @Override
    protected FriendlyData compute() {

        List<FriendlyNumbers> tasks = new ArrayList<>();

        int newEnd = end;
        FriendlyData joinedRes = new FriendlyData(0);
        int pivot;
        pivot = start + ((end-start)/depth);
        int newPivot = pivot;
        int start = this.start;

        for (int i = depth; i > 0; i--){
            if (i == 1) {
                joinedRes = friendly_numbers(start, end);
            }
            else {
                FriendlyNumbers right = new FriendlyNumbers(start, newPivot, 1);
                right.fork();
                tasks.add(right);
                start = newPivot;
                newPivot += pivot;
            }
        }

        for (FriendlyNumbers t:
                tasks) {
            joinedRes = FriendlyData.mergeData(joinedRes, t.join());
        }

//        for (int i = depth; i >= 0; i--){
//            if (depth == 1) {
//               joinedRes = joinResults(tasks);
////               return friendly_numbers(start, end);
//           } else {
//                pivot = start + ((end-start)/depth);
////               pivot = ((int) (start + (end - start)/2));
////               depth--;
//
//               FriendlyNumbers right = new FriendlyNumbers(pivot+1, end, depth);
//               right.fork();
//               tasks.add(right);
//               FriendlyNumbers left = new FriendlyNumbers(start, pivot, 1);
//               FriendlyData leftRes = left.compute();
//                start = pivot;
//
////            FriendlyData rightRes = right.join();
//
////            FriendlyData joinedRes = FriendlyData.mergeData(leftRes, rightRes);
//
//
//           }
//       }
        return  joinedRes;

    }


    //  starts the fork/join
    public static void getFriendlyNumbers(int start, int end, int depth){


        log.log(Level.INFO, String.format("start = %d end = %d depth = %d", start, end, depth));

        Stopwatch friendlyComputing = new Stopwatch("Friendly computing");
        friendlyComputing.start();
        FriendlyData data = ForkJoinPool.commonPool().invoke(new FriendlyNumbers(start, end, depth));
        friendlyComputing.stop();

        System.out.println(friendlyComputing.getInfoMsg());
        log.log(Level.INFO, friendlyComputing.getInfoMsg());


        int last = end - start + 1;



        //  implement parallel match


        Stopwatch friendlyMatch = new Stopwatch("Friendly match");
        friendlyMatch.start();

        FriendlyDataMatcher.findFriendlyNumbers(data, depth);

        friendlyMatch.stop();

        System.out.println(friendlyMatch.getInfoMsg());
        log.log(Level.INFO, friendlyMatch.getInfoMsg());
    }


    //  does the computation
    private FriendlyData friendly_numbers(int start, int end) {

//        log.log(Level.INFO, String.format("friendly_numbers(): start = %d end = %d depth = %d", start, end, depth));

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
//        log.log(Level.INFO, String.format("end of friendly_numbers(): start = %d end = %d depth = %d", start, end, depth));
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
