package com.gailo22;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class Main4 {
	
	public static void main(String[] args) {
		
		StringBuilder builder = new StringBuilder();
		builder.append("+" + 65);
		builder.append(4567);
		builder.append(3344);
		
		System.out.println(String.join(" ", builder));
		
		
		
		CountDownLatch sLatch = new CountDownLatch(10);
		
		ExecutorService newWorkStealingPool = Executors.newWorkStealingPool();
		
		IntStream.rangeClosed(1, 10)
		.forEach(i -> {
			newWorkStealingPool.submit(() -> {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {}
				
				System.out.println(Thread.currentThread().getName() + ": " + i);
				
				sLatch.countDown();
			});
		});
		
		try {
			sLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		newWorkStealingPool.shutdown();
		
	}

}
