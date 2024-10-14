package com.example.news.pojo.vo.news;

import com.alibaba.fastjson.annotation.JSONField;
import com.example.news.pojo.entity.mysql.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class NewsInit {
    private Long id;
    private String url;
    private String title;
    private String content;
    @JSONField(format = "yyyy年MM月dd日 HH:mm")
    private Date date;
    private String source;
    private Category category;
    private List<String> keywords;
    private Double standpoint;
}
