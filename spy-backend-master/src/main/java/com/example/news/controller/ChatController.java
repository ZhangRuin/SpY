package com.example.news.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.util.StrUtil;
import com.example.news.config.RedisConstantsConfig;
import com.example.news.pojo.vo.common.ResultResponse;
import com.example.news.service.NewsService;
import com.example.news.service.impl.XfAIServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/chat", produces = "application/json")
@RequiredArgsConstructor
//@SaCheckLogin
public class ChatController {
    public final XfAIServiceImpl aiService;
    public final NewsService newsService;
    public final StringRedisTemplate stringRedisTemplate;
    public final RedisConstantsConfig redisConstantsConfig;
    private static final String prompt = "将提供给你几天的新闻数据。在数据一中，每条数据数据内容按顺序包括日期，日期对应的新闻种类和日期对应的每种新闻种类对应的新闻数量。" +
            "在数据二中，每条数据数据内容按顺序包括日期，省份或地区名，和该日期该省份或地区对应的新闻数量。" +
            "请你对结合数据并回答有关问题。数据如下:\n";

    @GetMapping("question")
    public ResultResponse chat(String question) throws ParseException {
        // 创建一个date为2024-03-08
//        String dateString = "2024-03-08";
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        Date date = dateFormat.parse(dateString);
        Date date = new Date();
        String temp1, temp2;
        String s = stringRedisTemplate.opsForValue().get(redisConstantsConfig.getProvinceCountKey() + "category");
        if (StrUtil.isNotEmpty(s)) {
            temp1 = s;
        } else {
            temp1 = newsService.getDay14CategoryPrompt(newsService.get14DaysCategory(date));
            stringRedisTemplate.opsForValue().set(redisConstantsConfig.getProvinceCountKey() + "category", temp1, redisConstantsConfig.getPromptTTL());
        }
        s = stringRedisTemplate.opsForValue().get(redisConstantsConfig.getProvinceCountKey() + "province");
        if (StrUtil.isNotEmpty(s)) {
            temp2 = s;
        } else {
            temp2 = newsService.getProvinceCountPrompt(newsService.get7DaysProvinceCnt(date));
            stringRedisTemplate.opsForValue().set(redisConstantsConfig.getProvinceCountKey() + "province", temp2, redisConstantsConfig.getPromptTTL());
        }
        String resPrompt = prompt +
                "数据一如下：" + temp1 + "\n" +
                "数据二如下：" + temp2 + "\n" +
                "问题如下:" + question + "\n" +
                "如果你无法根据数据得出准确答案，返回：对不起，我无法理解您的问题，请重新描述";
        return ResultResponse.success(aiService.sendQuestion(resPrompt));
    }

    @GetMapping("try")
    public ResultResponse TRY() throws ParseException {
        // 日期字符串
        String dateString = "2024-03-08";
        // 日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse(dateString);
        System.out.println("Date: " + date);
        return ResultResponse.success(newsService.getProvinceCountPrompt(newsService.get7DaysProvinceCnt(date)));
    }
}
