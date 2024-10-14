package com.example.news.service.impl;

import com.example.news.dao.NewsEntityRepository;
import com.example.news.pojo.entity.mysql.News;
import com.example.news.pojo.entity.neo4j.NewsNeo4j;
import com.example.news.pojo.entity.neo4j.RelationNeo4j;
import com.example.news.pojo.vo.kg.Entity;
import com.example.news.pojo.vo.kg.KGResult;
import com.example.news.pojo.vo.kg.RelationEdge;
import com.example.news.pojo.vo.neo4j.CategoryVO;
import com.example.news.pojo.vo.neo4j.EntityVO;
import com.example.news.pojo.vo.neo4j.NewsInfoVO;
import com.example.news.pojo.vo.neo4j.RelationLinkVO;
import com.example.news.service.KGService;
import com.example.news.service.Neo4jService;
import com.example.news.service.NewsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j

public class Neo4jServiceImpl implements Neo4jService {
    @Autowired
    public NewsService newsService;
    @Autowired
    public KGService kgService;
    @Autowired
    private NewsEntityRepository newsEntityRepository;



    //分析某篇新闻的实体及关系，添加进neo4j数据库
    @Override
    public int addNews(Long id) {
        log.info("In addNews");

        News news = newsService.getNews(id);
        KGResult kgResult = kgService.getKGResult(news.getTitle() + "。" + news.getContent());
        if (kgResult == null) {
            return 0;
        }
        List<Entity> entities = kgResult.getEntities();
        List<RelationEdge> relations = kgResult.getRelations();

        for (Entity entity : entities) {
            NewsNeo4j newsEntity = new NewsNeo4j(entity.getValue(), entity.getType(), id);
            newsEntityRepository.save(newsEntity);
        }

        for (RelationEdge relation : relations) {
            NewsNeo4j n1 = newsEntityRepository.findFirstByValue(relation.getFirst().getValue());
            NewsNeo4j n2 = newsEntityRepository.findFirstByValue(relation.getSecond().getValue());

            if (n1 == null) {
                newsEntityRepository.save(NewsNeo4j.builder().newsId(id).type(relation.getFirst().getType()).value(relation.getFirst().getValue()).relationList(new ArrayList<>()).build());
            }
            if (n2 == null) {
                newsEntityRepository.save(NewsNeo4j.builder().newsId(id).type(relation.getSecond().getType()).value(relation.getSecond().getValue()).relationList(new ArrayList<>()).build());
            }

        }

        for (RelationEdge relation : relations) {
            addRelationship(relation.getSecond().getValue(),
                    relation.getFirst().getValue(),
                    relation.getRelation());
        }
        return 1;
    }


    @Override
    public void addRelationship(String first, String second, String relationName) {
        NewsNeo4j firstEntity = newsEntityRepository.findFirstByValue(first);
        NewsNeo4j secondEntity = newsEntityRepository.findFirstByValue(second);

        for (RelationNeo4j r : firstEntity.getRelationList()) {
            if (r.getNewsEntity().getValue().equals(second)) {
                return;
            }
        }
        firstEntity.getRelationList().add(RelationNeo4j.builder().newsEntity(secondEntity).relationName(relationName).build());
        newsEntityRepository.save(firstEntity);
    }

    @Override
    public void findKG(Long id) {
    }

    @Override
    public NewsInfoVO getNewsInfo(Long newsId) {
        List<NewsNeo4j> newsEntityList = newsEntityRepository.findByNewsId(newsId);
        if (newsEntityList.isEmpty()) {
            int res = addNews(newsId);
            if(res == 0){
                return null;
            }
        }
        List<CategoryVO> categoryVoList = getCategoryByNewsId(newsId);
        List<RelationLinkVO> relationLinkVOList = getLinkDataByNewsId(newsId);
        List<EntityVO> entityVOList = getEntityNodeByNewsId(newsId);
        return NewsInfoVO.builder().categories(categoryVoList).entityList(entityVOList).relationLinks(relationLinkVOList).build();

    }

    //根据newsId，给出该篇新闻的实体及关系
    @Override
    public List<NewsNeo4j> findKGByNewsId(Long newsId) {
        return newsEntityRepository.findByNewsId(newsId);
    }

    //根据newsId，返回实体的categories
    @Override
    public List<CategoryVO> getCategoryByNewsId(Long newsId) {
        List<NewsNeo4j> newsEntityList = newsEntityRepository.findByNewsId(newsId);
        List<CategoryVO> categoryVoList = new ArrayList<>();
        for (NewsNeo4j entity : newsEntityList) {
            CategoryVO categoryVo = CategoryVO.builder().name(entity.getType()).build();
            if (!categoryVoList.contains(categoryVo)) {
                categoryVoList.add(categoryVo);
            }
        }
        return categoryVoList;
    }

    //根据newsId，返回实体的linkData
    @Override
    public List<RelationLinkVO> getLinkDataByNewsId(Long newsId) {
        List<NewsNeo4j> newsEntityList = newsEntityRepository.findByNewsId(newsId);
        List<RelationLinkVO> relationLinkVoList = new ArrayList<>();
        for (NewsNeo4j entity : newsEntityList) {
            List<RelationNeo4j> relationList = entity.getRelationList();
            Long source = entity.getId();
            for (RelationNeo4j relation : relationList) {
                relationLinkVoList.add(RelationLinkVO.builder().source(source)
                        .target(relation.getNewsEntity().getId()).name(relation.getRelationName()).build());
            }
        }
        return relationLinkVoList;
    }

    //根据newsId，返回实体节点Data
    @Override
    public List<EntityVO> getEntityNodeByNewsId(Long newsId) {
        List<NewsNeo4j> newsEntityList = newsEntityRepository.findByNewsId(newsId);
        List<EntityVO> entityVoList = new ArrayList<>();
        for (NewsNeo4j entity : newsEntityList) {
            entityVoList.add(EntityVO.builder().id(entity.getId()).name(entity.getValue()).category(entity.getType()).build());
        }
        return entityVoList;
    }


    @Override
    public List<NewsNeo4j> getAllNewsEntity() {
        return newsEntityRepository.findAll();
    }

    @Override
    public NewsNeo4j getEntityByValue(String value) {
        return newsEntityRepository.findFirstByValue(value);
    }

    @Override
    public void deleteEntity(String value) {
        newsEntityRepository.delete(getEntityByValue(value));

    }

    @Override
    public KGResult addNews(News news) {
        KGResult kgResult = kgService.getKGResult(news.getTitle() + "。" + news.getContent());
        if (kgResult == null) {
            return null;
        }
        List<Entity> entities = kgResult.getEntities();
        List<RelationEdge> relations = kgResult.getRelations();

        for (Entity entity : entities) {
            NewsNeo4j newsEntity = new NewsNeo4j(entity.getValue(), entity.getType(), news.getId());
            newsEntityRepository.save(newsEntity);
        }

        for (RelationEdge relation : relations) {
            NewsNeo4j n1 = newsEntityRepository.findFirstByValue(relation.getFirst().getValue());
            NewsNeo4j n2 = newsEntityRepository.findFirstByValue(relation.getSecond().getValue());
            if (n1 == null) {
                newsEntityRepository.save(NewsNeo4j.builder().newsId(news.getId())
                        .type(relation.getFirst().getType()).value(relation.getFirst().getValue()).relationList(new ArrayList<>()).build());
            }
            if (n2 == null) {
                newsEntityRepository.save(NewsNeo4j.builder().newsId(news.getId())
                        .type(relation.getSecond().getType()).value(relation.getSecond().getValue()).relationList(new ArrayList<>()).build());
            }
        }
        for (RelationEdge relation : relations) {
            addRelationship(relation.getSecond().getValue(),
                    relation.getFirst().getValue(),
                    relation.getRelation());
        }
        return kgResult;
    }

}
