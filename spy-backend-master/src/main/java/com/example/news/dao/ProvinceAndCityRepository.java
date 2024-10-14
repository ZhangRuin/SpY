package com.example.news.dao;

import com.example.news.pojo.entity.mysql.ProvinceAndCity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProvinceAndCityRepository extends JpaRepository<ProvinceAndCity, Long> {
    List<ProvinceAndCity> findAll();
}
