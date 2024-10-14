package com.example.news.dao;

import com.example.news.pojo.entity.mysql.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


public interface NewsRepository extends JpaRepository<News, Long>, NewsRepositoryCustom, Serializable {
    // 自定义查询方法，用于获取所有的 source 值
    @Query("SELECT DISTINCT n.source FROM News n WHERE n.source IS NOT NULL")
    List<String> getAllSources();
    // 获取满足条件的记录总数
    long countByTitleContainingAndDateBetweenAndSourceContaining(String keyword, Date startDate, Date endDate, String source);
    @Query("SELECT COUNT(n) FROM News n WHERE n.date >= :date AND n.date < :nextDay")
    Long countNewsByDate(Date date, Date nextDay);

    @Query("SELECT n FROM News n WHERE n.date >= :date AND n.date < :nextDay")
    List<News> findByDateBetween(Date date, Date nextDay);

    @Query("SELECT COUNT(n) FROM News n WHERE n.date >= :date AND n.date < :nextDay AND n.standPoint >= :point1 AND n.standPoint < :point2")
    Long countNewsByDateAndStandpoint(Date date, Date nextDay, double point1, double point2);

    @Query("SELECT AVG(n.standPoint) FROM News n WHERE n.date >= :date AND n.date < :nextDay")
    Double averageStandpointByDate(Date date, Date nextDay);
}
