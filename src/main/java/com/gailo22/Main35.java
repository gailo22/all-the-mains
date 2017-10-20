package com.gailo22;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main35 {

	static void binaryString(String string) {
		for (byte b : string.getBytes()) {
			System.out.print(Integer.toBinaryString(b) + " ");
		}
		System.out.println();
	}

	static void binaryString(Integer integer) {
		System.out.println(Integer.toBinaryString(integer));
	}

	public static void main(String[] args) {
		Pattern p = Pattern.compile("[a-z]+@gmail\\.com");

		List<String> list = Arrays.asList("julia@gmail.com", "john@yahoo.com", "baby@gmail.com.sg");
		for (String s : list) {
			Matcher m = p.matcher(s);
			System.out.println(s + " ->");
			System.out.println("find: " + m.find());
			System.out.println("matches: " + m.matches());
			System.out.println("--------");
		}

		binaryString("HackerRank");
		binaryString(8675309);
	}

}
