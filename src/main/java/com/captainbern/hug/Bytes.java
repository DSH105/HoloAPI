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

    public byte[] toByteArray(int i) {
        return new byte[] {
                (byte) ((i >> 24) & 0x0FF),
                (byte) ((i >> 16) & 0x0FF),
                (byte) ((i >> 8) & 0x0FF),
                (byte) ((i) & 0x0FF)

        };
    }

    public byte[] toByteArray(short s) {
        return new byte[] {
                (byte) ((s >> 8) & 0x0FF),
                (byte) ((s) & 0x0FF)
        };
    }

    public byte[] toByteArray(long l) {
        return new byte[] {
                (byte) ((l >> 56) & 0x0FF),
                (byte) ((l >> 48) & 0x0FF),
                (byte) ((l >> 40) & 0x0FF),
                (byte) ((l >> 32) & 0x0FF),
                (byte) ((l >> 24) & 0x0FF),
                (byte) ((l >> 16) & 0x0FF),
                (byte) ((l >> 8) & 0x0FF),
                (byte) ((l) & 0x0FF)
        };
    }

    public byte[] toByteArray(double d) {
        return toByteArray(Double.doubleToRawLongBits(d));
    }

    public byte[] toByteArray(float f) {
        return toByteArray(Float.floatToRawIntBits(f));
    }

    public int toInt(byte[] bytes) {
        return ((bytes[0] & 0x0FF) << 24) |
                ((bytes[1] & 0x0FF) << 16) |
                ((bytes[2] & 0x0FF) << 8) |
                ((bytes[3] & 0x0FF));

    }

    public int toShort(byte[] bytes) {
        return ((bytes[0] & 0x0FF) << 8) |
                (bytes[1] & 0x0FF);
    }

    public long toLong(byte[] bytes) {
        return ((bytes[0] & 0x0FF) << 56) |
                ((bytes[1] & 0x0FF) << 48) |
                ((bytes[2] & 0x0FF) << 40) |
                ((bytes[3] & 0x0FF) << 32) |
                ((bytes[4] & 0x0FF) << 24) |
                ((bytes[5] & 0x0FF) << 16) |
                ((bytes[6] & 0x0FF) << 8) |
                ((bytes[7] & 0x0FF));
    }

    public double toDouble(byte[] bytes) {
        return Double.longBitsToDouble(toLong(bytes));
    }

    public float toFloat(byte[] bytes) {
        return Float.intBitsToFloat(toInt(bytes));
    }
}
