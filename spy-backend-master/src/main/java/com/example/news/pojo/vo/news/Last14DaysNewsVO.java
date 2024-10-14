package com.example.news.pojo.vo.news;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Lob;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class Last14DaysNewsVO {
//    @DateTimeFormat(pattern = "yyyy-MM-dd")
//    private Date date;
    @Lob
    private String title;
    @Lob
    private String content;
}
