package com.example.news.pojo.entity.mysql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class NewsCredibility {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long newsId;
    private double sourceScore;
    private double wordAndLogicScore;
    private double sentimentScore;
    private double categoryScore;
    private double totalScore;

}
