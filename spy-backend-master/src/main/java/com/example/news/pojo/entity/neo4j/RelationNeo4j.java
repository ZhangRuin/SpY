package com.example.news.pojo.entity.neo4j;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
@Data
@Builder
public class RelationNeo4j {
    @Id
    @GeneratedValue
    Long id;

    @TargetNode
    private NewsNeo4j newsEntity;

    private String relationName;
}
