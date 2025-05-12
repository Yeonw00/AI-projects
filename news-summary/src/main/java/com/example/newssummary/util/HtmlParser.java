package com.example.newssummary.util;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlParser {
	public static String extractArticle(String url) throws IOException {
		Document doc = Jsoup.connect(url).get();
		if (url.contains("mksports.co.kr")) {
			Elements articleDiv = doc.select("div.art_txt");
	        if (articleDiv.isEmpty()) return "";

	        StringBuilder content = new StringBuilder();
	        for (Element p : articleDiv.select("p")) {
	            content.append(p.text()).append("\n");
	        }
	        return content.toString().trim();
		} else {
			Elements paragraphs = doc.select("div.article__content p"); // 기사 본문 p 태그 기반(광고나 댓글은 거름)
			StringBuilder content = new StringBuilder();
			for (Element p : paragraphs) {
				content.append(p.text()).append(" ");
			}
			return content.toString().trim();
		}
	}
}
