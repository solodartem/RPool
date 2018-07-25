package com.company;

import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PoolAcquireTest {

    public static final long DEFAULT_THEAD_DELAY = 1000; // 1 second

    public static final Integer TEST_VAALUE_1 = 1;

    @Test()
    public void shouldGetNulOnClosedPool() throws InterruptedException {
        Pool<Integer> pool = new PoolImpl<>();
        assertTrue(pool.acquire() == null);
    }

    @Test()
    public void shouldWaitOnEmptyPool() throws InterruptedException {
        Pool<Integer> pool = new PoolImpl<>();
        AtomicBoolean threadFinished = new AtomicBoolean(false);
        pool.open();
        new Thread(() -> {
            try {
                Thread.sleep(DEFAULT_THEAD_DELAY);
                threadFinished.compareAndSet(false, true);
                pool.add(TEST_VAALUE_1);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).run();
        assertEquals(pool.acquire(), TEST_VAALUE_1);
        assertTrue(threadFinished.get());
    }

    @Test()
    public void shouldWaitAndFailOnEmptyPool() throws InterruptedException {
        Pool<Integer> pool = new PoolImpl<>();
        pool.open();
        long start = System.currentTimeMillis();
        pool.acquire(DEFAULT_THEAD_DELAY, TimeUnit.MILLISECONDS);
        System.out.println(System.currentTimeMillis() - start);
        assertTrue(System.currentTimeMillis() - start - DEFAULT_THEAD_DELAY < 40);
    }


}