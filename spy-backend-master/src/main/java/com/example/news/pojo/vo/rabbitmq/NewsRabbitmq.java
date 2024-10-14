package com.example.news.pojo.vo.rabbitmq;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class NewsRabbitmq {
    private Long id;
    private String url;
    private String title;
    private String content;
    @JSONField(format = "yyyy年MM月dd日 HH:mm")
    private Date date;
    private String source;

    public boolean filter() {
        return url != null && !url.isEmpty()
                && title != null && !title.isEmpty() && title.length() <= 100
                && content != null && !content.isEmpty() && content.length() <= 1000
                && date != null
                && source != null && !source.isEmpty();
    }
}
