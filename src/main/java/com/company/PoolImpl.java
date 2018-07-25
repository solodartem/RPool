package com.company;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PoolImpl<R> implements Pool<R> {

    private AtomicBoolean isOpen = new AtomicBoolean(false);

    private BlockingQueue<R> resources = new LinkedBlockingDeque<>();
    private ConcurrentHashMap<R, Lock> resourcesLocks = new ConcurrentHashMap();

    @Override
    public boolean isOpen() {
        return isOpen.get();
    }

    @Override
    public void open() {
        if (!isOpen.compareAndSet(false, true)) {
            throw new IllegalStateException();
        }
    }

    @Override
    public void close() {
        if (!isOpen.compareAndSet(true, false)) {
            throw new IllegalStateException();
        }
    }

    @Override
    public boolean add(R resource) {
        return resources.offer(resource);
    }

    @Override
    public boolean remove(R resource) {
    }

    private boolean remove(R resource, boolean waitLock) {
        synchronized (resource) {
            Lock resourceLock = resourcesLocks.remove(resource);
            if (waitLock && resourceLock != null) {
                resourceLock.lock();
            }
            return resources.remove(resource);
        }
    }

    @Override
    public R acquire() throws InterruptedException {
        if (!isOpen()) {
            return null;
        }
        return lockAndGetResource(resources.take());
    }

    @Override
    public R acquire(long timeout, TimeUnit unit) throws InterruptedException {
        if (!isOpen()) {
            return null;
        }
        return lockAndGetResource(resources.poll(timeout, unit));
    }

    private R lockAndGetResource(R resource) {
        if (resource != null) {
            ReentrantLock resourceLock = new ReentrantLock();
            resourceLock.lock();
            resourcesLocks.put(resource, resourceLock);
        }
        return resource;
    }

}
