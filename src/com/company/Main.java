package com.company;

import com.sun.javafx.runtime.SystemProperties;
import com.sun.javafx.scene.control.skin.RadioButtonSkin;
import com.sun.javafx.scene.web.Debugger;
import jdk.nashorn.internal.runtime.logging.DebugLogger;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
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
        int cores = 0;

        try {
            start = Integer.parseInt(args[0]);
            end = Integer.parseInt(args[1]);
            cores = Integer.parseInt(args[2]);
        } catch (NumberFormatException ex){
            System.out.println("Invalid parameter(s)");
            System.exit(2);
        }

        if (start < 2 || end < start || cores < 0){
            System.out.println("Invalid parameter(s)");
            System.exit(3);
        }
        if (cores == 0){
            cores = Runtime.getRuntime().availableProcessors();
        }

//        System.out.println("Cores = " + Runtime.getRuntime().availableProcessors());

        log = new DebugLogger("Main logger", Level.INFO, false);

//        log.log(Level.INFO, String.format("Numbers %d to %d, cores = %d", start, end, cores));
        System.out.format("Numbers %d to %d, cores = %d%n", start, end, cores);

        Stopwatch total = new Stopwatch("Total");
        total.start();

        List<KeyValue> friendlyNumbers = FriendlyNumbers.getFriendlyNumbers(start, end, cores);

        total.stop();

//        log.log(Level.INFO, total.getInfoMsg() + "\n");
        System.out.println(total.getInfoMsg());
        System.out.println();
        System.out.println("friendlyNumbers = " + friendlyNumbers);
    }
}
