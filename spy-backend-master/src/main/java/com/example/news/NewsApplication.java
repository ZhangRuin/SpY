package com.example.news;

import cn.dev33.satoken.SaManager;
import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.news.dao.MediaRepository;
import com.example.news.dao.NewsDocRepository;
import com.example.news.dao.NewsRepository;
import com.example.news.dao.ProvinceAndCityRepository;
import com.example.news.pojo.entity.mysql.Category;
import com.example.news.pojo.entity.mysql.Media;
import com.example.news.pojo.entity.mysql.News;
import com.example.news.pojo.document.es.NewsDoc;
import com.example.news.pojo.entity.mysql.ProvinceAndCity;
import com.example.news.pojo.vo.news.NewsInit;
import com.example.news.service.FlinkJobService;
import com.example.news.util.NewsFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class NewsApplication {
    @Value("${enable-init-data}")
    private boolean enableInitData;
    @Value("${enable-init-esData}")
    private boolean enableInitEsData;

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        ConfigurableApplicationContext context = SpringApplication.run(NewsApplication.class, args);
        // 获取 FlinkJobService Bean，并运行 Flink 作业
        FlinkJobService flinkJobService = context.getBean(FlinkJobService.class);
        flinkJobService.runFlinkJob();
        System.out.println("启动成功，Sa-Token 配置如下：" + SaManager.getConfig());
    }

    @Bean
    public CommandLineRunner dataInit(NewsRepository newsRepository, ProvinceAndCityRepository provinceAndCityRepository, MediaRepository mediaRepository) {
        return new CommandLineRunner() {
            @Override
            @Transactional
            public void run(String... args) throws Exception {
                if (!enableInitData) {
                    log.info("init data disabled");
                    return;
                }
                log.info("init data start");
                InputStream inputStream1 = new ClassPathResource("data_169.json").getInputStream();
                JSONArray jsonArray1 = JSONArray.parseArray(IoUtil.readUtf8(inputStream1));
                for (int i = 0; i < jsonArray1.size(); i++) {
                    JSONObject jsonObject = jsonArray1.getJSONObject(i);
                    NewsInit newsInit1 = JSONObject.toJavaObject(jsonObject, NewsInit.class);
                    if (NewsFilter.filter(newsInit1)) {
                        String text = newsInit1.getContent();
                        newsRepository.save(News.builder().title(newsInit1.getTitle()).date(newsInit1.getDate())
                                .content(newsInit1.getContent()).source(newsInit1.getSource())
                                .category(Category.string2Enum((String) jsonObject.get("category"))).standPoint(newsInit1.getStandpoint())
                                .keywords(newsInit1.getKeywords()).build());
                    }
                }
                //init media
//                if (mediaRepository.findAll().isEmpty()) {
//                    log.info("init data mediaRepository");
//                    InputStream inputStream2 = new ClassPathResource("media.json").getInputStream();
//                    JSONArray jsonArray2 = JSONArray.parseArray(IoUtil.readUtf8(inputStream2));
//                    for (int i = 0; i < jsonArray2.size(); i++) {
//                        JSONObject mediaJson = jsonArray2.getJSONObject(i);
//                        String categoryName = mediaJson.keySet().iterator().next();
//                        JSONArray mediaNames = mediaJson.getJSONArray(categoryName);
//                        for (int j = 0; j < mediaNames.size(); j++) {
//                            String name = mediaNames.getString(j);
//                            Optional<Media> mediaFindByName = mediaRepository.findByName(name);
//                            if (!mediaFindByName.isPresent()) {
//                                List<String> category = new ArrayList<>();
//                                category.add(categoryName);
//                                Media media = Media.builder().name(name).category(category).build();
//                                mediaRepository.save(media);
//                            } else {
//                                Media media = mediaFindByName.get();
//                                List<String> category = media.getCategory();
//                                if (!category.contains(categoryName)) {
//                                    category.add(categoryName);
//                                    media.setCategory(category);
//                                    mediaRepository.save(media);
//                                }
//                            }
//                        }
//                    }
//                }
                //init provinceAndCity
                if (provinceAndCityRepository.findAll().isEmpty()) {
                    log.info("init data provinceAndCityRepository");

                    InputStream inputStream3 = new ClassPathResource("province_city.json").getInputStream();
                    JSONArray jsonArray3 = JSONArray.parseArray(IoUtil.readUtf8(inputStream3));
                    for (int i = 0; i < jsonArray3.size(); i++) {
                        ProvinceAndCity provinceAndCity = ProvinceAndCity.builder().build();
                        JSONObject provinceJson = jsonArray3.getJSONObject(i);
                        String provinceName = provinceJson.keySet().iterator().next();
                        JSONArray citiesJsonArray = provinceJson.getJSONArray(provinceName);
                        List<String> cities = new ArrayList<>();
                        for (int j = 0; j < citiesJsonArray.size(); j++) {
                            String city = citiesJsonArray.getString(j);
                            cities.add(city);
                        }
                        provinceAndCity.setProvince(provinceName);
                        provinceAndCity.setCity(cities);
                        provinceAndCityRepository.save(provinceAndCity);
                    }

                    log.info("init data end");
                }
            }
        };
    }

    @Bean
    public CommandLineRunner esDataInit(NewsDocRepository newsDocRepository) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                if (!enableInitEsData) {
                    log.info("init data disabled");
                    return;
                }
                log.info("init data start");
                InputStream inputStream1 = new ClassPathResource("data_169.json").getInputStream();
                JSONArray jsonArray1 = JSONArray.parseArray(IoUtil.readUtf8(inputStream1));
                long index = 1;
                for (int i = 0; i < jsonArray1.size(); i++) {
                    JSONObject jsonObject = jsonArray1.getJSONObject(i);
                    NewsInit newsInit1 = JSONObject.toJavaObject(jsonObject, NewsInit.class);
                    if (NewsFilter.filter(newsInit1)) {
                        String text = newsInit1.getContent();
                        NewsDoc newsDoc = NewsDoc.builder().id(index).title(newsInit1.getTitle()).date(newsInit1.getDate())
                                .content(newsInit1.getContent()).source(newsInit1.getSource())
                                .category(Category.string2Enum((String) jsonObject.get("category"))).keywords(newsInit1.getKeywords())
                                .standPoint(newsInit1.getStandpoint())
                                .build();
                        ++index;
                        try {
                            newsDocRepository.save(newsDoc);
                        } catch (Exception exception) {
                            if (!(exception.getMessage()).contains("Created")) {
                                throw exception;
                            }
                        }
                    }
                }
                log.info("init data end");
            }
        };
    }

}

