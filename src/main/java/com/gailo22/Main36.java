package com.gailo22;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Main36 {
	
	static class Person {
		String name;
		int age;
		
		public Person(String name, int age) {
			this.name = name;
			this.age = age;
		}

		@Override
		public String toString() {
			return String.format("%s: %d", this.name, this.age);
		}
	}
	
	public static void main(String[] args) {
		
		int[] ar = { 4, 3, 5, 1, 2};
		
		List<Integer> list = Arrays.stream(ar)
			.boxed()
			.sorted(Comparator.reverseOrder())
			.collect(Collectors.toList());
		
		Integer[] array = Arrays.stream(ar)
			.boxed()
			.sorted(Comparator.reverseOrder())
			.toArray(Integer[]::new);
		
		Person[] people = { new Person("A", 10), 
							new Person("B", 5), 
							new Person("C", 8), 
							new Person("D", 20), 
							new Person("E", 15) 
						};
		
		List<Person> peopleList = Arrays.stream(people)
			.sorted((a, b) -> b.age - a.age)
			.collect(Collectors.toList());
		
		System.out.println(list);
		System.out.println(Arrays.toString(array));
		System.out.println(peopleList);
		
	}

}
