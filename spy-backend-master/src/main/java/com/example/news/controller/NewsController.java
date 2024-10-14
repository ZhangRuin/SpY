package com.example.news.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.news.pojo.entity.mysql.News;
import com.example.news.pojo.vo.common.ResultResponse;
import com.example.news.pojo.vo.news.OneDateRequest;
import com.example.news.pojo.vo.news.SearchRequest;
import com.example.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/spy", produces = "application/json")
@RequiredArgsConstructor
//@SaCheckLogin
public class NewsController {
    private final NewsService newsService;

    @GetMapping(path = "/news/count")
    public ResultResponse countNews() {
        return ResultResponse.success(newsService.count());
    }

    @PostMapping(path = "/news", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse addNews(@RequestBody News news) {
        newsService.addNews(news);
        return ResultResponse.success();
    }

    @GetMapping(path = "/news/{id}")
    public ResultResponse getNews(@PathVariable("id") Long id) {
        return ResultResponse.success(newsService.getNews(id));
    }

    @PutMapping(path = "/news/{id}", consumes = "application/json")
    public ResultResponse updateNews(@PathVariable("id") Long id, @RequestBody News news) {
        newsService.updateNews(id, news);
        return ResultResponse.success();
    }

    @DeleteMapping(path = "/news/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResultResponse deleteNews(@PathVariable("id") Long id) {
        newsService.deleteNews(id);
        return ResultResponse.success();
    }

    @GetMapping(path = "/news/criteria")
    public ResultResponse search(@Valid SearchRequest request) {
        return ResultResponse.success(newsService.search(request));
    }

    @GetMapping(path = "/news/sources")
    public ResultResponse getAllSources() {
        return ResultResponse.success(newsService.getAllSources());
    }

    @GetMapping(path = "/news/14num")
    public ResultResponse get14Num(OneDateRequest dateRequest) {
        return ResultResponse.success(newsService.get14Num(dateRequest.getDate()));
    }

    @GetMapping(path = "/news/2days/category")
    public ResultResponse get2DaysCategory(OneDateRequest dateRequest) {
        return ResultResponse.success(newsService.get2DaysCategory(dateRequest.getDate()));
    }

    @GetMapping(path = "/news/standpoint")
    public ResultResponse getStandpoint(OneDateRequest dateRequest) {
        return ResultResponse.success(newsService.getStandpoint(dateRequest.getDate()));
    }

    @GetMapping(path = "/news/keyword")
    public ResultResponse getKeyword(OneDateRequest dateRequest) {
        return ResultResponse.success(newsService.getKeyword(dateRequest.getDate()));
    }

    @GetMapping(path = "/news/provinceCount")
    public ResultResponse last14DaysProvinceCount(OneDateRequest dateRequest) {
        return ResultResponse.success(newsService.provinceCount(dateRequest.getDate()));
    }

    @GetMapping(path = "/news/event/{id}")
    public ResultResponse getEvent(@PathVariable("id") Long id) {
        return ResultResponse.success(newsService.getEvent(id));
    }

    @GetMapping(path = "/news/credibility/{id}")
    public ResultResponse getNewsCredibility(@PathVariable("id") Long id) {
        return ResultResponse.success(newsService.calculateCredibility(id));
    }

}
