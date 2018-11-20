package com.gailo22;

import java.util.concurrent.CompletableFuture;

public class Main79 {
	
	public static void main(String[] args) {

        CompletableFuture<Integer> future = new CompletableFuture<>();

        future.thenApply(it -> it + 1)
                .thenApply(it -> it * 2)
                .thenAccept(System.out::println)
                .handle((ok, ko) -> {
                    if (ko == null) return ok;
                    System.out.println(ko.getMessage());
                    return null;
                });

        future.complete(10);
        future.completeExceptionally(new RuntimeException("Error"));

    }

}
