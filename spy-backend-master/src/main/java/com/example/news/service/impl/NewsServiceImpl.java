package com.example.news.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.example.news.config.RedisConstantsConfig;
import com.example.news.dao.*;
import com.example.news.pojo.entity.mysql.*;
import com.example.news.pojo.document.es.NewsDoc;
import com.example.news.pojo.vo.kg.Event;
import com.example.news.pojo.vo.news.*;
import com.example.news.pojo.vo.page.PagedResult;
import com.example.news.service.AIService;
import com.example.news.service.HanLPService;
import com.example.news.service.KGService;
import com.example.news.service.NewsService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsServiceImpl implements NewsService {
    private final NewsEventRepository newsEventRepository;
    private final NewsRepository newsRepository;
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;
    private final ProvinceAndCityRepository provinceAndCityRepository;
    private final KGService kgService;
    private final MediaRepository mediaRepository;
    @Autowired
    private AIService aiService;
    @Autowired
    private HanLPService hanLPService;
    @Autowired
    private NewsCredibilityRepository newsCredibilityRepository;
    @Autowired
    private ZplusAndZminusRepository zplusAndZminusRepository;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedisConstantsConfig redisConstantsConfig;


    @Override
    public long count() {
        return newsRepository.count();
    }

    @Override
    public void addNews(News news) {
        newsRepository.save(news);
    }

    @Override
    public void updateNews(Long id, News news) {
        news.setId(id);
        newsRepository.save(news);
    }

    @Override
    public News getNews(Long id) {
        Optional<News> optionalNews = newsRepository.findById(id);
        return optionalNews.orElse(null);
    }

    @Override
    public void deleteNews(Long id) {
        newsRepository.deleteById(id);
    }

    // 使用ElasticSearch数据库
    @Override
    public PagedResult<NewsDoc> search(SearchRequest request) {
        Pageable pageable = PageRequest.of(request.getCurrentPage() - 1, request.getPageSize());

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // keyword关键字匹配
        if (!request.getKeyword().equals(""))
            boolQueryBuilder.must(QueryBuilders.multiMatchQuery(request.getKeyword(), "title"));
        // date范围筛选
        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("date");
        if (request.getStartDate() != null) rangeQuery.gte(request.getStartDate());
        if (request.getEndDate() != null) rangeQuery.lte(request.getEndDate());
        boolQueryBuilder.must(rangeQuery);
        // source来源筛选
        if (!request.getSource().equals(""))
            boolQueryBuilder.must(QueryBuilders.matchQuery("source", request.getSource()));

        // 高亮显示匹配到的关键词
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<em>");
        highlightBuilder.postTags("</em>");

        // 定义查询query
        NativeSearchQuery query = queryBuilder
                .withQuery(boolQueryBuilder)
                .withSorts(SortBuilders.scoreSort().order(SortOrder.DESC))
                .withSorts(SortBuilders.fieldSort("date").order(SortOrder.DESC))
                .withPageable(pageable)
                .withHighlightBuilder(highlightBuilder)
                .build();

        SearchHits<NewsDoc> searchHits = elasticsearchRestTemplate.search(query, NewsDoc.class);
        List<NewsDoc> newsList = new ArrayList<>();
        searchHits.forEach(searchHit -> {
            NewsDoc newsDoc = searchHit.getContent();

            Map<String, List<String>> highlightFields = searchHit.getHighlightFields();
            List<String> highlightedFieldName = highlightFields.get("title");
            if (highlightedFieldName != null) {
                // 处理高亮字段
                String highlightedText = highlightedFieldName.get(0);
                // 将高亮字段与实体对象结合，然后进行处理
                newsDoc.setTitle(highlightedText);
            }
            newsList.add(newsDoc);
        });

        PagedResult<NewsDoc> result = new PagedResult<>();

        result.setList(newsList);
        result.setTotalCount((int) searchHits.getTotalHits());
        log.info("Search result count: " + searchHits.getTotalHits());

        return result;
    }

//    @Override
//    public PagedResult<News> search(SearchRequest request) {
//        Pageable pageable = PageRequest.of(request.getCurrentPage() - 1, request.getPageSize());
//        List<News> newsList = newsRepository.findBySearchCriteria(request.getKeyword(),
//                request.getStartDate(), request.getEndDate(), request.getSource(), pageable);
//
//        PagedResult<News> result = new PagedResult<>();
//        result.setTotalCount(newsList.size());
//        long totalCount = newsRepository.countBySearchCriteria(
//                request.getKeyword(), request.getStartDate(), request.getEndDate(), request.getSource());
//        result.setTotalCount((int) totalCount);
//        log.info("Search result count: " + totalCount);
//        if (newsList.size() > request.getPageSize()) {
//            int fromIndex = (request.getCurrentPage() - 1) * request.getPageSize();
//            int toIndex = fromIndex + request.getPageSize();
//            if (fromIndex >= newsList.size()) {
//                newsList.clear();
//            } else if (toIndex > newsList.size()) {
//                toIndex = newsList.size();
//                newsList = newsList.subList(fromIndex, toIndex);
//            } else {
//                newsList = newsList.subList(fromIndex, toIndex);
//            }
//        }
//        result.setList(newsList);
//        log.info("Search result count: "+ newsList.size());
//
//        return result;
//    }

    @Override
    public List<String> getAllSources() {
        return newsRepository.getAllSources();
    }

    private Date addDate(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTime();
    }

    @Override
    public List<Day14VO> get14Num(Date date) {
        // 获得date日期之前14天的每一天的新闻数量，包括date这一天
        Date start = addDate(date, -13);
        ArrayList<Day14VO> num14VOS = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            Date d = addDate(start, i);
            Date nextDay = addDate(d, 1);
            Long count = newsRepository.countNewsByDate(d, nextDay);
            num14VOS.add(Day14VO.builder().num(count).date(d).build());
        }
        return num14VOS;
    }

    @Override
    public List<Last14DaysNewsVO> last14DaysNews(Date date) {
        // 获得date日期之前14天新闻
        Date start = addDate(date, -13);
        Date end = addDate(date, 1);
        List<News> last14DaysNewsList = newsRepository.findByDateBetween(start, end);
        ArrayList<Last14DaysNewsVO> last14DaysNewsVOS = new ArrayList<>();
        for (News news : last14DaysNewsList) {
            last14DaysNewsVOS.add(Last14DaysNewsVO.builder().title(news.getTitle()).content(news.getContent()).build());
        }
        return last14DaysNewsVOS;
    }

    @Override
    public List<ProvinceCountVO> provinceCount(Date date) {
        String s = stringRedisTemplate.opsForValue().get(redisConstantsConfig.getProvinceCountKey() + date.toString());
        if (StrUtil.isNotEmpty(s)) {
            return JSON.parseArray(s, ProvinceCountVO.class);
        }
        List<ProvinceCountVO> provinceCountVOList = new ArrayList<>();
        List<Last14DaysNewsVO> last14DaysNewsVOS = last14DaysNews(date);
        Map<String, Integer> provinceCountMap = initProvince();
//        List<Province> provinces = initProvince2();
        List<ProvinceAndCity> provinces = provinceAndCityRepository.findAll();
        for (Last14DaysNewsVO news : last14DaysNewsVOS) {
            String content = news.getContent();
            for (ProvinceAndCity provinceAndCity : provinces) {
                for (String city : provinceAndCity.getCity()) {
                    //单篇新闻，每个省份只加一次
                    if (content.contains(city)) {
                        String provinceName = provinceAndCity.getProvince();
                        provinceCountMap.put(provinceName, provinceCountMap.get(provinceName) + 1);
                        break;
                    }
                }
            }
        }
        for (Map.Entry<String, Integer> entry : provinceCountMap.entrySet()) {
            provinceCountVOList.add(ProvinceCountVO.builder().name(entry.getKey()).value(entry.getValue()).build());
        }
        stringRedisTemplate.opsForValue().set(redisConstantsConfig.getProvinceCountKey() + date.toString(),
                JSON.toJSONString(provinceCountVOList), redisConstantsConfig.getProvinceCountTTL());
        return provinceCountVOList;
    }

    @Override
    public Map<String, Integer> initProvince() {
        Map<String, Integer> provinceMap = new HashMap<>();
        provinceMap.put("香港", 0);
        provinceMap.put("澳门", 0);
        provinceMap.put("台湾", 0);
        provinceMap.put("北京", 0);
        provinceMap.put("天津", 0);
        provinceMap.put("上海", 0);
        provinceMap.put("重庆", 0);
        provinceMap.put("河北", 0);
        provinceMap.put("山西", 0);
        provinceMap.put("江苏", 0);
        provinceMap.put("浙江", 0);
        provinceMap.put("安徽", 0);
        provinceMap.put("福建", 0);
        provinceMap.put("湖北", 0);
        provinceMap.put("湖南", 0);
        provinceMap.put("广东", 0);
        provinceMap.put("海南", 0);
        provinceMap.put("四川", 0);
        provinceMap.put("贵州", 0);
        provinceMap.put("云南", 0);
        provinceMap.put("陕西", 0);
        provinceMap.put("甘肃", 0);
        provinceMap.put("青海", 0);
        provinceMap.put("内蒙古", 0);
        provinceMap.put("广西", 0);
        provinceMap.put("西藏", 0);
        provinceMap.put("宁夏", 0);
        provinceMap.put("新疆", 0);
        provinceMap.put("山东", 0);
        provinceMap.put("河南", 0);
        provinceMap.put("辽宁", 0);
        provinceMap.put("吉林", 0);
        provinceMap.put("黑龙江", 0);
        provinceMap.put("江西", 0);
        return provinceMap;
    }


    @Override
    public List<Day2CategoryVO> get2DaysCategory(Date date) {
        // 获得date日期和前一天的新闻分类数量
        List<Day2CategoryVO> day2CategoryVOS = new ArrayList<>();
        // 获得date和date前一天的所有新闻
        List<News> today = newsRepository.findByDateBetween(date, addDate(date, 1));
        List<News> yest = newsRepository.findByDateBetween(addDate(date, -1), date);
        // 遍历category
        for (Category category : Category.values()) {
            long todayCount = today.stream().filter(news -> category.equals(news.getCategory())).count();
            long yestCount = yest.stream().filter(news -> category.equals(news.getCategory())).count();
            day2CategoryVOS.add(Day2CategoryVO.builder()
                    .category(category.getName()).todayNum(todayCount).lastdayNum(yestCount).build());
        }
        return day2CategoryVOS;
    }

    @Override
    public List<Day14CategoryVO> get14DaysCategory(Date date) {
        List<Day14CategoryVO> day14CategoryVOS = new ArrayList<Day14CategoryVO>();
        for (int i = 0; i < 14; i++) {
            Date currentDate = addDate(date, -i);
            Date nextDate = addDate(currentDate, 1);
            // 获得currentDate的所有新闻
            List<News> currentDayNews = newsRepository.findByDateBetween(currentDate, nextDate);
            for (Category category : Category.values()) {
                long count = currentDayNews.stream().filter(news -> category.equals(news.getCategory())).count();
                day14CategoryVOS.add(Day14CategoryVO.builder().date(currentDate).category(category.getName()).num(count).build());
            }
        }
        return day14CategoryVOS;
    }

    @Override
    public String getDay14CategoryPrompt(List<Day14CategoryVO> day14CategoryVOS) {

        StringBuilder prompt = new StringBuilder();
        int l = day14CategoryVOS.size();
        // 创建一个SimpleDateFormat对象，并指定日期格式
        SimpleDateFormat formatter = new SimpleDateFormat("MM月dd日");
        for (int i = 0; i < l; i++) {
            String date=formatter.format(day14CategoryVOS.get(i).getDate());
            prompt.append(date).append("、").append(day14CategoryVOS.get(i).getCategory())
                    .append("、").append(day14CategoryVOS.get(i).getNum()).append(",");
        }
        return prompt.toString();
    }



    @Override
    public List<Day14ProvinceVO> get7DaysProvinceCnt(Date date) {
        List<String> provinceList = Arrays.asList("香港", "澳门", "台湾", "北京", "天津", "上海", "重庆", "河北", "山西", "江苏", "浙江", "安徽", "福建", "湖北", "湖南", "广东", "海南", "四川", "贵州", "云南", "陕西", "甘肃", "青海", "内蒙古", "广西", "西藏", "宁夏", "新疆", "山东", "河南", "辽宁", "吉林", "黑龙江", "江西");

        List<Day14ProvinceVO> Day14ProvinceVOS = new ArrayList<>();
        List<ProvinceAndCity> provinceAndCities = provinceAndCityRepository.findAll();
        for (int i = 0; i < 7; i++) {
            Map<String, Integer> provinceCntMap = initProvince();
            Date currentDate = addDate(date, -i);
            Date nextDate = addDate(currentDate, 1);
            // 获得currentDate的所有新闻
            List<News> currentDayNews = newsRepository.findByDateBetween(currentDate, nextDate);
            for (News news : currentDayNews) {
                String content = news.getContent()+news.getTitle();
                for (ProvinceAndCity provinceAndCity : provinceAndCities) {
                    for (String city : provinceAndCity.getCity()) {
                        //单篇新闻，每个省份只加一次
                        if (content.contains(city)) {
                            String provinceName = provinceAndCity.getProvince();
                            provinceCntMap.put(provinceName, provinceCntMap.get(provinceName) + 1);
                            break;
                        }
                    }
                }
            }
            for (String province : provinceList) {
                long count = provinceCntMap.get(province);
                Day14ProvinceVOS.add(Day14ProvinceVO.builder().date(currentDate).province(province).num(count).build());
            }
        }
        return Day14ProvinceVOS;
    }

    @Override
    public String getProvinceCountPrompt(List<Day14ProvinceVO> day14ProvinceVOS) {
        StringBuilder prompt = new StringBuilder();
        int l = day14ProvinceVOS.size();
        // 创建一个SimpleDateFormat对象，并指定日期格式
        SimpleDateFormat formatter = new SimpleDateFormat("MM月dd日");
        for (int i = 0; i < l; i++) {
            String date=formatter.format(day14ProvinceVOS.get(i).getDate());
            prompt.append(date).append(",").append(day14ProvinceVOS.get(i).getProvince())
                    .append(",").append(day14ProvinceVOS.get(i).getNum()).append(";");
        }
        return prompt.toString();
    }

    @Override
    public StandPointVO getStandpoint(Date date) {
        // 获得date日期的正面新闻数量，因为左闭右开所以写为[0.3,2.0)
        Date start = addDate(date, -13);
        ArrayList<StandPointNum> standPointNums = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            Date d = addDate(start, i);
            Date nextDay = addDate(d, 1);
            Long positiveCount = newsRepository.countNewsByDateAndStandpoint(d, nextDay,
                    0.3, 2.0);
            // 获得date日期的负面新闻数量
            Long negativeCount = newsRepository.countNewsByDateAndStandpoint(d, nextDay,
                    -1.0, -0.3);
            // 获得date日期的中立新闻数量
            Long neutralCount = newsRepository.countNewsByDateAndStandpoint(d, nextDay,
                    -0.3, 0.3);
            standPointNums.add(StandPointNum.builder().date(d).positiveNum(positiveCount)
                    .negativeNum(negativeCount).neutralNum(neutralCount).build());
        }
        Double average = newsRepository.averageStandpointByDate(date, addDate(date, 1));
        return StandPointVO.builder().standPointNums(standPointNums).averageStandPoint(average).build();
    }

    @Override
    public List<OneDayKeywordVO> getKeyword(Date date) {
        // 获得date日期的新闻
        List<News> newsList = newsRepository.findByDateBetween(date, addDate(date, 1));
        // 统计所有新闻的关键词
        Map<String, Integer> keywordMap = new HashMap<>();
        for (News news : newsList) {
            List<String> keywords = news.getKeywords();
            for (String keyword : keywords) {
                keywordMap.put(keyword, keywordMap.getOrDefault(keyword, 0) + 1);
            }
        }
        // 排序
        List<Map.Entry<String, Integer>> list = new ArrayList<>(keywordMap.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        // 取前1000个
        List<OneDayKeywordVO> oneDayKeywordVOS = new ArrayList<>();
        for (int i = 0; i < 1000 && i < list.size(); i++) {
            oneDayKeywordVOS.add(OneDayKeywordVO.builder().name(list.get(i).getKey())
                    .value(Long.valueOf(list.get(i).getValue())).build());
        }
        return oneDayKeywordVOS;
    }

    @Override
    public List<NewsEvent> getEvent(Long newsId) {
        List<NewsEvent> newsEvents = newsEventRepository.findAllByNewsId(newsId);
        if (newsEvents == null || newsEvents.isEmpty()) {
            newsEvents = new ArrayList<>();
            List<Event> events = kgService.getEvents(newsRepository.findById(newsId).get().getContent());
            for (Event event : events) {
                NewsEvent save = newsEventRepository.save(NewsEvent.builder()
                        .newsId(newsId).event(event.getEvent()).time(event.getTime()).build());
                newsEvents.add(save);
            }
        }
        return newsEvents;
    }

    @Override
    public int sourceAnalysis(Long newsId) {
        News news = getNews(newsId);
        String newsSource = news.getSource();
        List<Media> mediaList = mediaRepository.findAll();
        for (Media media : mediaList) {
            String sourceName = media.getName();
            if (newsSource.contains(sourceName) || sourceName.contains(newsSource)) {
                return 100;
            }
        }
        return 0;
    }

    @Override
    public int correspondingCategory(Long newsId) {
        Map<String, String> categoryMap = new HashMap<String, String>() {{
            put("财经", "FINANCE");
            put("房产", "FINANCE");
            put("彩票", "FINANCE");
            put("股票", "FINANCE");
            put("教育", "EDUCATION");
            put("科技", "TECHNOLOGY");
            put("社会", "SOCIETY");
            put("时政", "SOCIETY");
            put("体育", "SPORTS");
            put("游戏", "GAME");
            put("娱乐", "ENTERTAINMENT");
            put("家居", "HOME");
            put("时尚", "FASHION");
        }};
        News news = getNews(newsId);
        String newsSource = news.getSource();
        String category = categoryMap.get(news.getCategory().toString());
        List<Media> mediaList = mediaRepository.findAll();
        for (Media media : mediaList) {
            String sourceName = media.getName();
            if (newsSource.contains(sourceName) || sourceName.contains(newsSource)) {
                List<String> categoryList = media.getCategory();
                if (categoryList.contains(category)) {
                    return 100;
                }
            }
        }
        String aiClassification = aiService.sendQuestion("有一新闻媒体：" + news.getSource() + "。" +
                "请从如下类别找出该媒体可能所属的一个或多个类别：财经， 房产， 彩票， 股票， 教育， 科技， 社会， 时政， 体育， 游戏， 娱乐， 家居， 时尚。" +
                "答案只需要输出类别名，以顿号隔开。结尾不带句号。输出格式示例：财经、房产");

        List<String> aiClassificationList = Arrays.asList(aiClassification.split("、"));
        List<String> answer = new ArrayList<>();
        for (String s : aiClassificationList) {
            answer.add(categoryMap.get(s));
        }
        if (answer.contains(categoryMap.get(news.getCategory().toString()))) {
            return 100;
        }
        return 0;


    }

    @Override
    public int wordAndLogicAnalysis(Long newsId) {
        int score = -1;
        News news = getNews(newsId);
        String answer = aiService.sendQuestion(news.getTitle() + '\n' + news.getContent() + '\n' + "从根据以下十个因素：" +
                "  - 准确性： 准确地传达事实，避免虚假信息或误导性陈述。\n" +
                "  - 客观性： 客观中立，避免主观偏见或情绪化的表达。\n" +
                "  - 清晰度： 清晰易懂，避免使用含糊的词语、过度复杂的句子结构。\n" +
                "  - 公正性： 公正公平，避免歧视性言论或偏袒某一方。\n" +
                "  - 完整性： 全面报道事件的各个方面，避免片面或不完整的报道。\n" +
                "  - 逻辑推理：评估报道中的逻辑推理是否合乎常理。检查推理过程是否正确，是否存在逻辑上的漏洞或矛盾。\n" +
                "  - 因果关系：可能涉及到因果关系的论述，评估这些因果关系是否合理。\n" +
                "  - 比较与对比：报道会进行事物的比较与对比，评估这些比较与对比是否合理。\n" +
                "  - 逻辑结构：报道的整体逻辑结构是否清晰。检查报道是否有明确的主题句和支持句，以及是否遵循逻辑层次的安排。\n" +
                "  - 主题一致性：报道的语言和逻辑是否与报道的主题一致，避免与主题不一致或相悖的情况。\n" +
                "  为这篇文章的语言与逻辑进行打分，每一点满分为10分，只需要分数，满分为100分，输出数字即可，要敢于打低分和高分。只需要输出分数，不需要分析过程。输出格式为：最终结果：xx分。   ");

        Pattern pattern = Pattern.compile("\\d+"); // 匹配一个或多个数字
        Matcher matcher = pattern.matcher(answer);
        if (matcher.find()) {
            String number = matcher.group();
            score = Integer.parseInt(number);
        }
        return score;
    }

    @Override
    public double sentimentAnalysis(Long newsId) {
        hanLPService.setup();
        News news = getNews(newsId);
        double score = -1;
        try {
            double mark = hanLPService.sentimentAnalysis(news.getTitle() + '\n' + news.getContent());
            score = (1 - Math.abs(mark)) * 100;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return score;
    }

    @Override
    public NewsCredibility calculateCredibility(Long newsId) {
        if (newsCredibilityRepository.findByNewsId(newsId).isPresent()) {
            return newsCredibilityRepository.findByNewsId(newsId).get();
        }
        double sourceScore = sourceAnalysis(newsId);
        double wordAndLogicScore = wordAndLogicAnalysis(newsId);
        double sentimentScore = sentimentAnalysis(newsId);
        double categoryScore = correspondingCategory(newsId);
        NewsCredibility news = NewsCredibility.builder().
                build().setNewsId(newsId).setSourceScore(sourceScore).setWordAndLogicScore(wordAndLogicScore)
                .setCategoryScore(categoryScore).setSentimentScore(sentimentScore);
        List<ZplusAndZminus> Zlist = zplusAndZminusRepository.findAll();
        if (Zlist.isEmpty()) {
            Zlist.add(ZplusAndZminus.builder().type("wordAndLogic").zmax(92).zmin(85).build());
            Zlist.add(ZplusAndZminus.builder().type("sentiment").zmax(76).zmin(37.8).build());
        }
        ZplusAndZminus ZwordAndLogic = Zlist.get(0);
        ZplusAndZminus Zsentiment = Zlist.get(1);
        //求Zij、Z+、Z-
        if (wordAndLogicScore > ZwordAndLogic.getZmax()) {
            ZwordAndLogic.setZmax(wordAndLogicScore);
        }
        if (wordAndLogicScore < ZwordAndLogic.getZmin()) {
            if (wordAndLogicScore > 0) {
                ZwordAndLogic.setZmin(wordAndLogicScore);
            }
        }
        if (sentimentScore > Zsentiment.getZmax()) {
            Zsentiment.setZmax(sentimentScore);
        }
        if (sentimentScore < Zsentiment.getZmin()) {
            if (sentimentScore > 0) {
                Zsentiment.setZmin(sentimentScore);
            }
        }
        zplusAndZminusRepository.save(ZwordAndLogic);
        zplusAndZminusRepository.save(Zsentiment);

        double weight1 = 0.5;
        double weight2 = 0.25;
        double weight3 = 0.125;
        double[] Zij = new double[4];
        Zij[0] = sourceScore / 100;
        Zij[1] = (wordAndLogicScore - ZwordAndLogic.getZmin()) / (ZwordAndLogic.getZmax() - ZwordAndLogic.getZmin());
        Zij[2] = (sentimentScore - Zsentiment.getZmin()) / (Zsentiment.getZmax() - Zsentiment.getZmin());
        Zij[3] = (categoryScore) / 100;
        double dplus = Math.sqrt(weight1 * Math.pow(1 - Zij[0], 2) + weight2 * Math.pow(ZwordAndLogic.getZmax() / 100 - Zij[1], 2)
                + weight3 * Math.pow(Zsentiment.getZmax() / 100 - Zij[2], 2) + weight3 * Math.pow(1 - Zij[3], 2));
        double dminus = Math.sqrt(weight1 * Math.pow(Zij[0], 2) + weight2 * Math.pow(ZwordAndLogic.getZmin() / 100 - Zij[1], 2)
                + weight3 * Math.pow(Zsentiment.getZmin() / 100 - Zij[2], 2) + weight3 * Math.pow(Zij[3], 2));
        double total = (dminus / (dminus + dplus)) * 2 - 1;
        if(Double.isInfinite(total)){
            log.error("可信度数值为infinity，出现错误，不存入数据库");
            return null;
        }
        news.setTotalScore(total);
        newsCredibilityRepository.save(news);
        return news;
    }


}
