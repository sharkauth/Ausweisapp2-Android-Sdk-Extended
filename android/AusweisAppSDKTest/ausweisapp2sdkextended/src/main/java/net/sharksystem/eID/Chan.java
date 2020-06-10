package net.sharksystem.eID;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Simple buffered channel backed by an {@link BlockingQueue}.
 * @param <E>
 */
public class Chan<E> implements InChan<E>, OutChan<E> {
    private BlockingQueue<E> queue;

    public Chan(int capacity) {
        this.queue = new LinkedBlockingQueue<>(capacity);
    }

    public InChan<E> in() {
        return this;
    }

    public OutChan<E> out() {
        return this;
    }

    public void drain() {
        queue.clear();
    }

    @Override
    public boolean add(E e) {
        return queue.add(e);
    }

    @Override
    public boolean offer(E e) {
        return queue.offer(e);
    }

    @Override
    public void put(E e) throws InterruptedException {
        queue.put(e);
    }

    @Override
    public boolean offer(E e, long l, TimeUnit timeUnit) throws InterruptedException {
        return queue.offer(e, l, timeUnit);
    }

    @Override
    public E take() throws InterruptedException {
        return queue.take();
    }

    @Override
    public E poll(long l, TimeUnit timeUnit) throws InterruptedException {
        return queue.poll(l, timeUnit);
    }

    @Override
    public int remainingCapacity() {
        return queue.remainingCapacity();
    }

    @Override
    public boolean remove(Object o) {
        return queue.remove(o);
    }

    @Override
    public boolean contains(Object o) {
        return queue.contains(o);
    }

    @Override
    public int drainTo(Collection<? super E> collection) {
        return queue.drainTo(collection);
    }

    @Override
    public int drainTo(Collection<? super E> collection, int i) {
        return queue.drainTo(collection, i);
    }
}
