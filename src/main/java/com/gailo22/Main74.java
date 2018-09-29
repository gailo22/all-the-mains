package com.gailo22;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

public class Main74 {

    private static final ExecutorService POOL = Executors.newSingleThreadExecutor(
        new ThreadFactoryBuilder()
            .setDaemon(true)
            .build());

    public static void main(String[] args) {
        BigInteger fib = fibCF(10);
        System.out.println(fib);
    }

    static BigInteger fibCF(int n) {
        return n <= 2 ? ONE : fibCF(n, ZERO, ONE).join();
    }

    static CompletableFuture<BigInteger> fibCF(int n, BigInteger a, BigInteger b) {
        return n <= 0 ? terminate(a) : tailCall(() -> fibCF(n - 1, b, a.add(b)));

    }

    static <T> CompletableFuture<T> tailCall(Supplier<CompletableFuture<T>> s) {
        CompletableFuture<CompletableFuture<T>> f = CompletableFuture.supplyAsync(s, POOL);
        return f.thenCompose(Function.identity());
    }

    static <T> CompletableFuture<T> terminate(T t) {
        return CompletableFuture.completedFuture(t);
    }

}
