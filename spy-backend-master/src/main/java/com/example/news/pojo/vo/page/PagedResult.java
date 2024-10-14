package com.example.news.pojo.vo.page;

import lombok.Data;

import java.util.List;

@Data
public class PagedResult<T> {
    private List<T> list;
    private Integer totalCount;
}
