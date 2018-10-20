package com.gailo22;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class Main75 {

    public static void main(String[] args) {
        CompletableFuture<CompletableFuture<String>> f1 = cf1().thenApply(it -> cf1());
        Function<CompletableFuture<String>, CompletableFuture<String>> identity = Function.identity();
        CompletableFuture<String> f2 = f1.thenCompose(identity);
        CompletableFuture<Void> f3 = f2.thenAccept(System.out::println);

        f3.join();

        String s = Optional.of("a")
            .map(it -> Optional.of("b"))
            .flatMap(Function.identity())
            .orElse("");

        System.out.println(s);


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

}
