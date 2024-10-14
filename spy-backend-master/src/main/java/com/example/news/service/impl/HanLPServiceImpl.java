package com.example.news.service.impl;

import com.example.news.component.MyHanLPClient;
import com.example.news.pojo.entity.mysql.Category;
import com.example.news.service.HanLPService;
import com.hankcs.hanlp.restful.HanLPClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class HanLPServiceImpl implements HanLPService, Serializable {
    HanLPClient client;

    @Override
    public void setup(){
        client = new HanLPClient("https://hanlp.hankcs.com/api", "NDcyM0BiYnMuaGFubHAuY29tOlk0QlRnRUpkekJmbHlQZWE=");
    }

    @Override
    public Category textAnalysis(String text) throws IOException {
        //Map<String, List> doc = client.parse("2021年HanLPv2.1为生产环境带来次世代最先进的多语种NLP技术。英首相与特朗普通电话讨论华为与苹果公司。");
        //prettyPrint(doc);
        return Category.string2Enum(client.textClassification(text, "news_zh"));
    }

    @Override
    public double sentimentAnalysis(String text) throws IOException {
        return client.sentimentAnalysis(text);
    }

    @Override
    public List<String> keyExtraction(String text) throws IOException {
        Map<String,Double>keysTmp=client.keyphraseExtraction(text);
        return new ArrayList<>(keysTmp.keySet());
    }

}
