package com.gailo22;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Main11 {
	
	public static void main(String[] args) {
		
		Stream<? extends Map<String, List<String>>> map = idList().stream()
				.map(id -> Collections.singletonMap(id, userList()));
		
		//map.collect(Collectors.toMap(keyMapper, valueMapper));
		
//		Map<List<String>, List<String>> collect = map.collect(Collectors.toMap(id -> id, Function.identity()));
//		System.out.println(collect);
		
	}
	
	private static List<String> idList() {
		return Lists.newArrayList("1", "2", "3");
	}
	
	private static List<String> userList() {
		return Lists.newArrayList("a", "b", "c");
	}

}
