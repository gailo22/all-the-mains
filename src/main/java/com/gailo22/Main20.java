package com.gailo22;

import java.util.List;

import com.google.common.collect.Lists;

public class Main20 {

	private static void printReverse(List<String> lst, int i) {
		if (i >= lst.size()) {
			return;
		}
		String temp = lst.get(i);
		i++;
		printReverse(lst, i);
		System.out.println(temp);
	}
	
	public static void main(String[] args) {
		List<String> lst = Lists.newArrayList("a", "b", "c", "d");
		
		printReverse(lst, 0);
	}
}
