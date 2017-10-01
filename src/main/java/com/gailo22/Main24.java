package com.gailo22;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Main24 {
	
	public static void main(String[] args) {
		String text = "This is the world of happiness world of you and me and you and you";
		
		Map<String, Long> map = 
				Pattern.compile("\\s+")
				    .splitAsStream(text)
		            .collect(groupingBy(identity(), 
		            		            counting()));
		
		// java 7
		List<Map.Entry<String, Long>> list = new LinkedList<>(map.entrySet());
	    Collections.sort(list, new Comparator<Map.Entry<String, Long>>() {
	        @Override
	        public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
	            return (o2.getValue()).compareTo(o1.getValue());
	        }
	    });
	    
	    // java 8
	    List<Map.Entry<String, Long>> list2 = new LinkedList<>(map.entrySet());
	    Collections.sort(list2, (o1, o2) -> {
	    	return (o2.getValue()).compareTo(o1.getValue());
	    });
	    
	    LinkedHashMap<String, Long> map2 = map.entrySet()
           .stream()
           .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
           .collect(toMap(Map.Entry::getKey, 
                          Map.Entry::getValue, 
                          (e1, e2) -> e1, 
                          LinkedHashMap::new));
		
		System.out.println(map); // {the=1, world=2, happiness=1, and=3, of=2, me=1, This=1, is=1, you=3}
		System.out.println(list); // [and=3, you=3, world=2, of=2, the=1, happiness=1, me=1, This=1, is=1]
		System.out.println(list2); // [and=3, you=3, world=2, of=2, the=1, happiness=1, me=1, This=1, is=1]
		System.out.println(map2); // [and=3, you=3, world=2, of=2, the=1, happiness=1, me=1, This=1, is=1]
	}

}
