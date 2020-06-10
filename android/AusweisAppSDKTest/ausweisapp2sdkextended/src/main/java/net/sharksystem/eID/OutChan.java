package net.sharksystem.eID;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Only allows getting queued items from the channel.
 * @param <E>
 */
public interface OutChan<E> {
    E take() throws InterruptedException;

    E poll(long var1, TimeUnit var3) throws InterruptedException;

    int remainingCapacity();

    boolean contains(Object var1);

    int drainTo(Collection<? super E> var1);

    int drainTo(Collection<? super E> var1, int var2);

    void drain();
}
