package com.gailo22;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Main48 {

	public static void main(String[] args) {
		CompletableFuture<Integer> future = new CompletableFuture<>();
		
		System.out.println(future.isDone());
		CompletableFuture.allOf(
			    CompletableFuture.runAsync(() -> method1()),
			    CompletableFuture.runAsync(() -> method2())
			)
		.thenRun(() -> result())
		.join();
		
		System.out.println(future.isDone());
		
	}

	private static Object result() {
		System.out.println("result: " + Thread.currentThread());
		return null;
	}

	private static Object method1() {
		try {
			System.out.println("method1: " + Thread.currentThread());
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static Object method2() {
		try {
			System.out.println("method2: " + Thread.currentThread());
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

}
