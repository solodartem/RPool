package com.company;

import java.util.concurrent.TimeUnit;

public interface Pool<R> {

    boolean isOpen();

    void open();

    void close();

    void closeNow();

    boolean add(R resource);

    boolean remove(R resource);

    boolean removeNow(R resource);

    R acquire() throws InterruptedException;

    R acquire(long timeout, TimeUnit unit) throws InterruptedException;

    void release(R resource);
}
