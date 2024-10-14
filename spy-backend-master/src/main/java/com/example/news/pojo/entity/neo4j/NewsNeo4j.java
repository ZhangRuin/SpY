package com.example.news.pojo.entity.neo4j;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.neo4j.core.schema.*;

import java.util.List;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;


@Data
@Node("NewEntity")
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class NewsNeo4j {
    @Id
    @GeneratedValue
    private Long id;
    private String value;
    private String type;
    private Long newsId;

    @Relationship(type = "Relation",direction = OUTGOING)
    private List<RelationNeo4j> relationList;

    public NewsNeo4j(String value, String type){
        this.value = value;
        this.type = type;
    }

    public NewsNeo4j(String value, String type, Long newsId) {
        this.value = value;
        this.type = type;
        this.newsId = newsId;
    }
}
