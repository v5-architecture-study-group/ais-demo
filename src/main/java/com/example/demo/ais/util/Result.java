package com.example.demo.ais.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract sealed class Result<T> permits Result.Success, Result.Failure {

    public abstract T orElse(T defaultValue);

    public abstract <E extends Throwable> T orElseThrow(Supplier<E> throwableSupplier) throws E;

    public abstract T get();

    public abstract <E> Result<E> map(Function<T, E> mappingFunction);

    public abstract void doIfSuccessful(Consumer<T> consumer);

    public abstract boolean isSuccessful();

    public abstract String reason();

    public static <T> Result<T> success(T value) {
        return new Success<>(value);
    }

    public static <T> Result<T> failure(String reason) {
        return new Failure<>(reason);
    }

    public static <T> Result<T> failure(Throwable throwable) {
        return new Failure<>(throwable.getMessage());
    }

    public static final class Success<T> extends Result<T> {

        private final T value;

        private Success(T value) {
            this.value = value;
        }

        @Override
        public T orElse(T defaultValue) {
            return value;
        }

        @Override
        public <E extends Throwable> T orElseThrow(Supplier<E> throwableSupplier) throws E {
            return value;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public <E> Result<E> map(Function<T, E> mappingFunction) {
            try {
                return new Success<>(mappingFunction.apply(value));
            } catch (Throwable ex) {
                return new Failure<>(ex.getMessage());
            }
        }

        @Override
        public void doIfSuccessful(Consumer<T> consumer) {
            consumer.accept(value);
        }

        @Override
        public boolean isSuccessful() {
            return true;
        }

        @Override
        public String reason() {
            return "The operation was successful";
        }
    }

    public static final class Failure<T> extends Result<T> {

        private final String reason;

        private Failure(String reason) {
            this.reason = reason;
        }

        @Override
        public T orElse(T defaultValue) {
            return defaultValue;
        }

        @Override
        public <E extends Throwable> T orElseThrow(Supplier<E> throwableSupplier) throws E {
            throw throwableSupplier.get();
        }

        @Override
        public T get() {
            throw new IllegalStateException("No result");
        }

        @Override
        public <E> Result<E> map(Function<T, E> mappingFunction) {
            return new Failure<>(reason);
        }

        @Override
        public void doIfSuccessful(Consumer<T> consumer) {
            // NOP
        }

        @Override
        public boolean isSuccessful() {
            return false;
        }

        @Override
        public String reason() {
            return reason;
        }
    }
}
