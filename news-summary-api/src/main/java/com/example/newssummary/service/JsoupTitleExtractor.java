package com.example.newssummary.service;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import com.example.newssummary.config.MetaTitleProperties;

@Service
@EnableConfigurationProperties(MetaTitleProperties.class)
public class JsoupTitleExtractor implements TitleExtractor {
	
	@Autowired
	private MetaTitleProperties props;

	@Override
	public String extractTitle(String url) {
		if (url == null || url.isBlank()) return null;
		try {
			Document doc = Jsoup.connect(url)
					.userAgent(props.getUserAgent())
					.timeout(props.getTimeoutMs())
					.followRedirects(true)
					.get();
			
			String title = firstNonBlank(
					meta(doc, "meta[property=og:title]"),
					meta(doc, "meta[name=twitter:title]"),
					text(doc.selectFirst("title")),
					text(doc.selectFirst("h1"))
			);
			
			return title == null ? null : trimLen(title, props.getMaxLength());
		} catch (IOException e) {
			return null;
		}
	}
	
	private String meta(Document d, String sel) {
		Element el = d.selectFirst(sel);
		return el != null ? el.attr("content") : null;
	}
	
	private String text(Element e) {
		return e != null ? e.text() : null;
	}
	
	private String firstNonBlank(String... xs) {
		for (String x : xs) {
			if (x != null && !x.isBlank()) return x.trim();
		}
		return null;
	} 
	
	private String trimLen(String s, int max) {
		String cleaned = s.replaceAll("\\s+", " ").trim();
		return cleaned.length() > max ? cleaned.substring(0, max) + "..." : cleaned;
	}

}
