package com.gailo22;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main14 {

	public static void main(String[] args) throws IOException {
		InputStream is1 = Main14.class.getResourceAsStream("props.properties");
		InputStream is2 = Main14.class.getClass().getResourceAsStream("/com/gailo22/props.properties");
		InputStream is3 = Main14.class.getClassLoader().getResourceAsStream("com/gailo22/props.properties");
		
		Properties prop1 = createProperties(is1);
		Properties prop2 = createProperties(is2);
		Properties prop3 = createProperties(is3);
		
		String name1 = (String) prop1.get("name");
		String name2 = (String) prop2.get("name");
		String name3 = (String) prop3.get("name");
		System.out.println(name1);
		System.out.println(name2);
		System.out.println(name3);
	}

	private static Properties createProperties(InputStream is) throws IOException {
		Properties props = new Properties();
		props.load(is);
		return props;
	}
}
