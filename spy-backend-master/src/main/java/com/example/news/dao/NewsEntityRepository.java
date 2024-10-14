package com.example.news.dao;

import com.example.news.pojo.entity.neo4j.NewsNeo4j;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

public interface NewsEntityRepository extends Neo4jRepository<NewsNeo4j, Long> {

//    @Query(value = "match p=(s:Ship)-[]-() where s.name={0} return p")
//    List<PathValue> getRelationshipByName(String name);

    NewsNeo4j findFirstByValue(String value);

    List<NewsNeo4j> findByNewsId(Long newsId);

//    @Query("MATCH (:NewsNeo4j {newsId: $newsId})-[r]-() DELETE r")
//    void deleteRelationshipsByNewsId(Long newsId);

    void deleteByNewsId(Long newsId);
}
