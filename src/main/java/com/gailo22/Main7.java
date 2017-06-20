package com.gailo22;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.Lists;
import com.google.json.JsonSanitizer;

public class Main7 {

	private static final List<String> UPDATE_KEYS = Lists.newArrayList("a", "g");

	public static void main(String[] args) throws Exception {
		// JSONArray array = new JSONArray("[{a: 1}, {b: [2, 3]}]");
		// //JSONObject array = new JSONObject("{root: [{a: 1}, {b: [2, 3]}]}");
		// String xml = XML.toString(array);
		// xml = "<root>" + xml + "</root>";
		// System.out.println(xml);
		//
		// DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
		// DocumentBuilder b = f.newDocumentBuilder();
		// Document doc = b.parse(new
		// ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
		//
		// String json =
		// XML.toJSONObject(xml).getJSONObject("root").get("array").toString();
		// System.out.println(json);

//		JSONObject obj = new JSONObject("{a: 1, b: [{c: 2, d: 3}, {}]}");
//		String xml2 = XML.toString(obj, "root");
//		System.out.println(xml2);
//
//		String json2 = XML.toJSONObject(xml2).get("root").toString();
//		System.out.println(json2);

		String jsonDataStr = "{a: 1, b: [{a: 2, d: 3}], f: {a: 123}, g: [5, 6]}";
		//String jsonDataStr = "{g: [5, 6]}";
		String sanitizeJsonStr = JsonSanitizer.sanitize(jsonDataStr);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(sanitizeJsonStr);
		
		System.out.println(sanitizeJsonStr);
		updateJsonNode(rootNode, true);
		System.out.println(rootNode.toString());
		updateJsonNode(rootNode, false);
		System.out.println(rootNode.toString());

	}

	private static void updateJsonNode(JsonNode rootNode, boolean isEncrypt) {
		Iterator<Map.Entry<String, JsonNode>> fields = rootNode.fields();

		while (fields.hasNext()) {
			Entry<String, JsonNode> next = fields.next();
			String key = next.getKey();
			JsonNode value = next.getValue();

			if (value instanceof ArrayNode) {
				updateArrayNode(key, value, isEncrypt);
			} else if (value instanceof ObjectNode) {
				updateJsonNode(value, isEncrypt);
			} else {
				updateTextNode(next, key, isEncrypt);
			}
		}
	}

	private static void updateArrayNode(String key, JsonNode value, boolean isEncrypt) {
		ArrayNode array = (ArrayNode) value;
		for (int i = 0; i < array.size(); i++) {
			JsonNode v = array.get(i);
			if (v.isObject()) {
				updateJsonNode(v, isEncrypt);
			} else {
				updateArrayTextNode(array, i, key, isEncrypt);
			}
		}
	}

	private static void updateTextNode(Entry<String, JsonNode> nodeEntry, String key, boolean isEncrypt) {
		if (UPDATE_KEYS.contains(key)) {
			String newValue = isEncrypt? 
					encrypt(nodeEntry.getValue().asText()) :
				    decrypt(nodeEntry.getValue().asText());
			nodeEntry.setValue(new TextNode(newValue));
		}
	}
	
	private static void updateArrayTextNode(ArrayNode arrayNode, int index, String key, boolean isEncrypt) {
		if (UPDATE_KEYS.contains(key)) {
			String newValue = isEncrypt ? 
					encrypt(arrayNode.get(index).asText()) : 
					decrypt(arrayNode.get(index).asText());
			arrayNode.set(index, new TextNode(newValue));
		}
	}

	private static String encrypt(String text) {
		return text + "-enc";
	}
	
	private static String decrypt(String text) {
		return text.replace("-enc", "");
	}
	
}
