package com.example.news.dao;

import com.example.news.pojo.entity.mysql.NewsEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsEventRepository extends JpaRepository<NewsEvent, Long> {
    List<NewsEvent> findAllByNewsId(Long newsId);

    void deleteAllByNewsId(Long newsId);
}
