package com.gailo22;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

public class Main16 {

	public static void main(String[] args) {
		
		List<Person> lst = Lists.newArrayList(
				buildPerson("John", "address1", "address2"),
				buildPerson("James", "address1", "address3"),
				buildPerson("James", "address2", "address3"),
				buildPerson("Jar", "address2", "address3"),
				buildPerson("Jobs", "address3", "address4"),
				buildPerson("Jobs", "address5", "address6"),
				buildPerson("Jobs", "address7", "address8"),
				buildPerson("Joes", "address1", "address5")
				);
		
		Map<String, List<Person>> map = lst.stream()
		   .collect(Collectors.groupingBy(Person::getName, 
				   Collectors.mapping(Function.identity(), Collectors.toList())));
		
		map.forEach((k, v) -> {
			System.out.println(k +": " + v);
		});
		
		Map<String, List<String>> map2 = map.entrySet().stream()
	            .collect(Collectors.toMap(entry -> entry.getKey(), 
	            		                  entry -> entry.getValue()
	            		                                .stream()
	            		                                .flatMap(p -> p.getAddress().stream())
	            		                                .distinct()
	            		                                .sorted()
	            		                                .collect(Collectors.toList())));
		System.out.println("======================");
		map2.forEach((k, v) -> {
			System.out.println(k +": " + v);
		});
		
	}

	private static Person buildPerson(String name, String... address) {
		return new Person.Builder(name).address(Lists.newArrayList(address)).build();
	}
	
	static class Person {
		String name;
		List<String> address;
		public Person(String name, List<String> address) {
			super();
			this.name = name;
			this.address = address;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public List<String> getAddress() {
			return address;
		}
		public void setAddress(List<String> address) {
			this.address = address;
		}
		@Override
		public String toString() {
			return "Person [name=" + name + ", address=" + address + "]";
		}

		static class Builder {
			final String name; // required
			List<String> address; // optional
			Builder(String name) {
				this.name = name;
			}
			Builder address(List<String> address) {
				this.address = address;
				return this;
			}
			Person build() {
				return new Person(name, address);
			}
		}
	}
}
