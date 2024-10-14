package com.example.news.pojo.document.es;

import com.example.news.pojo.entity.mysql.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;
import java.util.List;

@Data
@Document(indexName = "news")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewsDoc {
    @Id ()
    private Long id;

    @Field(type = FieldType.Text, searchAnalyzer = "ik_smart", analyzer = "ik_max_word")
    private String title;       //标题

    @Field(type = FieldType.Date)
    private Date date;          //日期

    @Field(type = FieldType.Text, searchAnalyzer = "ik_smart", analyzer = "ik_max_word")
    private String content;     //内容

    @Field(type = FieldType.Text)
    private String source;      //来源

    @Field(type = FieldType.Keyword)
    private Category category;  //类别

    @Field(type = FieldType.Double)
    private Double standPoint; //立场 保存的是小数

    @Field(type = FieldType.Text)
    private List<String> keywords; //关键词
}
