package com.gailo22;

import java.util.HashSet;
import java.util.Set;

public class Main30 {
	
	public static void main(String[] args) {
		String str1 = "aeiou";
		String str2 = "We are students";
		
		Set<Character> h = new HashSet<>();
		for (Character c : str1.toCharArray()) {
			h.add(c);
		}
		
		StringBuilder sb = new StringBuilder();
		for (Character c : str2.toCharArray()) {
			if (!h.contains(c)) {
				sb.append(c);
			}
		}
		
		System.out.println(sb.toString());
	}

}
