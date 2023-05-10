package com.example.demo.ais.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public final class SubscriberList<T> {

    private static final Logger log = LoggerFactory.getLogger(SubscriberList.class);
    private final List<T> subscribers = new ArrayList<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public Subscription subscribe(T subscriber) {
        Objects.requireNonNull(subscriber, "subscriber must not be null");
        lock.writeLock().lock();
        try {
            subscribers.add(subscriber);
        } finally {
            lock.writeLock().unlock();
        }

        return () -> {
            lock.writeLock().lock();
            try {
                subscribers.remove(subscriber);
            } finally {
                lock.writeLock().unlock();
            }
        };
    }

    public void visit(Consumer<T> visitor) {
        List<T> subscribersToVisit;
        lock.readLock().lock();
        try {
            subscribersToVisit = List.copyOf(subscribers);
        } finally {
            lock.readLock().unlock();
        }
        subscribersToVisit.forEach(subscriber -> {
            try {
                visitor.accept(subscriber);
            } catch (Throwable ex) {
                log.error("Visitor threw an unexpected exception", ex);
            }
        });
    }
}
