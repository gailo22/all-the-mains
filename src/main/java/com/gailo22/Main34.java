package com.gailo22;

import java.util.Arrays;

public class Main34 {
	
	static boolean[] primes = new boolean[10000];
	
	public static void sieve() {
		Arrays.fill(primes, true);
		primes[0] = false;
		primes[1] = false;
		
		int len = primes.length;
		for (int i=2; i<len; i++) {
			if (primes[i]) {
				for (int j=2; i*j<len; j++) {
					primes[i*j] = false;
				}
			}
		}
	}
	
	public static boolean isPrime(int n) {
		return primes[n];
	}
	
	public static void main(String[] args) {
		
		sieve();
		
		for (int i=0; i<16; i++) {
			System.out.println(i + " -> " + isPrime(i));
		}
	}

}
