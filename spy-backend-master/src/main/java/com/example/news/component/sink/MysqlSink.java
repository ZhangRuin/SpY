package com.example.news.component.sink;

import com.example.news.pojo.vo.news.NewsInit;
import com.example.news.component.DbConnector;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;

@Slf4j
@Component
public class MysqlSink extends RichSinkFunction<NewsInit> {
    @Value("${spring.datasource.druid.driver-class-name}")
    private String mysqlDriverClassName;
    @Value("${spring.datasource.druid.url}")
    private String mysqlURL;
    @Value("${spring.datasource.druid.username}")
    private String mysqlUsername;
    @Value("${spring.datasource.druid.password}")
    private String mysqlPassword;
    private Connection conn = null;
    private PreparedStatement insertNewsStmt = null;
    private PreparedStatement insertKeywordsStmt = null;

    // 打开数据库连接，只执行一次，之后一直使用这个连接
    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
//        Class.forName(mysqlDriverClassName);    // 加载数据库驱动
//        conn = DriverManager.getConnection(                 // 获取连接
//                mysqlURL,         // 数据库URL
//                mysqlUsername,    // 用户名
//                mysqlPassword);   // 登录密码
        conn = DbConnector.getMysqlConnection();
        insertNewsStmt = conn.prepareStatement(  // 获取执行语句
                "insert into news.news(category, content, date, source, " +
                        "stand_point, title, id) values (?, ?, ?, ?, ?, ?, ?)");  // 插入news数据
        insertKeywordsStmt = conn.prepareStatement(
                "insert into news.news_keywords(news_id, keywords) values (?, ?)"); // 插入keywords数据
    }

    // 执行插入和更新
    @Override
    public void invoke(NewsInit newsInit, Context ctx) throws Exception {
        insertNews(newsInit);
        insertKeywords(newsInit);
    }

    // 关闭数据库连接
    @Override
    public void close() throws Exception {
        super.close();
        if (conn != null) conn.close();
        if (insertNewsStmt != null) insertNewsStmt.close();
        if (insertKeywordsStmt != null) insertKeywordsStmt.close();
    }

    private void insertNews(NewsInit newsInit) throws SQLException {
        insertNewsStmt.setString(1, newsInit.getCategory().name());
        insertNewsStmt.setString(2, newsInit.getContent());
        insertNewsStmt.setTimestamp(3, new java.sql.Timestamp(newsInit.getDate().getTime()));
        insertNewsStmt.setString(4, newsInit.getSource());
        insertNewsStmt.setDouble(5, newsInit.getStandpoint());
        insertNewsStmt.setString(6, newsInit.getTitle());
        insertNewsStmt.setLong(7, newsInit.getId());
        insertNewsStmt.executeUpdate();
        log.info("MySQL写入: " + insertNewsStmt);
    }

    private void insertKeywords(NewsInit newsInit) throws SQLException {
        for (String keyword: newsInit.getKeywords()) {
            insertKeywordsStmt.setLong(1, newsInit.getId());
            insertKeywordsStmt.setString(2, keyword);

            insertKeywordsStmt.executeUpdate();
        }
    }
}

