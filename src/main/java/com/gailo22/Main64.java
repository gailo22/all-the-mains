package com.gailo22;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class Main64 {

    public static void main(String[] args) throws Exception {

        CompletableFuture<String> future1 = future1();
        CompletableFuture<String> future2 = future2();
        Function<String, CompletionStage<String>> function3 = x -> future3(new Request(x));

        // A -> B: Function<A, B>
        // (A, B) -> C: BiFunction<A, B, C>

        // 1. thenApply likes map
        // CompletableFuture<A> -> (A -> B) => CompletableFuture<B>

        future1.thenApply(x -> x + " world")
            .thenAccept(System.out::println);

        // 2. thenCompose likes flatMap
        // CompletableFuture<A> -> (A -> CompletableFuture<B>) -> CompletableFuture<B>

        future2.thenCompose(function3)
            .thenAccept(System.out::println);

        // 3. thenCombine like reduce
        // CompletableFuture<A> -----v
        // CompletableFuture<B> -> ((A, B) -> C) -> CompletableFuture<C>

        future1.thenCombine(future2, (h1, h2) -> h1 + ", " + h2)
            .thenAccept(System.out::println);

        // Put it all together

        future1.thenCompose(function3)
            .thenCombine(future2, (h1, h2) -> h1 + ", " + h2)
            .thenAccept(System.out::println);

        TimeUnit.SECONDS.sleep(5);
    }

    private static CompletableFuture<String> future3(Request request) {
        return CompletableFuture.supplyAsync(() -> "hello3, " + request.getName());
    }

    private static CompletableFuture<String> future2() {
        return CompletableFuture.supplyAsync(() -> "hello2");
    }

    private static CompletableFuture<String> future1() {
        CompletableFuture<String> f1 = new CompletableFuture<>();
        f1.complete("hello1");

        return f1;
    }

}

@Data
@AllArgsConstructor
class Request {
    String name;
}
