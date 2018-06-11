package com.gailo22;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class Main62 {
	
    static ExecutorService executorService = Executors.newFixedThreadPool(5);

	public static void main(String[] args) {

        long startTime = System.nanoTime();
        List<Supplier<CompletableFuture<Integer>>> supplierList =
            Arrays.asList(() -> task1(), () -> task2(), () -> task3(), () -> task4(), () -> task5());

        List<CompletableFuture<Integer>> completableFutures = supplierList.stream()
            .map(Supplier::get)
            .collect(Collectors.toList());

        System.out.println("-----");

        sequence(completableFutures)
            .thenAccept(System.out::println).join();

        long endTime = System.nanoTime();
        System.out.println("time: " + TimeUnit.SECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS) + " secs");

	}

    private static Integer time(Op<Integer> operation) {
        long startTime = System.nanoTime();
        Integer result = operation.runOp();
        long endTime = System.nanoTime();
        double elapsedSeconds = TimeUnit.SECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS);
        System.out.printf("Elapsed time: %.3f seconds.%n", elapsedSeconds);
        return result;
    }

    static<T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> com) {
        return CompletableFuture.allOf(com.toArray(new CompletableFuture[0]))
            .thenApply(v -> com.stream()
                .map(CompletableFuture::join)
                .collect(toList())
            );
    }


	private static CompletableFuture<Integer> task1() {
	    return CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) { }
            System.out.println(1 + " : " + Thread.currentThread().getName());
            return 1;
        }, executorService);
    }
	private static CompletableFuture<Integer> task2() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(4);
            } catch (InterruptedException e) { }
            System.out.println(2 + " : " + Thread.currentThread().getName());
            return 2;
        }, executorService);
    }
	private static CompletableFuture<Integer> task3() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) { }
            System.out.println(3 + " : " + Thread.currentThread().getName());
            return 3;
        }, executorService);
    }
	private static CompletableFuture<Integer> task4() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) { }
            System.out.println(4 + " : " + Thread.currentThread().getName());
            return 4;
        }, executorService);
    }
	private static CompletableFuture<Integer> task5() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) { }
            System.out.println(5 + " : " + Thread.currentThread().getName());
            return 5;
        }, executorService);
    }

}
