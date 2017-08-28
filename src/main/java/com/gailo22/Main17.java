package com.gailo22;

import java.util.Arrays;
import java.util.Stack;

public class Main17 {
	
	public static void main(String[] args) {
		
//		int[][] m = new int[4][6];
//		int[][] m = {
//				{0, 0, 0, 0, 0, 0},
//				{0, 0, 0, 0, 0, 0},
//				{0, 0, 0, 0, 0, 0},
//				{0, 0, 0, 0, 0, 0},
//		};
		
		String str1 = "AGGTAB";
		String str2 = "GXTXAYB";
		System.out.println("string1: " + str1);
		System.out.println("string2: " + str2);
		int[][] m = lcs(str1, str2);
		System.out.println(Arrays.deepToString(m).replace("], ", "],\n"));
		System.out.println("LCS: " + m[str1.length()][str2.length()]);
		
		String lcsString = lcsString(m, str1);
		System.out.println("LCS string: " + lcsString);
	}

	private static String lcsString(int[][] m, String str1) {
		int rLen = m.length - 1;
		int cLen = m[0].length - 1;
		
		Stack<String> stack = new Stack<>();
		while (rLen > 0 && cLen > 0) {
			int value = m[rLen][cLen];
			if (value > 0 && changeDiagonal(m, rLen, cLen, value)) {
				stack.push(String.valueOf(str1.charAt(rLen - 1)));
				rLen--;
				cLen--;
			} else {
				if (value == m[rLen-1][cLen]) {
					rLen--;
				} else {
					cLen--;
				}
			}
		}
		
		StringBuilder builder = new StringBuilder(stack.size());
		while (!stack.isEmpty()) {
			builder.append(stack.pop() + " ");
		}
		
		return builder.toString();
	}

	private static boolean changeDiagonal(int[][] m, int rLen, int cLen, int value) {
		return value == m[rLen-1][cLen-1] + 1;
	}

	private static int[][] lcs(String str1, String str2) {
		int sLen1 = str1.length();
		int sLen2 = str2.length();
		
		int[][] m = new int[sLen1+1][sLen2+1];
		int rLen = m.length;
		int cLen = m[0].length;
		
		System.out.println(String.format("LxC: %dx%d", rLen, cLen));
		for (int c=0; c<cLen; c++) {
			m[0][c] = 0;
		}
		for (int r=0; r<rLen; r++) {
			m[r][0] = 0;
		}
		
		for (int r=1; r<rLen; r++) {
			for (int c=1; c<cLen; c++) {
				if (str1.charAt(r-1) == str2.charAt(c-1)) {
					m[r][c] = m[r-1][c-1] + 1;
				} else {
					m[r][c] = Math.max(m[r][c-1], m[r-1][c]);
				}
			}
		}
		
		return m;
	}

}
