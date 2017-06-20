package com.gailo22;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.chrono.ThaiBuddhistDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Main5 {
	
	public static void main(String[] args) throws IOException, ParseException {
		Path path = Paths.get("C:\\Users\\montreeb\\Desktop\\hellloworld.txt");
		Files.write("Hello world 123".getBytes(), path.toFile());
		
		System.out.println(StandardCharsets.UTF_8.toString());
		
		String format = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
		System.out.println(format);
		
		String[] split = "Resume - 10".split(" ");
		System.out.println(split[split.length - 1]);
		
//		System.out.println(Encode.forHtml("{hello}"));
		
		String str = "ZKvusSMpc+6Cfs0rZ2vVvQ==";
		String decode = URLDecoder.decode(str, StandardCharsets.UTF_8.toString());
		String encode = URLEncoder.encode(str, StandardCharsets.UTF_8.toString());
		System.out.println("decode: " + decode);
		System.out.println("encode: " + encode);
		
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Date d = f.parse("2017-05-19T14:07:18.000Z");
		System.out.println(d);
		
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssX").create();
		System.out.println(gson.toJson(d));
		
		System.out.println(Clock.systemDefaultZone());
		
		System.out.println(ThaiBuddhistDate.now());
		
		
//		XSSUtilServiceImpl xssUtilServiceImpl = new XSSUtilServiceImpl();
//		String stripXSS = xssUtilServiceImpl.stripXSS("Hello world");
//		System.out.println(stripXSS);
		
		String decode2 = java.net.URLDecoder.decode("hello+world", "UTF-8");
		System.out.println(decode2);
		
		
//		String jsonDataStr = "{a: 1, b: [{a: 2, d: 3}], f: {a: 123}}";
//		String sanitizeStr = JsonSanitizer.sanitize(jsonDataStr);
//		System.out.println(sanitizeStr);
//		
//		String jsonBody = StringEscapeUtils.unescapeHtml4("hello world + 1");
//		System.out.println(jsonBody);
//		String decode3 = java.net.URLDecoder.decode(jsonBody.replace("+", "%2B"), "UTF-8");
//		System.out.println(decode3);
//		
//		String hello = "“hello“";
//		System.out.println(StringEscapeUtils.escapeHtml4(hello));
		
		
		List<String> lst = Lists.newArrayList("a,b,c,d,e");
		String collect = lst.stream().collect(Collectors.joining(","));
		System.out.println(collect);
		
		System.out.println("my-file!!.pdf".replaceAll("[^\\w.-]", "_"));
		
		String url = "https://stageb.myskillsfuture.sg/content/portal/en/portal-search/portal-search-jobs-tab/_jcr_content/par/job-directory.search?query=rows%3d15%26sort%3dnew_posting_date%2520desc%252C%2520modified_on%2520desc%26type%3djobsVacancies%26q%3dcourseagq7x%25253cscript%25253ealert%2525281%252529%25253c%25252fscript%25253eibll6&_=1496809636367";
		
//		System.out.println(URLDecoder.decode(url, "UTF-8"));
//		System.out.println(URLDecoder.decode(URLDecoder.decode(URLDecoder.decode(url, "UTF-8"), "UTF-8"), "UTF-8"));
//		System.out.println(StringEscapeUtils.escapeHtml4(url));
//		
//		String encStr = "q3k47dVS9tePkMUs6VsG7nSzg98_b-fez3fjBsnxbFH3rsV5Vhfynvx9dtT5hYaf8mZpBjU7FNGPdFagKwJ8C1JVa5JFUSSaG3n6ezliplE=";
//		String decryptValue = DefaultCrypto.getInstance().decryptValue(encStr);
//		System.out.println(decryptValue);
//		
//		String a = "a8e4bef8-6acd-4545-9e98-323fce8f0152";
//		String encryptValue1 = DefaultCrypto.getInstance().encryptValue(a);
//		String encryptValue2 = DefaultCrypto.getInstance().encryptValue(encryptValue1);
//		
//		System.out.println("encryptValue1: " + encryptValue1);
//		System.out.println("encryptValue2: " + encryptValue2);
//		
//		System.out.println(DefaultCrypto.getInstance().decryptValue(encryptValue1));
//		System.out.println(DefaultCrypto.getInstance().decryptValue(encryptValue2));

	}

}
