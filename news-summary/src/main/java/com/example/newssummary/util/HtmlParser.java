package com.example.newssummary.util;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlParser {
	public static String extractArticle(String url) throws IOException {
		Document doc = Jsoup.connect(url).get();
		Elements paragraphs = doc.select("p"); // 기사 본문 p 태그 기반
		StringBuilder content = new StringBuilder();
		for (Element p : paragraphs) {
			content.append(p.text()).append(" ");
		}
		return content.toString();
	}
}
