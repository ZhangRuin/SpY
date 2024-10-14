package com.example.news.pojo.entity.mysql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class ProvinceAndCity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String province;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "province_city_mapping", joinColumns = @JoinColumn(name = "province_id"))
    @Column(name = "city")
    private List<String> city;
}
