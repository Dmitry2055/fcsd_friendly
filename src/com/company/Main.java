package com.company;

import com.sun.javafx.runtime.SystemProperties;
import com.sun.javafx.scene.control.skin.RadioButtonSkin;
import com.sun.javafx.scene.web.Debugger;
import jdk.nashorn.internal.runtime.logging.DebugLogger;

import java.math.BigInteger;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main{

    private static DebugLogger log;
    private static ForkJoinPool pool;

    public static void main(String[] args) {

        if (args.length != 3) {
            System.out.println("Usage: start end tasks_number");
            System.exit(1);
        }

        int start = 0;
        int end = 0;
        int depth = 0;

        try {
            start = Integer.parseInt(args[0]);
            end = Integer.parseInt(args[1]);
            depth = Integer.parseInt(args[2]);
        } catch (NumberFormatException ex){
            System.out.println("Invalid parameter(s)");
            System.exit(2);
        }

        if (start < 2 || end < start || depth < 0){
            System.out.println("Invalid parameter(s)");
            System.exit(3);
        }
        if (depth == 0){
            depth = Runtime.getRuntime().availableProcessors();
        }

//        System.out.println("Cores = " + Runtime.getRuntime().availableProcessors());

        log = new DebugLogger("Main logger", Level.INFO, false);

        log.log(Level.INFO, String.format("Numbers %d to %d, depth = %d", start, end, depth));
        System.out.format("Numbers %d to %d, depth = %d%n", start, end, depth);

        Stopwatch total = new Stopwatch("Total");
        total.start();

        FriendlyNumbers.getFriendlyNumbers(start, end, depth);

        total.stop();

        log.log(Level.INFO, total.getInfoMsg() + "\n");
        System.out.println(total.getInfoMsg());
        System.out.println();
    }
}
