package com.gailo22;

import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

public class Main9 {

	static int birthdayCakeCandles(int n, int[] ar) {
		
		return 0;
	}
	
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		int n = in.nextInt();
		int[] ar = new int[n];
		for (int i = 0; i < n; i++) {
			ar[i] = in.nextInt();
		}
		
		System.out.println(Arrays.toString(ar));
		int result = birthdayCakeCandles(n, ar);
		System.out.println(result);
	}
}
