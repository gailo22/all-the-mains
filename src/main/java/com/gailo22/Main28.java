package com.gailo22;

public class Main28 {
	
	public static void main(String[] args) {
		System.out.println(kadane(new int[]{1,-3,2,1,-1}));
	}

	private static int kadane(int[] arr) {
		int max = arr[0];
		int maxSoFar = max;
		for (int i=1; i<arr.length; i++) {
			maxSoFar = Math.max(arr[i], arr[i] + maxSoFar);
			max = Math.max(max, maxSoFar);
		}
		return max;
	}

}
