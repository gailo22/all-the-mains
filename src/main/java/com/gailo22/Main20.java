package com.gailo22;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

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
		
		List list = new LinkedList();
		list.add("[");
		list.add("A");
		list.add("]");
		System.out.println(list); // [[, A, ]]
		
		ListIterator it = list.listIterator();
		while (it.hasNext()) {
			if ("[".equals(it.next()) || "]".equals(it.next())) {
				it.remove();
			} else {
				it.add("*");
			}
		}
		
		System.out.println(list); // [A]
	}
}
