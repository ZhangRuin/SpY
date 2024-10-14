package com.example.news.service;

import com.example.news.pojo.entity.mysql.Category;

import java.io.IOException;
import java.util.List;

public interface HanLPService {
    void setup();
    Category textAnalysis(String text) throws IOException;

    double sentimentAnalysis(String text) throws IOException;

    List<String> keyExtraction(String text) throws IOException;
}
