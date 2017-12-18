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
	
	static class ShowInstances {
		static Show<String> stringShow = t -> t;
		static Show<Integer> intShow = t -> String.valueOf(t);
		static Show<Person> personShow = t -> String.format("name: %s", t.getName());
	}
	
	static class Shows {
		static <T> Function<T, String> show(Show<T> show) {
			return t -> show.show(t);
		}
	}
	
	public static void main(String[] args) {
		System.out.println(Shows.show(ShowInstances.stringShow).apply("hello"));
		System.out.println(Shows.show(ShowInstances.intShow).apply(123));
		System.out.println(Shows.show(ShowInstances.personShow).apply(new Person("Jones")));
	}

}
