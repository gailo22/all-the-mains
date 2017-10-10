package com.gailo22;

public class Main29 {
	
	public static void main(String[] args) {
		printRev(0, 10);
		System.out.println();
		System.out.println(fib(5));
	}
	
	static void printRev(int from, int to) {
		if (from >= to) {
			return;
		}
		
		printRev(++from, to);
		System.out.print(from + " ");
	}
	
	static int fib(int n) {
		if (n <= 1) return n;
		
		int fib2 = fib(n-2);
		int fib1 = fib(n-1);
		
		return fib2 + fib1;
	}

}
