package com.company;

/**
 * Created by dmitr on 6/13/2016.
 */
public class Stopwatch {
    private long start;
    private long end;
    private long span;
    private String title;

    public Stopwatch(String title) {
        this.title = title;
    }


    void start(){
        start = System.nanoTime();
    }

    void stop(){
        end = System.nanoTime();
        span = end - start;
    }

    long getNano(){
        return span;
    }

    double getSeconds(){
        return span/1000000000.0;
    }

    String getInfoMsg(){
        return String.format("%s time: %.3f s", title, getSeconds());
    }
}
