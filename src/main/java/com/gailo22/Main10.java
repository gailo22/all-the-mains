package com.gailo22;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Main10 {
	
	private static final String GET_USER_ENDPOINT_URL = "http://192.168.152.121:9090/uam/sp-authentication";
	private static final String GET_RESUME_ENDPOINT_URL = "http://192.168.152.121:8099/KuderInd/Ind/TransitionSkills/GetResumeInfos";
//	private static final String GET_USER_ENDPOINT_URL = "http://localhost:12001/uam/sp-authentication";
//	private static final String GET_RESUME_ENDPOINT_URL = "http://localhost:28882/KuderInd/Ind/TransitionSkills/GetResumeInfos";

	public static void main(String[] args) throws IOException {
//		String userId = "a9821b5e-bc49-454d-9a23-0000da77dff8";
//		String nric = "S9217832C";
		
//		fromNRICsToUserIds();
		
		String fileLocation = "C:\\dev\\project\\wda\\portal\\portal-portfolio-services\\src\\main\\java\\com\\wda\\portfolio\\util\\users.txt";
		Path path = Paths.get(fileLocation);
		List<String> userIds = Files.readAllLines(path);
		Map<String, String> userMap = new HashMap<>();
		
		List<Map<String, List<String>>> resumesMapList = userIds.stream()
				.map(line -> {
					String[] split = line.split(",");
					String nric = split[0];
					String userId = split[1];
					userMap.put(userId, nric);
					return userId;
				})
				.map(userId -> Collections.singletonMap(userId, getResumeListByUserId(userId)))
				.collect(Collectors.toList());
		
		resumesMapList.forEach(map -> {
			String userId = map.keySet().iterator().next();
			List<String> values = map.get(userId);
			System.out.println(userMap.get(userId) + ": " + userId +": " + values.size() + " => " + values);
		});
	}

	private static void fromNRICsToUserIds() throws IOException {
		String inputFile = "C:\\dev\\project\\wda\\portal\\portal-portfolio-services\\src\\main\\java\\com\\wda\\portfolio\\util\\nrics.txt";
		String outputFile = "C:\\dev\\project\\wda\\portal\\portal-portfolio-services\\src\\main\\java\\com\\wda\\portfolio\\util\\users.txt";
		Path pathIn = Paths.get(inputFile);
		Path pathOut = Paths.get(outputFile);
		List<String> allLines = Files.readAllLines(pathIn);
		
		List<String> userIds= allLines.stream()
				                      .map(x -> x + "," + getUserIdByNric(x))
				                      //.limit(200)
				                      .collect(Collectors.toList());
		System.out.println(userIds);
		Files.write(pathOut, userIds, Charset.defaultCharset());
	}

	private static String getUserIdByNric(String nric) {
//		KuderAccessServiceImpl kuderService = new KuderAccessServiceImpl();
//		JsonObject jsonBody = new JsonObject();
//		jsonBody.addProperty("extAttribute", nric);
//		RestResult result = kuderService.doPost(GET_USER_ENDPOINT_URL, jsonBody.toString());
//		if (result != null && result.getStatus().isSuccess()) {
//			Gson gson = new GsonBuilder().create();
//			JsonObject jsonObject = gson.fromJson(result.getData(), JsonObject.class);
//			String profileEncoded = jsonObject.get("profile").getAsString();
//			String profileJson = decodeBase64ToJsonString(profileEncoded);
//			JsonObject jsonObj = new Gson().fromJson(profileJson, JsonObject.class);
//			String userId = jsonObj.get("uid").getAsString();
//			return userId;
//		}
		
		return "";
	}

	private static List<String> getResumeListByUserId(String userId) {
		List<String> userList = Lists.newArrayList();
//		KuderAccessServiceImpl kuderService = new KuderAccessServiceImpl();
//		
//		String paramsString = new KuderUrlBuilder().userId(userId).build();
//		RestResult result = kuderService.doGet(GET_RESUME_ENDPOINT_URL, paramsString);
//		
//		if (result != null && result.getStatus().isSuccess()) {
//			Gson gson = new GsonBuilder().create();
//			JsonArray jsonArray = gson.fromJson(result.getData(), JsonArray.class);
//			if (jsonArray != null) {
//				jsonArray.forEach(x -> {
//					JsonObject jsonObject = x.getAsJsonObject();
//					userList.add(jsonObject.get("resumeInfoId").getAsString());
//				});
//			}
//		}
		
		return userList;
	}
	
	private static String decodeBase64ToJsonString(String jwtString) {
        String decodedString = null;
        Base64.Decoder decoder = Base64.getDecoder();
        jwtString = jwtString.split("\\.")[1];

        byte[] decodedByteArray = decoder.decode(jwtString);
        decodedString = new String(decodedByteArray);
        return decodedString;
    }

}
