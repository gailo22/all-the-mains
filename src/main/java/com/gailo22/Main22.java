package com.gailo22;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class Main22 {
	
	public static void main(String[] args) throws IOException {

		JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("a", 1);
		
		System.out.println(jsonObj.toString());
		
		Path path = Paths.get("C:\\dev\\project\\wda\\portal\\portal-portfolio-services\\src\\main\\java\\com\\wda\\portfolio\\util\\test.json");
		
		String jsonString = new String(Files.readAllBytes(path));
		
//		Gson gson = new GsonBuilder().serializeNulls().setDateFormat("dd-MM-yyyy HH:mm a").create();
//
//		DocumentDepository docDepo = gson.fromJson(jsonString, DocumentDepository.class);
//		
//		System.out.println(docDepo);
		
		Gson gson = new GsonBuilder()
				.serializeNulls()
				.setDateFormat("dd-MM-yyyy HH:mm a")
				.create();

//		Type listOfTestimonialObject = new TypeToken<List<Testimonial>>() {
//		}.getType();
		
//		gson.fromJson(jsonString, listOfTestimonialObject);
		
		List<String> list = Lists.newArrayList("1", "2");
		
		boolean contains = list.contains("3");
		System.out.println(contains);
		
		List<Integer> ints = Stream.of(1,2,3).filter(x -> x == 4).collect(Collectors.toList());
		
		System.out.println(ints);
		
		Stream<List<Integer>> nestedInts = Stream.of(Lists.newArrayList(1), Lists.newArrayList(2, 3));
		
		List<Integer> intList = nestedInts.flatMap(x -> x.stream())
				  						  .collect(toList());
		
	
//		System.out.println(intList);
//		
//		String[] split = "a,b".split(",");
//
//		
//		for (String s : split) {
//			System.out.println(s);
//		}
//		
////		List<String> lst = Stream.of(split).map(s -> {
////			if (s.equals("a")) return null;
////			if (s.equals("b")) throw new RuntimeException("nulllll");;
////			return s;
////		})
////		.filter(Objects::nonNull)
////		.collect(Collectors.toList());
//		
////		System.out.println(lst);
//		
//		String studentJsonStr = " null ";
//		if (StringUtils.isNotEmpty(studentJsonStr)) {
//			String stJsonArray = "[" + studentJsonStr + "]";
//			JsonParser parser = new JsonParser();
//			JsonArray jsonArray = parser.parse(stJsonArray).getAsJsonArray();
//			
//			System.out.println(jsonArray.size());
//		} else {
//			System.out.println("empty");
//		}
//		
//		String ids = "[1,2]";
//		ids.split(",");
//		
//		JsonParser parser = new JsonParser();
//		JsonArray asJsonArray = parser.parse(ids).getAsJsonArray();
//		
//		String abbr = StringUtils.abbreviate("School Leader Assistant", 0, 20);
//		String abbr2 = "School Leader Assistant".substring(0, 20) + "...";
//		
//		System.out.println(abbr);
//		System.out.println(abbr2);
		
		
//		List<Integer> collect = Stream.of(Arrays.asList(1,2,3,4,5))
//				.map(Unchecked.function(x -> {
//					throwException();
//					return 0;
//				}))
//				.collect(Collectors.toList());
//		
//		System.out.println("collect: " + collect);
//		
		
		
		
		String str = "/jobs/{abc}/job-alert/{def}";
		String replaceAll = str.replaceAll("\\{.*?\\}", "(.*?)");
		System.out.println(replaceAll);
		
		
		Tuple2<String, String> tuple = Tuple.tuple("a", "b");
		System.out.println(tuple.v1);
		System.out.println(tuple.v2);
		
		System.out.println(DateTimeFormatter.ISO_DATE_TIME.toString());
		
		List<Person> people = Lists.newArrayList(new Person("John", 30), 
				                                 new Person("Jane", 20),
				                                 new Person("Jones", 20));
		
		Map<Integer, List<Person>> groupByAge = people.stream()
		                                           .collect(
		                                                Collectors.groupingBy(Person::getAge, 
		                                        		    Collectors.mapping(Function.identity(), 
		                                        				               Collectors.toList())));
		
		System.out.println(groupByAge);
		
		String filePath = "KuderServiceDeleteProfCertificationUrl=\"/KuderInd/Ind/TransitionSkills/DeleteResumeProfessionalCertification\"";
		System.out.println(filePath.substring(filePath.indexOf("=") + 2, filePath.length() - 1));
		
		JsonObject jsonObj2 = new JsonObject();
		jsonObj2.addProperty("resumeInfoId", "22222");
		jsonObj2.addProperty("resumeName", "resumename");
		
		System.out.println(jsonObj2.toString());
		
		System.out.println(Instant.now().getNano());
		
		
		System.out.println("{a}bc{d}".replace("{a}", "1").replace("{d}", "2"));
		System.out.println(String.valueOf(true));
	}
	
	static void throwRuntimeException() {
		throw new RuntimeException();
	}
	
	static void throwException() throws Exception {
		throw new Exception();
	}
	
	@SuppressWarnings("unchecked")
	private static <T, E extends Exception> T throwActualException(Exception exception) throws E {
	    throw (E) exception;
	}
	
	static class Person {
		private String name;
		private int age;
		
		Person(String name, int age) {
			this.name = name;
			this.age = age;
		}
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getAge() {
			return age;
		}
		public void setAge(int age) {
			this.age = age;
		}

		@Override
		public String toString() {
			return "Person [name=" + name + ", age=" + age + "]";
		}
	}

}
