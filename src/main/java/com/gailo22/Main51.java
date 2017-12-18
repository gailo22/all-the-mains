package com.gailo22;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Main51 {
	
	// a -> Json
	@FunctionalInterface
	interface JsonWriter<A> {
		Json write(A a);
	}
	
	interface Json {}
	static class JsObject implements Json {
		Map<String, Json> get;
		JsObject(Map<String, Json> theGet) {
			this.get = theGet;
		}
	}
	static class JsString implements Json {
		String get;
		JsString(String theGet) {
			this.get = theGet;
		}
	}
	static class JsNumber implements Json {
		Double get;
		JsNumber(Double theGet) {
			this.get = theGet;
		}
	}
	static class JsNull implements Json {}
	
	static class Person {
		String name;
		String email;
		public Person(String name, String email) {
			super();
			this.name = name;
			this.email = email;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
	}
	
	static class JsonWriterInstances {
		static JsonWriter<String> stringWriter = a -> new JsString(a);
		static JsonWriter<Person> personWriter = a -> {
			Map<String, Json> map = new HashMap<>();
			map.put("name", new JsString(a.getName()));
			map.put("email", new JsString(a.getEmail()));
			return new JsObject(map);
		};
	}
	static class Jsons {
		public static <A> Function<A, Json> toJson(JsonWriter<A> w) {
			return a -> w.write(a);
		}
	}
	
	public static void main(String[] args) {
		System.out.println(Jsons.toJson(JsonWriterInstances.stringWriter)
				                .apply("abc"));
		System.out.println(Jsons.toJson(JsonWriterInstances.personWriter)
				                .apply(new Person("Jonus", "jonas@gmail.com")));
	}

}
