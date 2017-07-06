package com.gailo22;

import java.util.concurrent.ConcurrentHashMap;

public class Main15 {

	public static void main(String[] args) {

		ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

		map.computeIfAbsent("a", k -> k.length());
		map.computeIfAbsent("abc", String::length);
		map.computeIfAbsent("hello", String::length);

		System.out.println(map); // {a=1, abc=3, hello=5}

	}
}
