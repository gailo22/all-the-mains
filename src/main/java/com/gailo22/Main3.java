package com.gailo22;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Main3 {

	public static void main(String[] args) throws IOException {
		JsonObject readJsonFromUrl = readJsonFromUrl("http://kuderbackend:8099/kuderind/Swagger/docs/v1?api_key=dLowCJs2G48v6A==");
		
		//printEndpoints(readJsonFromUrl);
		printDefinitions(readJsonFromUrl);
	}
	
	private static void printEndpoints(JsonObject readJsonFromUrl) {
		JsonObject definitions = readJsonFromUrl.get("paths").getAsJsonObject();
		Set<Map.Entry<String, JsonElement>> entries = definitions.entrySet();
		for (Map.Entry<String, JsonElement> entry : entries) {
			String key = entry.getKey();
			System.out.println(key);
		}
	}

	private static void printDefinitions(JsonObject readJsonFromUrl) {
		JsonObject definitions = readJsonFromUrl.get("definitions").getAsJsonObject();
		Set<Map.Entry<String, JsonElement>> entries = definitions.entrySet();
		for (Map.Entry<String, JsonElement> entry : entries) {
			String key = entry.getKey();
			printModelProperties(definitions, key);
			//System.out.println();
		}
	}

	private static void printModelProperties(JsonObject definitions, String modelName) {
		System.out.println(modelName + ",,");
		JsonObject model = definitions.get(modelName).getAsJsonObject();
		JsonElement required = model.get("required");
		JsonObject properties = model.get("properties").getAsJsonObject();
		System.out.println("required:,, " + required);
		Set<Map.Entry<String, JsonElement>> entries = properties.entrySet();
		for (Map.Entry<String, JsonElement> entry : entries) {
			String key = entry.getKey();
			JsonObject valueObj = properties.get(key).getAsJsonObject();
			String type = valueObj.get("type").getAsString();
			JsonElement maxLengthElement = valueObj.get("maxLength");
			String maxLength = "";
			if (maxLengthElement != null && !maxLengthElement.isJsonNull()) {
				maxLength = maxLengthElement.getAsString();
			}
			
			System.out.println("," + key + "," + type + "," + maxLength);
		}
	}

	public static JsonObject readJsonFromUrl(String url) throws IOException {
		InputStream is = new URL(url).openStream();
		Gson gson = new Gson();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")))) {
			return gson.fromJson(reader, JsonObject.class);
		}
	}

}
