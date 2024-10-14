package com.example.news.dao;

import com.example.news.pojo.entity.mysql.News;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Deprecated
public class NewsRepositoryCustomImpl implements NewsRepositoryCustom {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<News> findBySearchCriteria(String keyword, Date startDate, Date endDate, String source, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<News> query = cb.createQuery(News.class);
        Root<News> news = query.from(News.class);
        List<Predicate> predicates = new ArrayList<>();

        // 对于keyword添加筛选条件
        Path<String> titlePath = news.get("title");
        predicates.add(cb.like(titlePath, "%" + keyword + "%"));

        Path<Date> datePath = news.get("date");
        if (startDate != null) {
            // 对于startDate添加筛选条件
            predicates.add(cb.greaterThanOrEqualTo(datePath, startDate));
        }

        if (endDate != null) {
            // 对于endDate添加筛选条件
            endDate.setHours(23);
            endDate.setMinutes(59);
            predicates.add(cb.lessThanOrEqualTo(datePath, endDate));
        }

        // 对于source添加筛选条件
        Path<String> sourcePath = news.get("source");
        predicates.add(cb.like(sourcePath, "%" + source + "%"));
        //开启分页
        query.select(news)
                .where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
        TypedQuery<News> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<News> resultList = typedQuery.getResultList();
        return resultList;
        //return entityManager.createQuery(query).getResultList();
    }

    @Override
    //@Cacheable(value = "newsCountCache", key = "#keyword + '-' + #startDate + '-' + #endDate + '-' + #source")
    public long countBySearchCriteria(String keyword, Date startDate, Date endDate, String source) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<News> news = query.from(News.class);
        List<Predicate> predicates = new ArrayList<>();

        // 对于keyword添加筛选条件
        Path<String> titlePath = news.get("title");
        predicates.add(cb.like(titlePath, "%" + keyword + "%"));

        Path<Date> datePath = news.get("date");
        if (startDate != null) {
            // 对于startDate添加筛选条件
            predicates.add(cb.greaterThanOrEqualTo(datePath, startDate));
        }

        if (endDate != null) {
            // 对于endDate添加筛选条件
            endDate.setHours(23);
            endDate.setMinutes(59);
            predicates.add(cb.lessThanOrEqualTo(datePath, endDate));
        }

        // 对于source添加筛选条件
        Path<String> sourcePath = news.get("source");
        predicates.add(cb.like(sourcePath, "%" + source + "%"));

        // 构建查询
        query.select(cb.count(news))
                .where(cb.and(predicates.toArray(new Predicate[predicates.size()])));

// 执行查询并返回结果数量
        return entityManager.createQuery(query).getSingleResult();
    }
}
