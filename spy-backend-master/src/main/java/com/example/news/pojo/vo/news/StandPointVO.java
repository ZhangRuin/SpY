package com.example.news.pojo.vo.news;

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
public class StandPointVO {
    private Double averageStandPoint;
    private List<StandPointNum> standPointNums;
}
