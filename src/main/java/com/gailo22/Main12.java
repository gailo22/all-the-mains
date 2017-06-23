package com.gailo22;

import java.util.Arrays;

public class Main12 {

	public static void main(String[] args) {
		int[] arr = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
		
		System.out.println(Arrays.toString(arr));
		shuffle(arr);
		System.out.println(Arrays.toString(arr));
		
	}

	private static void shuffle(int[] arr) {
		int n = arr.length;
		for (int i = 0; i < n; i++) {
			int r = i + (int) (Math.random() * (n - i));
			int swap = arr[r];
			arr[r] = arr[i];
			arr[i] = swap;
		}
	}
}
