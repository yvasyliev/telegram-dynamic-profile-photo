package com.github.yvasyliev.model;

@FunctionalInterface
public interface ThrowingRunnable extends Runnable {
    void runWithException() throws Exception;

    @Override
    default void run() {
        try {
            runWithException();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
