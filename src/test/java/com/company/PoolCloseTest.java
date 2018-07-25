package com.company;

import com.company.utils.SimpleExecutorService;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static com.company.utils.TimeUtils.assertDefaultDelay;
import static com.company.utils.TimeUtils.assertNonBlockingDelay;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PoolCloseTest {

    public static final Integer TEST_VALUE_1 = 1;

    @Test
    public void shouldWaitForAllResourcesReleased() throws ExecutionException, InterruptedException {
        Pool<Integer> pool = new PoolImpl<>();
        pool.open();

        SimpleExecutorService executorService = new SimpleExecutorService();
        executorService.submitAcquireWaitReleaseTask(pool, TEST_VALUE_1);

        executorService.submit(() -> {
            long start = System.currentTimeMillis();
            pool.close();
            assertDefaultDelay(start);
        });

        executorService.awaitTermination();
    }

    @Test
    public void shouldIgnoreAcquiredResources() throws ExecutionException, InterruptedException {
        Pool<Integer> pool = new PoolImpl<>();
        pool.open();

        SimpleExecutorService executorService = new SimpleExecutorService();
        executorService.submitAcquireWaitReleaseTask(pool, TEST_VALUE_1);

        executorService.submit(() -> {
            System.out.println("start");
            long start = System.currentTimeMillis();
            assertTrue(pool.isOpen());
            pool.closeNow();
            assertNonBlockingDelay(start);
            assertFalse(pool.isOpen());
            System.out.println("end");
        });

        executorService.awaitTermination();
    }

}