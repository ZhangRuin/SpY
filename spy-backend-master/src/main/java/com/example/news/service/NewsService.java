package com.example.news.service;

import com.example.news.pojo.entity.mysql.News;
import com.example.news.pojo.document.es.NewsDoc;
import com.example.news.pojo.entity.mysql.NewsCredibility;
import com.example.news.pojo.entity.mysql.NewsEvent;
import com.example.news.pojo.vo.news.*;
import com.example.news.pojo.vo.page.PagedResult;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface NewsService {
    long count();

    void addNews(News news);

    void updateNews(Long id, News news);

    News getNews(Long id);

    void deleteNews(Long id);

    PagedResult<NewsDoc> search(SearchRequest request);

    List<String> getAllSources();

    List<Day14VO> get14Num(Date date);

    List<Last14DaysNewsVO> last14DaysNews(Date date);

    List<ProvinceCountVO> provinceCount(Date date);

    Map<String, Integer> initProvince();

    List<Day2CategoryVO> get2DaysCategory(Date date);

    List<Day14CategoryVO> get14DaysCategory(Date date);

    String getDay14CategoryPrompt(List<Day14CategoryVO> day14CategoryVOS);

    List<Day14ProvinceVO> get7DaysProvinceCnt(Date date);


    String getProvinceCountPrompt(List<Day14ProvinceVO> day14ProvinceVOS);

    StandPointVO getStandpoint(Date date);

    List<OneDayKeywordVO> getKeyword(Date date);

    List<NewsEvent> getEvent(Long newsId);

    int sourceAnalysis(Long newsId);

    int wordAndLogicAnalysis(Long newsId);

    double sentimentAnalysis(Long newsId);

    int correspondingCategory(Long newsId);

    NewsCredibility calculateCredibility(Long newsId);
}
