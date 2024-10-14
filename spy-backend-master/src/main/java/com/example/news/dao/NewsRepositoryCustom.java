package com.example.news.dao;

import com.example.news.pojo.entity.mysql.News;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;


import java.util.Date;
import java.util.List;

@Deprecated
public interface NewsRepositoryCustom {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    List<News> findBySearchCriteria(
            @Param("keyword") String keyword,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("source") String source,
            Pageable pageable
    );
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    long countBySearchCriteria(
                    @Param("keyword") String keyword,
                    @Param("startDate") Date startDate,
                    @Param("endDate") Date endDate,
                    @Param("source") String source
            );
}
