package com.example.news.service;

import com.example.news.pojo.vo.kg.Event;
import com.example.news.pojo.vo.kg.KGResult;

import java.util.List;

public interface KGService {
    KGResult getKGResult(String content);
    List<Event> getEvents(String content);
}
