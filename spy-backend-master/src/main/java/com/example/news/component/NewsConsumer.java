package com.example.news.component;

import com.alibaba.fastjson2.JSONObject;
import com.example.news.dao.NewsEntityRepository;
import com.example.news.dao.NewsRepository;
import com.example.news.pojo.entity.mysql.News;
import com.example.news.pojo.entity.neo4j.NewsNeo4j;
import com.example.news.pojo.vo.kg.Entity;
import com.example.news.pojo.vo.kg.KGResult;
import com.example.news.pojo.vo.kg.RelationEdge;
import com.example.news.pojo.vo.news.NewsInit;
import com.example.news.pojo.vo.rabbitmq.NewsRabbitmq;
import com.example.news.service.HanLPService;
import com.example.news.service.KGService;
import com.example.news.service.Neo4jService;
import com.example.news.service.NewsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class NewsConsumer {
    @Autowired
    private NewsService newsService;
    @Autowired
    HanLPService hanLPService;
    @Autowired
    public KGService kgService;
    @Autowired
    private NewsEntityRepository newsEntityRepository;
    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private Neo4jService neo4jService;

    /**
     * 接收消息，newsRabbitmq对象可能会有空值，需要写代码过滤
     * @param messageBytes 消息字节序列
     */
    @RabbitListener(queuesToDeclare = @Queue(name = "python_news_neo4j", durable = "true"))
    public void receive(byte[] messageBytes) {
        String message = new String(messageBytes, StandardCharsets.UTF_8);
        try {
            // 使用 Fastjson 将 JSON 消息转换为 Java 对象
            NewsRabbitmq newsRabbitmq = JSONObject.parseObject(message, NewsRabbitmq.class);
            log.info("RabbitListener Received message: {}", newsRabbitmq);

            if (newsRabbitmq.filter()) {
                hanLPService.setup();
                String text = newsRabbitmq.getContent();
                News news = News.builder().id(newsRabbitmq.getId())
                        .title(newsRabbitmq.getTitle()).content(newsRabbitmq.getContent())
                        .date(newsRabbitmq.getDate()).source(newsRabbitmq.getSource())
                        .category(hanLPService.textAnalysis(text))
                        .keywords(hanLPService.keyExtraction(text))
                        .standPoint(hanLPService.sentimentAnalysis(text)).build();
                log.info("RabbitListener news: {}", news);

                KGResult kgResult = kgService.getKGResult(news.getTitle() + "。" + news.getContent());
                if (kgResult == null) {
                    log.info("大模型生成知识图谱失败");
                }else{
                    log.info("大模型生成知识图谱成功");
                }


                newsService.calculateCredibility(newsRabbitmq.getId()); // 生成可信度
                log.info("可信度生成成功");


                List<Entity> entities = kgResult.getEntities();
                List<RelationEdge> relations = kgResult.getRelations();

                for (Entity entity : entities) {
                    NewsNeo4j newsEntity = new NewsNeo4j(entity.getValue(), entity.getType(),news.getId());
                    newsEntityRepository.save(newsEntity);
                }

                for (RelationEdge relation : relations) {
                    NewsNeo4j n1 = newsEntityRepository.findFirstByValue(relation.getFirst().getValue());
                    NewsNeo4j n2 = newsEntityRepository.findFirstByValue(relation.getSecond().getValue());

                    if (n1 == null) {
                        newsEntityRepository.save(NewsNeo4j.builder().newsId(news.getId()).type(relation.getFirst().getType()).value(relation.getFirst().getValue()).relationList(new ArrayList<>()).build());
                    }
                    if (n2 == null) {
                        newsEntityRepository.save(NewsNeo4j.builder().newsId(news.getId()).type(relation.getSecond().getType()).value(relation.getSecond().getValue()).relationList(new ArrayList<>()).build());
                    }

                }

                for (RelationEdge relation : relations) {
                    neo4jService.addRelationship(relation.getSecond().getValue(),
                            relation.getFirst().getValue(),
                            relation.getRelation());
                }
                log.info("该新闻的知识图谱已存入neo4j数据库");

            }
        } catch (Exception e) {
            log.error("Error message: {}", e.getMessage());
        }
    }
}
