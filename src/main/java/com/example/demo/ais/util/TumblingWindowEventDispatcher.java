package com.example.demo.ais.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

public final class TumblingWindowEventDispatcher<T> {

    private static final Logger log = LoggerFactory.getLogger(TumblingWindowEventDispatcher.class);
    private final SubscriberList<Consumer<List<T>>> subscriberList = new SubscriberList<>();

    private final List<T> window = new ArrayList<>();

    public TumblingWindowEventDispatcher(ScheduledExecutorService dispatcherExecutorService, Duration windowSize) {
        requireNonNull(dispatcherExecutorService, "dispatcherExecutorService must not be null");
        requireNonNull(windowSize, "windowSize must not be null");
        log.info("Will dispatch events every {}", windowSize);
        dispatcherExecutorService.scheduleWithFixedDelay(this::dispatchEvents, windowSize.toMillis(), windowSize.toMillis(), TimeUnit.MILLISECONDS);
    }

    public void enqueue(T event) {
        requireNonNull(event, "event must not be null");
        synchronized (window) {
            window.add(event);
        }
    }

    private void dispatchEvents() {
        List<T> eventsToDispatch;
        synchronized (window) {
            eventsToDispatch = List.copyOf(window);
            window.clear();
        }
        log.trace("Dispatching {} events", eventsToDispatch.size());
        subscriberList.forEach(subscriber -> subscriber.accept(eventsToDispatch));
    }

    public Subscription subscribe(Consumer<List<T>> subscriber) {
        return subscriberList.subscribe(subscriber);
    }
}
