package org.jlleitschuh.sandbox;

import java.util.Random;
import java.util.stream.LongStream;

public class CloudstackRandomGenerator {
    public static String generateToken(long time, int length) {
        String charset = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rand = new Random(time);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int pos = rand.nextInt(charset.length());
            sb.append(charset.charAt(pos));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        LongStream
            .rangeClosed(startTime + 0, startTime + (long) (3_600_000))
            .parallel()
            .mapToObj(time -> generateToken(time, 10))
            .forEach(System.out::println);
    }
}
