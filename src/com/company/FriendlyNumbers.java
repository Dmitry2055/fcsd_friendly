package com.company;

import java.math.BigInteger;
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

    private Logger log = Logger.getLogger("FriendlyNumbers");

    public FriendlyNumbers(int start, int end, int depth){
        this.start = start;
        this.end = end;
        this.depth = depth;
    }

    @Override
    protected FriendlyData compute() {

//        log.log(Level.INFO, String.format("compute(): start = %d end = %d depth = %d", start, end, depth));

//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        int newEnd = end;
        if (depth <= 1) {
            return friendly_numbers(start, end);
        } else {
            int newStart = start + ((end-start)/depth);
            depth--;
//            log.log(Level.INFO, String.format("compute(): newStart = %d", newStart));

            newEnd = newStart;
            FriendlyNumbers right = new FriendlyNumbers(newStart+1, end, depth);
            right.fork();
            FriendlyNumbers left = new FriendlyNumbers(start, newEnd, 1);
            FriendlyData leftRes = left.compute();
            FriendlyData rightRes = right.join();

            FriendlyData joinedRes = FriendlyData.mergeData(leftRes, rightRes);
            return  joinedRes;
        }


//        return FriendlyData;
    }

    public static void getFriendlyNumbers(int start, int end, int depth){
        long startTime = System.nanoTime();
        FriendlyData data = ForkJoinPool.commonPool().invoke(new FriendlyNumbers(start, end, depth));

        long endTime = System.nanoTime();
        long elapsed = endTime - startTime; //  nanoseconds
        System.out.format("Time taken: %.3f s%n", elapsed/1000000000.0);


        int last = end - start + 1;
        System.out.println("Starting search for the friendly numbers...");


        int friendlyCount = 0;

        for (int i = 0; i < last; i++) {
            for (int j = i + 1; j < last; j++) {
                //if ((data.getNum()[i] == data.getNum()[j]) && (data.getNum()[i] == data.getNum()[j])) {
                if (data.getRatio()[i] == data.getRatio()[j]){
                    System.out.printf("%d and %d are FRIENDLY\n", data.getThe_num()[i], data.getThe_num()[j]);
                    friendlyCount++;
                }
            }
        }
        System.out.println("Friendly count = " + friendlyCount);

    }

    public FriendlyData friendly_numbers(int start, int end) {

//        logger.log(Level.INFO, String.format("start = %d   end = %d \n",start, end));
        log.log(Level.INFO, String.format("friendly_numbers(): start = %d end = %d depth = %d", start, end, depth));

        int last = end - start + 1;

        FriendlyData data = new FriendlyData(last);
//
//        int[] the_num = new int[last];
//        int[] num = new int[last];
//        int[] den = new int[last];

        int i, j, factor, ii, sum, done, n;

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
            }

            //  join sums here

            data.getNum()[ii] = sum;  // save the sum of factors
            data.getDen()[ii] = i;    // save the i (the number)
            data.getRatio()[ii] = ((double) sum)/i;    //  save the ratio
//            n = gcdThing(data.getNum()[ii], data.getDen()[ii]); //	n = the Greatest Common Divisor
//            data.getNum()[ii] /= n;
//            data.getDen()[ii] /= n;
        } // end for


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
