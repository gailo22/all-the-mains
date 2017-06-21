package com.gailo22;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main6 {

	public static void main(String[] args) {
		
		Runnable t1 = new MyTask("task1");
		Runnable t2 = new MyTask("task2");
		Runnable t3 = new MyTask("task3");
		
		ExecutorService pool = Executors.newFixedThreadPool(4);
		pool.submit(t1);
		pool.submit(t2);
		pool.submit(t3);
		
		pool.shutdown();
		
	}

}

class MyTask implements Runnable {
	private String name;
	
	MyTask(String name) {
		this.name = name;
	}

	@Override
	public void run() {
		System.out.println(name + ": " + Thread.currentThread().getName());
	}
}
