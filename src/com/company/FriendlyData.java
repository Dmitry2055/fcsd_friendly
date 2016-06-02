package com.company;

/**
 * Created by dmitr on 6/2/2016.
 */
public class FriendlyData {
    private int[] the_num;
    private int[] num;
    private int[] den;

    public FriendlyData(int size){
        the_num = new int[size];
        num = new int[size];
        den = new int[size];
    }

    public int[] getThe_num(){
        return the_num;
    }

    public int[] getNum(){
        return num;
    }

    public int[] getDen(){
        return den;
    }

    public static FriendlyData mergeData(FriendlyData left, FriendlyData right){
        FriendlyData res = new FriendlyData(0);

        res.the_num = concat(left.getThe_num(), right.getThe_num());
        res.num = concat(left.getNum(), right.getNum());
        res.den = concat(left.getDen(), right.getDen());

        return res;
    }

    private static int[] concat(int[] a, int[] b) {
        int aLen = a.length;
        int bLen = b.length;
        int[] c = new int[aLen+bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }
}
