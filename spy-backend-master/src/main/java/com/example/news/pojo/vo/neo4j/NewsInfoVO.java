package com.example.news.pojo.vo.neo4j;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class NewsInfoVO {
    List<EntityVO> entityList;
    List<CategoryVO> categories;
    List<RelationLinkVO> relationLinks;
}
