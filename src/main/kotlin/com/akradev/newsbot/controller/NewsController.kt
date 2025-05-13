package com.akradev.newsbot.controller

import com.akradev.newsbot.service.NewsCrawlerService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/news")
class NewsController(
    private val crawlerService: NewsCrawlerService,
) {

    @GetMapping("/titles")
    fun getTitlesByCategories(
        @RequestParam categories: List<NewsCategory>,
    ): List<NewsItem> {
        return categories.flatMap { crawlerService.fetchTitles(it) }
    }
}

enum class NewsCategory(val sid1: String) {
    POLITICS("100"),
    ECONOMY("101"),
    SOCIETY("102"),
    LIFE_CULTURE("103"),
    WORLD("104"),
    IT_SCIENCE("105")
}

data class NewsItem(
    val title: String,
    val url: String,
    val category: NewsCategory,
)

enum class NewsSection(val mainCode: String, val subCode: String) {
    HEALTH("103", "241"),       // 생활/문화 > 건강정보
    FOOD("103", "238"),         // 생활/문화 > 음식/맛집
    TRAVEL("103", "255"),       // 생활/문화 > 여행/레저
    PET("103", "376"),          // 생활/문화 > 반려동물
    BEAUTY("103", "213"),       // 생활/문화 > 패션/뷰티
    RELIGION("103", "229"),    // 생활/문화 > 종교

    // 사회
    SOCIAL_ACCIDENT("102", "249"),
    SOCIAL_EDUCATION("102", "250"),
    SOCIAL_LABOR("102", "251"),
    SOCIAL_MEDIA("102", "252"),
    SOCIAL_ENVIRONMENT("102", "254"),
    SOCIAL_WELFARE("102", "256"),
    SOCIAL_FOOD_MEDICAL("102", "376"),
    SOCIAL_LOCAL("102", "258"),
    SOCIAL_PEOPLE("102", "276"),
    SOCIAL_GENERAL("102", "239");

    fun toUrl(): String = "https://news.naver.com/breakingnews/section/$mainCode/$subCode"
}

data class SectionNewsItem(
    val title: String,
    val url: String,
    val summary: String?,
    val section: NewsSection
)