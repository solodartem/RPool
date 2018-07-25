package com.company.utils;

import com.company.Pool;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.company.utils.TimeUtils.DEFAULT_THREAD_DELAY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class SimpleExecutorService {

    private ExecutorService executors = Executors.newFixedThreadPool(10);
    private List<Future> futures = new LinkedList<>();

    public void submit(Runnable task) {
        futures.add(executors.submit(task));
    }

    public void awaitTermination() throws ExecutionException, InterruptedException {
        for (Future future : futures) {
            future.get();
        }
    }


    public void submitAcquireWaitReleaseTask(Pool pool, Object value) {
        this.submit(() -> {
            assertTrue(pool.add(value));
            try {
                assertEquals(value, pool.acquire());
                Thread.sleep(DEFAULT_THREAD_DELAY);
                pool.release(value);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
