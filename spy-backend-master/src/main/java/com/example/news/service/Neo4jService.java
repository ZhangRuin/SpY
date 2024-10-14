package com.example.news.service;

import com.example.news.pojo.entity.mysql.News;
import com.example.news.pojo.entity.neo4j.NewsNeo4j;
import com.example.news.pojo.vo.kg.KGResult;
import com.example.news.pojo.vo.neo4j.CategoryVO;
import com.example.news.pojo.vo.neo4j.EntityVO;
import com.example.news.pojo.vo.neo4j.NewsInfoVO;
import com.example.news.pojo.vo.neo4j.RelationLinkVO;

import java.util.List;


public interface Neo4jService {
    int addNews(Long id);

//    void addRelationship(String first, String type1, String second, String type2, String relationName, Long newsId);

    void addRelationship(String first, String second, String relationName);

    void findKG(Long id);

    NewsInfoVO getNewsInfo(Long newsId);

    List<NewsNeo4j> findKGByNewsId(Long id);

    //根据newsId，给出实体的categories
    List<CategoryVO> getCategoryByNewsId(Long newsId);

    List<RelationLinkVO> getLinkDataByNewsId(Long newsId);

    List<EntityVO> getEntityNodeByNewsId(Long newsId);

    List<NewsNeo4j> getAllNewsEntity();

    NewsNeo4j getEntityByValue(String value);


    void deleteEntity(String value);

    KGResult addNews(News news);

}
