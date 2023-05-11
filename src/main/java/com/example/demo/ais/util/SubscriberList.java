package com.example.demo.ais.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

public final class SubscriberList<T> {

    private static final Logger log = LoggerFactory.getLogger(SubscriberList.class);
    private final List<T> subscribers = new ArrayList<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public Subscription subscribe(T subscriber) {
        requireNonNull(subscriber, "subscriber must not be null");
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

    /**
     * Performs the given action of each subscriber in the list. Any exceptions thrown by the action are silently
     * logged.
     */
    public void forEach(Consumer<? super T> action) {
        requireNonNull(action, "action must not be null");
        List<T> subscribersToVisit;
        lock.readLock().lock();
        try {
            subscribersToVisit = List.copyOf(subscribers);
        } finally {
            lock.readLock().unlock();
        }
        subscribersToVisit.forEach(subscriber -> {
            try {
                action.accept(subscriber);
            } catch (Throwable ex) {
                log.error("Action threw an unexpected exception", ex);
            }
        });
    }
}
