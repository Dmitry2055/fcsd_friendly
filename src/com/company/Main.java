package com.company;

import com.sun.javafx.runtime.SystemProperties;
import com.sun.javafx.scene.web.Debugger;

import java.math.BigInteger;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main{

    private static Logger logger;
    private static ForkJoinPool pool;

    public static void main(String[] args) {

        logger = Logger.getLogger("com.company");
//        pool = ForkJoinPool.commonPool();

        int start = 30;
        int end = 300;

        if (start == 0 && end == 0) {
            logger.log(Level.WARNING, "start == end == 0");
            return;
        }

        System.out.format("Number %d to %d\n", start, end);


//        long startTime = System.nanoTime();
        FriendlyNumbers.getFriendlyNumbers(start, end, 1);
//        long endTime = System.nanoTime();
//        long elapsed = endTime - startTime;
//        System.out.println("Time taken: " + elapsed);

    }
}
