package com.example.news.util;

import com.example.news.pojo.vo.news.NewsInit;

public class NewsFilter {
    public static boolean filter(NewsInit newsInit) {
        // 过滤包含空字段的新闻
        if (newsInit.getTitle().isEmpty()) return false;
        if (newsInit.getContent().isEmpty()) return false;
        if (newsInit.getDate() == null) return false;
        if (newsInit.getSource().isEmpty()) return false;

        // 过滤标题超过100个字的新闻
        if (newsInit.getTitle().length() > 100) return false;
        if (newsInit.getContent().length() > 1000) return false;
        return true;
    }
}
