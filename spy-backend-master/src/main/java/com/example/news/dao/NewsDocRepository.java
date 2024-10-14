package com.example.news.dao;

import com.example.news.pojo.document.es.NewsDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface NewsDocRepository extends ElasticsearchRepository<NewsDoc, Long> {

}
