package com.example.news.pojo.entity.mysql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Lob
    private String title;       //标题
    private Date date;          //日期
    @Lob
    private String content;     //内容
    private String source;      //来源
    @Enumerated(EnumType.STRING)
    private Category category;  //类别
    private Double standPoint;  //立场 保存的是小数
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> keywords; //关键词
}
