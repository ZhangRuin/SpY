package com.example.news.component.sink;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.example.news.component.DbConnector;
import com.example.news.pojo.document.es.NewsDoc;
import com.example.news.pojo.vo.news.NewsInit;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
public class EsSink extends RichSinkFunction<NewsInit> {
    private RestClient restClient;
    private ElasticsearchClient client;

    private HttpHost httpHost;

    @Value("${spring.elasticsearch.uris}")
    private String uris;

    @Override
    public void open(Configuration parameters) {
        httpHost = DbConnector.getEsHttpHost();
        restClient = RestClient.builder(httpHost).build();
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());
        client = new ElasticsearchClient(transport);
    }

    @Override
    public void close() throws Exception {
        client.shutdown();
        restClient.close();
    }

    @Override
    public void invoke(NewsInit value, Context context) throws Exception {
        NewsDoc newsDoc = NewsDoc.builder().id(value.getId()).title(value.getTitle())
                .content(value.getContent()).date(value.getDate()).source(value.getSource())
                .category(value.getCategory()).keywords(value.getKeywords())
                .standPoint(value.getStandpoint()).build();

        IndexRequest<NewsDoc> indexRequest = new IndexRequest.Builder<NewsDoc>()
                .index("news")
                .id(String.valueOf(value.getId()))
                .document(newsDoc)
                .build();
        client.index(indexRequest);
        log.info("ElasticSearch写入: " + newsDoc);
    }

}
