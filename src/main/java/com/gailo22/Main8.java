package com.gailo22;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Main8 {

	public static void main(String[] args) throws IOException {
		
		String ADD_USER_URL = "http://localhost:28881/KuderInd/Ind/UserAccess/SaveUserId";
		String ADD_USERS_URL = "http://localhost:28881/KuderInd/Ind/UserAccess/SaveUserIds";
		
		Path path = Paths.get("C:\\Users\\montreeb\\Desktop\\projects\\ePortfolio\\20170603\\stageb-individualid.txt\\stageb-individualid - Copy.txt");
		
		List<String> readAllLines = Files.readAllLines(path);
		List<List<String>> partition = Lists.partition(readAllLines, 100);
		
//		KuderAccessServiceImpl kuderService = new KuderAccessServiceImpl();
		AtomicInteger counter = new AtomicInteger(0);
		
//		String paramsString = new KuderUrlBuilder().build();
//		System.out.println(ADD_USER_URL);
//		
//		System.out.println("size: " + partition.size());
//		int index = 5;
//		for (String part : partition.get(index)) {
//			String jsonBody2 = toJsonBody(part).toString();
//			RestResult result2 = kuderService.doPost(ADD_USER_URL, paramsString, jsonBody2);
//			int incrementAndGet = counter.incrementAndGet();
//			System.out.print(incrementAndGet + " ");
//			System.out.println(result2.getStatus().getMessage());
//		}
		
		partition.forEach(part -> {
			String jsonBody = toJsonBodyList(part);
			System.out.println(jsonBody);
			
//			String paramsString = new KuderUrlBuilder().build();
//			System.out.println(ADD_USERS_URL);
//			RestResult result = kuderService.doPost(ADD_USERS_URL, paramsString, jsonBody);
			
//			String message = result.getStatus().getMessage();
//			System.out.println(message);
//			
//			if (message.contains("Connection reset")) System.exit(0);
			
			// if not success insert one by one
//			if (result == null || result.getStatus().isSuccess() == false) {
//				part.forEach(p -> {
//					String jsonBody2 = toJsonBody(p).toString();
//					RestResult result2 = kuderService.doPost(ADD_USER_URL, paramsString, jsonBody2);
//					int incrementAndGet = counter.incrementAndGet();
//					System.out.print(incrementAndGet + " ");
//					System.out.println(result2.getStatus().getMessage());
//					
////					if (incrementAndGet >= 1000) {
////						// slow down a bit
////						System.exit(0);
////					}
//				});
//			}
			
		});
		
	}

	private static String toJsonBodyList(List<String> partition) {
		JsonArray array = new JsonArray();
		partition.forEach(userId -> {
			JsonObject jsonObj = toJsonBody(userId);
			array.add(jsonObj);
		});
		return array.toString();
	}

	private static JsonObject toJsonBody(String userId) {
		JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("UserId", userId);
		return jsonObj;
	}
}
