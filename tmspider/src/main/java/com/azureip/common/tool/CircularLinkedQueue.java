package com.azureip.common.tool;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 循环链表实现
 *
 * @param <T>
 */
public class CircularLinkedQueue<T> {
    private ConcurrentLinkedQueue<T> queue;
    private Iterator<T> iterator;

    public CircularLinkedQueue() {
        queue = new ConcurrentLinkedQueue<>();
        iterator = queue.iterator();
    }

    public CircularLinkedQueue(Collection<? extends T> c) {
        queue = new ConcurrentLinkedQueue<>(c);
        iterator = queue.iterator();
    }

    public CircularLinkedQueue<T> add(T t) {
        queue.add(t);
        return this;
    }

    public T next() {
        if (!iterator.hasNext()) {
            iterator = queue.iterator();
        }
        return iterator.next();
    }
}