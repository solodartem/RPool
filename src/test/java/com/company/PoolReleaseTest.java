package com.company;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PoolReleaseTest {

    public static final Integer TEST_VALUE_1 = 1;

    @Test
    public void shouldReturnResourceToPool() throws InterruptedException {
        Pool<Integer> pool = new PoolImpl<>();
        pool.open();
        assertTrue(pool.add(TEST_VALUE_1));
        assertEquals(TEST_VALUE_1, pool.acquire());
        pool.release(TEST_VALUE_1);
        assertEquals(TEST_VALUE_1, pool.acquire());
    }

}