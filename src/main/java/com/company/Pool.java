package com.company;

import java.util.concurrent.TimeUnit;

public interface Pool<R> {

    boolean isOpen();

    void open();

    void close();

    boolean add(R resource);

    boolean remove(R resource);

    R acquire() throws InterruptedException;

    R acquire(long timeout, TimeUnit unit) throws InterruptedException;
}
