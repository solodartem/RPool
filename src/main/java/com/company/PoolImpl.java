package com.company;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// TODO
// add clean method for all internal resources. I believe it is out of scope of test task :D

public class PoolImpl<R> implements Pool<R> {

    private AtomicBoolean isOpen = new AtomicBoolean(false);

    private BlockingQueue<R> resources = new LinkedBlockingDeque<>();
    private ConcurrentHashMap<R, Lock> resourcesLocks = new ConcurrentHashMap();
    private Phaser acquiredResourcesPhase = new Phaser();

    private void checkAndSetStatus(boolean isOpened) {
        if (!isOpen.compareAndSet(!isOpened, isOpened)) {
            throw new IllegalStateException();
        }
    }

    @Override
    public boolean isOpen() {
        return isOpen.get();
    }

    @Override
    public void open() {
        checkAndSetStatus(true);
        acquiredResourcesPhase.register();
    }

    @Override
    public void close() {
        acquiredResourcesPhase.arriveAndAwaitAdvance();
        checkAndSetStatus(false);
    }

    @Override
    public void closeNow() {
        checkAndSetStatus(false);
    }

    @Override
    public boolean add(R resource) {
        return resources.offer(resource);
    }

    @Override
    public boolean remove(R resource) {
        return remove(resource, true);
    }

    @Override
    public boolean removeNow(R resource) {
        return remove(resource, false);
    }

    private boolean remove(R resource, boolean waitLock) {
        if (resource == null) {
            return false;
        }
        Lock resourceLock = resourcesLocks.get(resource);
        if (waitLock && resourceLock != null) {
            resourceLock.lock();
            resourcesLocks.remove(resource);
            return resources.remove(resource);
        } else {
            return resourceLock != null || resources.remove(resource);
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

    @Override
    public void release(R resource) {
        Lock lock = resourcesLocks.get(resource);
        if (lock != null) {
            lock.unlock();
        }
        add(resource);
        acquiredResourcesPhase.arrive();
    }

    private R lockAndGetResource(R resource) {
        if (resource != null) {
            ReentrantLock resourceLock = new ReentrantLock();
            resourceLock.lock();
            resourcesLocks.put(resource, resourceLock);
            acquiredResourcesPhase.register();
        }
        return resource;
    }

}
