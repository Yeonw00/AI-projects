package com.example.newssummary.util;

import java.io.IOException;
import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeoutException;

import org.jsoup.Jsoup;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import net.dankito.readability4j.Article;
import net.dankito.readability4j.Readability4J;

public class HtmlParser {
//	public static String extractArticle(String url) throws IOException {
//		Document doc = Jsoup.connect(url)
//				.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/120.0.0.0 Safari/537.36")
//				.get();
//		if (url.contains("mksports.co.kr")) {
//			Elements paragraphs = doc.select("div.art_txt p"); // 모든 요소 가져오기
//			System.out.println("전체 요소 수: " + paragraphs.size());
//		    StringBuilder content = new StringBuilder();
//		    for (Element p : paragraphs) {
//		        content.append(p.text()).append(" ");
//		    }
//		    return content.toString().trim();
//		} else {
//			Elements paragraphs = doc.select("div.article__content p"); // 기사 본문 p 태그 기반(광고나 댓글은 거름)
//			StringBuilder content = new StringBuilder();
//			for (Element p : paragraphs) {
//				content.append(p.text()).append(" ");
//			}
//			return content.toString().trim();
//		}
//	}
	
	public static String extractArticle(String url) throws IOException, TimeoutException {
		WebDriver driver = null;
		// chromedriver 경로 설정
	    System.setProperty("webdriver.chrome.driver", "C:\\tools\\chromedriver.exe");

	    // Headless 옵션 추가 (선택사항)
	    ChromeOptions options = new ChromeOptions();
	    options.addArguments("--headless=new"); // 최신 방식의 headless
	    options.addArguments("--disable-gpu");
	    options.addArguments("--no-sandbox");
	    options.addArguments("--disable-dev-shm-usage");
	    options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

	    try {
	    	driver = new ChromeDriver(options);
	    	driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
	    	driver.get(url);
	    	
	    	// JavaScript 렌더링 완료 후 페이지 소스를 가져옴
	    	String html = driver.getPageSource();
	    	
	    	// Readability4J 방식으로 본문 추출
	    	Readability4J readability4J = new Readability4J(url, html);
	    	Article article = readability4J.parse();
	    	
	    	if (article != null && article.getTextContent() != null) {
	            return article.getTextContent().trim();
	        } else {
	            return "본문을 추출하지 못했습니다.";
	        }
	    } catch (NoSuchElementException e) {
	        System.err.println("요소를 찾을 수 없습니다: " + e.getMessage());
	    } catch (Exception e) {
	        System.err.println("예외 발생: " + e.getMessage());
	    } finally {
	    	if(driver != null) {
	    		driver.quit();
	    	}
	    }

	    return "";
	}
}
