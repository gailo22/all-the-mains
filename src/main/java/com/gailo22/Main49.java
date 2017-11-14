package com.gailo22;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Main49 {
	
	public static void main(String[] args) {
		
		CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> 1);
		CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> 2);
		
		List<CompletableFuture<Integer>> futuresList = Arrays.asList(future1, future2);
		CompletableFuture<Void> allFuturesResult = CompletableFuture.allOf(
				futuresList.toArray(new CompletableFuture[0]));
		
		CompletableFuture<List<Integer>> result = 
			allFuturesResult.thenApply(v -> futuresList.stream()
				.map(future -> future.join())
				.collect(Collectors.toList()));
		
		result.thenAccept(System.out::println); // [1, 2]
		
	}

}
