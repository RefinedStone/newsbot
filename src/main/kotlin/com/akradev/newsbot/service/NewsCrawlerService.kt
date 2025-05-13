package com.akradev.newsbot.service

import com.akradev.newsbot.controller.NewsCategory
import com.akradev.newsbot.controller.NewsController
import com.akradev.newsbot.controller.NewsItem
import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class NewsCrawlerService {

    fun fetchTitles(category: NewsCategory): List<NewsItem> {
        val driver = createDriver()
        val url = buildUrl(category)

        return try {
            driver.get(url)
            val elements = waitForTitleElements(driver)
            extractNewsItems(elements, category)
        } finally {
            driver.quit()
        }
    }

    private fun createDriver(): WebDriver {
        WebDriverManager.chromedriver().setup()
        val options = ChromeOptions().apply {
            addArguments("--headless=new", "--disable-gpu")
        }
        return ChromeDriver(options)
    }

    private fun buildUrl(category: NewsCategory): String {
        return "https://news.naver.com/main/ranking/popularDay.naver?mid=etc&sid1=${category.sid1}"
    }

    private fun waitForTitleElements(driver: WebDriver): List<WebElement> {
        val wait = WebDriverWait(driver, Duration.ofSeconds(10))
        wait.until(
            ExpectedConditions.presenceOfElementLocated(By.cssSelector(".list_content > a.list_title"))
        )
        return driver.findElements(By.cssSelector(".list_content > a.list_title"))
    }

    private fun extractNewsItems(elements: List<WebElement>, category: NewsCategory): List<NewsItem> {
        return elements.mapNotNull {
            val title = it.text.trim()
            val url = it.getAttribute("href")
            if (title.isNotBlank() && url != null) {
                NewsItem(title = title, url = url, category = category)
            } else {
                null
            }
        }
    }
}