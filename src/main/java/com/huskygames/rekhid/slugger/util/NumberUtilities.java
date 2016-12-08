package com.huskygames.rekhid.slugger.util;

public class NumberUtilities {
    public static int truncate(int z, int base, int max) {
        if (z + base > max) {
            return max - z;
        } else {
            return z;
        }
    }

    public static double min(double a, double b) {
        return a > b ? b : a;
    }

    public static double max(double a, double b) {
        return a > b ? a : b;
    }

}
