package com.gailo22;

import java.util.function.Function;

public class Main50 {

	// a -> string
	interface Show<T> {
		String show(T t);
	}
	
	static class Person {
		String name;
		
		Person(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
	
	static class StringShow implements Show<String> {
		@Override
		public String show(String t) {
			return t;
		}
	}
	
	static class IntShow implements Show<Integer> {
		@Override
		public String show(Integer t) {
			return String.valueOf(t);
		}
	}
	
	static class PersonShow implements Show<Person> {
		@Override
		public String show(Person t) {
			return String.format("name: %s", t.getName());
		}
	}
	
	static class Shows {
		static <T> Function<T, String> show(Show<T> show) {
			return t -> show.show(t);
		}
	}
	
	public static void main(String[] args) {
		System.out.println(Shows.show(new StringShow()).apply("hello"));
		System.out.println(Shows.show(new IntShow()).apply(123));
		System.out.println(Shows.show(new PersonShow()).apply(new Person("Jones")));
	}

}
