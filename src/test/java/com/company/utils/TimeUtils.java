package com.company.utils;

import static org.junit.Assert.assertTrue;

public class TimeUtils {

    public static final long DEFAULT_THREAD_DELAY = 2000;
    public static final long DEFAULT_CPU_DELAY = 40;

    public static void assertDefaultDelay(long start) {
        long delta = System.currentTimeMillis() - start;
        assertTrue("Delta was: " + delta, Math.abs(delta - DEFAULT_THREAD_DELAY) < DEFAULT_CPU_DELAY);
    }

    public static void assertNonBlockingDelay(long start) {
        long delta = System.currentTimeMillis() - start;
        assertTrue("Delta was: " + delta, delta >= 0 && delta < DEFAULT_CPU_DELAY);
    }
}
