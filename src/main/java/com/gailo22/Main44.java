package com.gailo22;

import java.util.concurrent.RecursiveTask;

public class Main44 {
	
	static class FibFJTask extends RecursiveTask<Integer> {

		private static final long serialVersionUID = 1L;

		final int n;
		
		FibFJTask(int n) {
			this.n = n;
		}
		
		@Override
		protected Integer compute() {
			if (n<=1) return n;
			
			FibFJTask f1 = new FibFJTask(n-1);
			f1.fork();
			FibFJTask f2 = new FibFJTask(n-2);
			return f2.compute() + f1.join();
		}
		
	}
	
	public static void main(String[] args) {
		FibFJTask fib = new FibFJTask(10);
		System.out.println(fib.invoke()); // 55
	}

}
