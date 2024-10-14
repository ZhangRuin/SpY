package com.example.news.component.job;

import com.alibaba.fastjson.JSONObject;
import com.example.news.component.MyHanLPClient;
import com.example.news.component.sink.EsSink;
import com.example.news.component.sink.MysqlSink;
import com.example.news.component.sink.Neo4jSink;
import com.example.news.config.RabbitmqConfig;
import com.example.news.config.XfXhConfig;
import com.example.news.pojo.vo.news.NewsInit;
import com.example.news.pojo.vo.rabbitmq.NewsRabbitmq;
import com.example.news.service.AIService;
import com.example.news.service.HanLPService;
import com.example.news.service.KGService;
import com.example.news.util.NewsFilter;
import com.hankcs.hanlp.restful.HanLPClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SideOutputDataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSource;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

@Slf4j
public class SaveJob implements FlinkJob, Serializable {
    @Autowired
    private RabbitmqConfig rabbitmqConfig;
    @Autowired
    HanLPService hanLPService;

    @Override
    public void run(String... args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.enableCheckpointing(500L);  // 消费仅一次或至少一次，检查点是必须的
        env.setParallelism(1);

        env.registerType(NewsInit.class);
        env.registerType(HanLPClient.class);
//        env.registerType(HanLPClient.class);
//        env.registerType(HanLPService.class);

        final RMQConnectionConfig connectionConfig = new RMQConnectionConfig.Builder()
                .setHost(rabbitmqConfig.getHost())
                .setPort(rabbitmqConfig.getPort())
                .setUserName(rabbitmqConfig.getUsername())
                .setPassword(rabbitmqConfig.getPassword())
                .setVirtualHost(rabbitmqConfig.getVirtualHost())
                .build();

        RMQSource<String> rabbitmqSource = new RMQSource<>(
                connectionConfig,            // rabbitmq连接的配置
                "python_news",               // rabbitmq的队列名，消费的队列名
                false,                       // 使用相关编号，至少一次时设置为false
                new SimpleStringSchema());   // 反序列化成java的对象

        final DataStreamSource<String> newsDS = env.addSource(rabbitmqSource);

        OutputTag<NewsInit> mysqlTag = new OutputTag<>("mysql_news", TypeInformation.of(NewsInit.class));
        OutputTag<NewsInit> esTag = new OutputTag<>("es_news", TypeInformation.of(NewsInit.class));
        OutputTag<NewsInit> neo4jTag = new OutputTag<>("neo4j_news", TypeInformation.of(NewsInit.class));

        SingleOutputStreamOperator<NewsInit> newsInitStream = newsDS
                .map((newsMessage) -> {
                    NewsRabbitmq newsRabbitmq = null;
                    try {
                        // 使用 Fastjson 将 JSON 消息转换为 Java 对象
                        newsRabbitmq = JSONObject.parseObject(newsMessage, NewsRabbitmq.class);
                        log.info("Flink Received message: {}", newsRabbitmq);
                    } catch (Exception e) {
                        log.error("Error parsing JSON message: {}", e.getMessage());
                    }
                    return newsRabbitmq;
                })

                .filter(NewsRabbitmq::filter) // 过滤包含空字段、超过字数限制的新闻

                .map((newsRabbitmq -> {
                    hanLPService.setup();
                    String text = newsRabbitmq.getContent();
                    NewsInit newsInit = NewsInit.builder().id(newsRabbitmq.getId())
                            .url(newsRabbitmq.getUrl()).title(newsRabbitmq.getTitle())
                            .content(newsRabbitmq.getContent())
                            .date(newsRabbitmq.getDate()).source(newsRabbitmq.getSource())
                            .category(hanLPService.textAnalysis(text))
                            .keywords(hanLPService.keyExtraction(text))
                            .standpoint(hanLPService.sentimentAnalysis(text))
                            .build();
//                    log.info("map: newsRabbitmq -> " + newsInit);
                    return newsInit;
                }))

                .filter(NewsFilter::filter)

                .process(
                        new ProcessFunction<NewsInit, NewsInit>() {
                            @Override
                            public void processElement(NewsInit newsInit, ProcessFunction<NewsInit, NewsInit>.Context context, Collector<NewsInit> collector) {
//                                log.info("process: " + newsInit);
                                context.output(mysqlTag, newsInit);
                                context.output(esTag, newsInit);
                                context.output(neo4jTag, newsInit);
                            }
                        }
                );

//        newsInitStream.print();
        SideOutputDataStream<NewsInit> mysqlStream = newsInitStream.getSideOutput(mysqlTag);
        mysqlStream.addSink(new MysqlSink());

        SideOutputDataStream<NewsInit> esStream = newsInitStream.getSideOutput(esTag);
        esStream.addSink(new EsSink());

        SideOutputDataStream<NewsInit> neo4jStream = newsInitStream.getSideOutput(neo4jTag);
        neo4jStream.addSink(new Neo4jSink());

        // 执行作业
        env.execute("Test Job");
    }
}
