package com.akradev.newsbot.service

import com.akradev.newsbot.controller.NewsSection
import com.akradev.newsbot.controller.SectionNewsItem
import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.stereotype.Service
import java.io.File
import java.time.Duration

@Service
class SectionNewsCrawlerService {
    fun fetchSectionNews(section: NewsSection): List<SectionNewsItem> {
        val driver = createDriver()
        val url = section.toUrl()

        return try {
            driver.get(url)
            savePageSource(driver.pageSource, section)
            val selector = "ul.sa_list li.sa_item a.sa_text_title"
            val wait = WebDriverWait(driver, Duration.ofSeconds(10))
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)))

            val elements = driver.findElements(By.cssSelector("ul.sa_list li.sa_item"))
            elements.mapNotNull { item ->
                val linkElement = try {
                    item.findElement(By.cssSelector("a.sa_text_title"))
                } catch (e: NoSuchElementException) {
                    return@mapNotNull null
                }

                val title = try {
                    linkElement.findElement(By.cssSelector("strong.sa_text_strong")).text.trim()
                } catch (e: NoSuchElementException) {
                    linkElement.text.trim()
                }

                val url = linkElement.getAttribute("href") ?: return@mapNotNull null

                val summary = try {
                    item.findElement(By.cssSelector("div.sa_text_lede"))
                        .getAttribute("textContent")
                        ?.trim()
                        ?.takeIf { it.isNotBlank() }
                } catch (e: NoSuchElementException) {
                    null
                }
                if (title.isNotBlank()) {
                    SectionNewsItem(title = title, url = url, summary = summary, section = section)
                } else null
            }
        } finally {
            driver.quit()
        }
    }

    private fun createDriver(): WebDriver {
        WebDriverManager.chromedriver().setup()
        val options = ChromeOptions().apply {
            addArguments("--headless=new", "--disable-gpu", "--no-sandbox")
        }
        return ChromeDriver(options)
    }

    private fun savePageSource(source: String, section: NewsSection) {
        val fileName = "pageSource-${section.name}.html"
        File(fileName).writeText(source)
        println("✅ pageSource 저장 완료: $fileName")
    }
}