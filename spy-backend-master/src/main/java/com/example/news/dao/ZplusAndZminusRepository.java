package com.example.news.dao;

import com.example.news.pojo.entity.mysql.NewsEvent;
import com.example.news.pojo.entity.mysql.ZplusAndZminus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZplusAndZminusRepository extends JpaRepository<ZplusAndZminus, Long> {
}
