package com.example.news.component.sink;

import com.example.news.component.DbConnector;
import com.example.news.pojo.vo.news.NewsInit;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.neo4j.driver.*;

@Slf4j
public class Neo4jSink extends RichSinkFunction<NewsInit> {
    private Driver driver;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        driver = DbConnector.getDriver();
    }

    @Override
    public void close() throws Exception {
        if (driver != null) {
            driver.close();
        }
    }

    @Override
    public void invoke(NewsInit newsInit, Context context) throws Exception {
        try (Session session = driver.session()) {
            // 构建Cypher查询语句
            String cypherQuery = "CREATE (n:News {category: $category, content: $content, date: $date, source: $source, standpoint: $standpoint, title: $title})";
            // 执行查询
            session.run(cypherQuery,
                    Values.parameters(
                            "category", newsInit.getCategory().name(),
                            "content", newsInit.getContent(),
                            "date", newsInit.getDate().toInstant().toString(),
                            "source", newsInit.getSource(),
                            "standpoint", newsInit.getStandpoint(),
                            "title", newsInit.getTitle()
                    )
            );
            log.info("Neo4j写入: {}", newsInit);
        } catch (Exception e) {
            log.error("Neo4j写入失败: {}", e.getMessage());
        }
    }
}
