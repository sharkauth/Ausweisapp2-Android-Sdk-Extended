package net.sharksystem.eID;

import java.util.concurrent.TimeUnit;

/**
 * Only allows offering/ adding items to the channel.
 * @param <E>
 */
public interface InChan<E> {
    boolean add(E var1);

    boolean offer(E var1);

    void put(E var1) throws InterruptedException;

    boolean offer(E var1, long var2, TimeUnit var4) throws InterruptedException;

    int remainingCapacity();

    boolean remove(Object var1);

    boolean contains(Object var1);
}
