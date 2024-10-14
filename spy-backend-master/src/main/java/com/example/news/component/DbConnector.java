package com.example.news.component;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Config;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;

@Slf4j
@Component
public class DbConnector {

    /**
     * MySQL数据库连接
     *
     * @return Connection
     * @throws Exception
     */
    public static Connection getMysqlConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");    // 加载数据库驱动
        //返回连接
        return DriverManager.getConnection(                 // 获取连接
                "jdbc:mysql://111.229.131.214:11301/news",         // 数据库URL
                "root",         // 用户名
                "Spy?1130ywydzz");    // 登录密码
    }

    /**
     * ElasticSearch数据库连接
     *
     * @return HttpHost
     */
    public static HttpHost getEsHttpHost() {
        return new HttpHost("111.229.131.214", 9200, "http");
    }

    public static final String username = "neo4j";
    public static final String password = "neo4jspy";
    public static final String url = "bolt://123.60.77.90:7687";

    /**
     * Neo4j数据库连接
     *
     * @return Driver
     */
    public static Driver getDriver() {
        Driver driver = GraphDatabase.driver(url, AuthTokens.basic(username, password), Config.builder().build());
        if (driver == null) {
            log.info("****************A driver can`t be created to {}", url);
        }
        return driver;
    }

}
