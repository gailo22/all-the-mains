package com.gailo22;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main19 {
	
	public static void main(String[] args) {
		
		String[] strs = {"abc", "def", "ghi", "google", "  aphabet"};
		System.out.println(String.join(",", strs)); // abc,def,ghi,google,aphabet
		
		Map<String, String> map = new HashMap<>();
		map.put("a", "abc");
		map.put("d", "def");
		map.put("g", "ghi");
		
		List<String> list = new ArrayList<>();
		map.forEach((k, v) -> {
			list.add(k + "#" + v);
		});
		
		System.out.println(String.join(",", list)); // a#abc,d#def,g#ghi
		
		Map<String, List<String>> map2 = Arrays.stream(strs)
		    .filter(s -> s.length() > 0)
		    .map(s -> s.trim().toLowerCase())
		    .collect(Collectors.groupingBy(s -> String.valueOf(s.charAt(0)), 
		    		                       LinkedHashMap::new, 
		    		                       Collectors.toList()));
		
		List<String> list2 = new ArrayList<>();
		map2.forEach((k, v) -> {
			list2.add(k + "#" + String.join(",", v));
		});

		System.out.println(String.join(" , ", list2)); // a#abc,aphabet , d#def , g#ghi,google
		
	}

}
