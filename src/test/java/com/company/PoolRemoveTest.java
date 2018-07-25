package com.company;

import com.company.utils.SimpleExecutorService;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.company.utils.TimeUtils.*;
import static org.junit.Assert.*;

public class PoolRemoveTest {

    public static final Integer TEST_VALUE_1 = 1;

    @Test
    public void shouldSkipNullResource() {
        Pool<Integer> pool = new PoolImpl<>();
        pool.open();
        assertFalse(pool.remove(null));
    }

    @Test
    public void shouldWaitForAcquiredResource() throws InterruptedException, ExecutionException {
        Pool<Integer> pool = new PoolImpl<>();
        pool.open();
        pool.add(TEST_VALUE_1);
        SimpleExecutorService executorService = new SimpleExecutorService();
        executorService.submitAcquireWaitReleaseTask(pool, TEST_VALUE_1);

        executorService.submit(() -> {
            long start = System.currentTimeMillis();
            assertTrue(pool.remove(TEST_VALUE_1));
            assertDefaultDelay(start);
        });
        executorService.awaitTermination();
    }

    @Test
    public void shouldRemoveNowAcquiredResource() throws InterruptedException, ExecutionException {
        Pool<Integer> pool = new PoolImpl<>();
        pool.open();
        pool.add(TEST_VALUE_1);
        AtomicBoolean valueExists = new AtomicBoolean(false);
        SimpleExecutorService executorService = new SimpleExecutorService();
        executorService.submit(() -> {
            try {
                assertEquals(TEST_VALUE_1, pool.acquire());
                valueExists.compareAndSet(false, true);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        executorService.submit(() -> {
            long start = System.currentTimeMillis();
            assertTrue(valueExists.get());
            assertTrue(pool.removeNow(TEST_VALUE_1));
            assertNonBlockingDelay(start);
        });

        executorService.awaitTermination();
    }

    @Test
    public void shouldRemoveNowNonAcquiredResource() throws InterruptedException {
        Pool<Integer> pool = new PoolImpl<>();
        pool.open();
        pool.add(TEST_VALUE_1);
        long start = System.currentTimeMillis();
        assertTrue(pool.removeNow(TEST_VALUE_1));
        assertNonBlockingDelay(start);
    }

}