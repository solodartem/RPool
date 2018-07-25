package com.company;

import com.company.utils.SimpleExecutorService;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.company.utils.TimeUtils.DEFAULT_THREAD_DELAY;
import static com.company.utils.TimeUtils.assertDefaultDelay;
import static org.junit.Assert.*;

public class PoolAcquireTest {


    public static final Integer TEST_VALUE_1 = 1;

    @Test()
    public void shouldGetNullOnClosedPool() throws InterruptedException {
        Pool<Integer> pool = new PoolImpl<>();
        assertTrue(pool.acquire() == null);
    }

    @Test()
    public void shouldWaitOnEmptyPool() throws ExecutionException, InterruptedException {
        Pool<Integer> pool = new PoolImpl<>();
        AtomicBoolean threadFinished = new AtomicBoolean(true);
        pool.open();
        SimpleExecutorService executorService = new SimpleExecutorService();
        executorService.submit(() -> {
            threadFinished.compareAndSet(true, false);
            try {
                Thread.sleep(DEFAULT_THREAD_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pool.add(TEST_VALUE_1);
        });
        executorService.submit(() -> {
            try {
                long start = System.currentTimeMillis();
                assertEquals(pool.acquire(), TEST_VALUE_1);
                assertFalse(threadFinished.get());
                assertDefaultDelay(start);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        executorService.awaitTermination();
    }

    @Test()
    public void shouldWaitAndFailOnEmptyPool() throws InterruptedException {
        Pool<Integer> pool = new PoolImpl<>();
        pool.open();
        long start = System.currentTimeMillis();
        assertNull(pool.acquire(DEFAULT_THREAD_DELAY, TimeUnit.MILLISECONDS));
        assertDefaultDelay(start);
    }

}