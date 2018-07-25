package com.company;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PoolStateTest {

    @Test
    public void shouldTreatStatus() {
        Pool<Integer> pool = new PoolImpl<>();
        assertFalse(pool.isOpen());
        pool.open();
        assertTrue(pool.isOpen());
        pool.close();
        assertFalse(pool.isOpen());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldBlockOpeningOfOpenedPool() {
        Pool<Integer> pool = new PoolImpl<>();
        pool.open();
        assertTrue(pool.isOpen());
        pool.open();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldBlockClosingOfClosedPool() {
        Pool<Integer> pool = new PoolImpl<>();
        pool.open();
        pool.close();
        assertFalse(pool.isOpen());
        pool.close();
    }
}