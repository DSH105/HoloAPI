package com.captainbern.hug;

public class Bytes {

    public static byte[] merge(byte[] a, byte... b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public static byte[] rMerge(byte[] b, byte... a) {
        return merge(a, b);
    }
}
