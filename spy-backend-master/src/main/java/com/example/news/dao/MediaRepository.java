package com.example.news.dao;

import com.example.news.pojo.entity.mysql.Media;
import com.example.news.pojo.entity.mysql.ProvinceAndCity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MediaRepository extends JpaRepository<Media, Long> {
    List<Media> findAll();
    Optional<Media> findByName(String name);
}
