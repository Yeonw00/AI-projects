package com.example.newssummary.util;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeoutException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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

	    Map<String, String> domainSelectorMap = new HashMap<>();
	    domainSelectorMap.put("n.news.naver.com", "#dic_area");
	    domainSelectorMap.put("mksports.co.kr", "div.art_txt");
	    domainSelectorMap.put("yna.co.kr", "div.story-news.article");

	    try {
	    	driver = new ChromeDriver();
	        driver.get(url);
	        
	        String matchedSelector = null;
	        for(Map.Entry<String, String> entry : domainSelectorMap.entrySet()) {
	        	if(url.contains(entry.getKey())) {
	        		matchedSelector = entry.getValue();
	        		break;
	        	}
	        }
	        
	        if(matchedSelector == null) {
	        	return "지원되지 않는 언론사 URL입니다.";
	        }
	        
	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(matchedSelector)));
	        WebElement contentDiv = driver.findElement(By.cssSelector(matchedSelector));
	        String content = contentDiv.getText();

	        return content.trim();

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
