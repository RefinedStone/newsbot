package com.akradev.newsbot.controller

import com.akradev.newsbot.service.SectionNewsCrawlerService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/section-news")
class SectionNewsController(
    private val sectionNewsCrawlerService: SectionNewsCrawlerService
) {

    @GetMapping
    fun getSectionNews(
        @RequestParam sections: List<NewsSection>
    ): List<SectionNewsItem> {
        return sections.flatMap { sectionNewsCrawlerService.fetchSectionNews(it) }
    }
}