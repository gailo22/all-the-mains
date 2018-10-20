package com.gailo22;

import java.util.concurrent.*;

public class Main76 {

    private static ScheduledExecutorService delayer = Executors.newScheduledThreadPool(2);

    public static void main(String[] args) {
        CompletableFuture<Void> f = cf1()
            .acceptEither(
                timeoutAfter(3, TimeUnit.SECONDS),
                s -> System.out.println(s))
            .exceptionally(throwable -> {
                shutdown();
                return null;
            });

        f.join();

        shutdown();
    }

    private static void shutdown() {
        delayer.shutdown();
    }


    private static CompletableFuture<String> cf1() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
            }
            return "1";
        });
    }


    private static <T> CompletableFuture<T> timeoutAfter(long timeout, TimeUnit unit) {
        CompletableFuture<T> result = new CompletableFuture<T>();
        delayer.schedule(() -> result.completeExceptionally(new TimeoutException()), timeout, unit);
        return result;
    }

}
