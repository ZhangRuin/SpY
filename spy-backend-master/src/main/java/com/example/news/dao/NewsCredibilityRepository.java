package com.example.news.dao;

import com.example.news.pojo.entity.mysql.Media;
import com.example.news.pojo.entity.mysql.NewsCredibility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NewsCredibilityRepository extends JpaRepository<NewsCredibility, Long> {
    Optional<NewsCredibility> findByNewsId(Long newsId);
}
