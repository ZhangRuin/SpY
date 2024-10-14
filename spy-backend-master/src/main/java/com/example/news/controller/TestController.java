package com.example.news.controller;

import com.example.news.pojo.vo.common.ResultResponse;
import com.example.news.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Deprecated
@RestController
public class TestController {
    @Autowired
    private NewsService newsService;
    @GetMapping("/test")
    public ResultResponse test(long newsId){

        return ResultResponse.success(newsService.calculateCredibility(newsId));
    }

}
