package com.gailo22;

import java.util.ArrayList;
import java.util.List;

public class Main23 {
	
	public static void main(String[] args) {

		String str = "abc";
		int n = str.length();
		List<String> list = permute(str, 0, n-1);
		System.out.println(list);
		
		List<String> list2 = permute2(str);
		System.out.println(list2);
	}
	
	private static List<String> permute2(String str) {
		List<String> results = new ArrayList<>();
		permute2("", str, results);
		return results;
	}
	
	private static void permute2(String prefix, String suffix, List<String> results) {
		if (suffix.length() == 0) {
			results.add(prefix);
		} else {
			for (int i=0; i<suffix.length(); i++) {
				permute2(prefix + suffix.charAt(i), suffix.substring(0, i) +
						suffix.substring(i+1, suffix.length()), results);
			}
		}
	}

	private static List<String> permute(String str, int l, int r) {
		List<String> results = new ArrayList<>();
		permute(str, l, r, results);
		return results;
	}
	
	private static void permute(String str, int l, int r, List<String> results) {
		
		if (l == r) {
			results.add(str);
		} else {
			for (int i=l; i<=r; i++) {
				str = swap(str, l, i);
				permute(str, l+1, r, results);
				str = swap(str, l, i);
			}
		}
	}

	private static String swap(String str, int i, int j) {
		char[] charArray = str.toCharArray();
		char temp = charArray[i];
		charArray[i] = charArray[j];
		charArray[j] = temp;
		return String.valueOf(charArray);
	}

}
