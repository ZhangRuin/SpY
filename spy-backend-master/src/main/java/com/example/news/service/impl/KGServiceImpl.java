package com.example.news.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.example.news.pojo.vo.kg.Event;
import com.example.news.pojo.vo.kg.KGResult;
import com.example.news.service.AIService;
import com.example.news.service.KGService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class KGServiceImpl implements KGService {
    @Autowired
    private AIService aiService;

    public final String promptKG = "实体：在知识图谱中，实体代表了现实世界中的个体或者概念。关系用来描述实体之间的连接或联系，它表示了实体之间的某种关联关系。下面给出的关系类型中比如(\"地点\", \"人物\", \"祖籍\")，第一个元素表示\"地点\"表示宾语，第二个元素\"人物\"表示主语，第三个元素\"祖籍\"表示关系类型，所以这个三元组表示人物的祖籍是地点。对于给定的文本，需要从中识别出实体和它们之间的关系，你输出的实体类型是下面给出的实体类型(可以有别的类型)，输出的关系类型是下面给出的关系类型(可以有别的类型)，输出json格式的字符串，你必须按照示例的格式输出，确保括号和引号输出正确。输出内容要保证是完整正确的json段。\n" +
            "实体类型:人物,影视作品,生物,Number,Date,国家,网站,网络小说,图书作品,歌曲,地点,气候,行政区,TEXT,历史人物,学校,企业,出版社,书籍,音乐专辑,城市,景点,电视综艺,机构,作品,语言,学科专业\n" +
            "关系类型:(\"地点\", \"人物\", \"祖籍\"),(\"人物\", \"人物\", \"父亲\"),(\"地点\", \"企业\", \"总部地点\"),(\"地点\", \"人物\", \"出生地\"),(\"Number\", \"行政区\", \"面积\"),(\"Text\", \"机构\", \"简称\"),(\"Date\", \"影视作品\", \"上映时间\"),(\"人物\", \"人物\", \"妻子\"),(\"音乐专辑\", \"歌曲\", \"所属专辑\"),(\"Number\", \"企业\", \"注册资本\"),(\"城市\", \"国家\", \"首都\"),(\"人物\", \"影视作品\", \"导演\"),(\"Text\", \"历史人物\", \"字\"),(\"Number\", \"人物\", \"身高\"),(\"企业\", \"影视作品\", \"出品公司\"),(\"Number\", \"学科专业\", \"修业年限\")\n" +
            "input:合肥土地市场迎来龙年首场土拍，共计3宗涉宅地块出让，地块均溢价成交，最高溢价率达54%，共计成交金额32.3亿元。\n" +
            "output:{\"entities\":[{\"type\":\"地点\",\"value\":\"合肥\"},{\"type\":\"Date\",\"value\":\"龙年\"},{\"type\":\"Number\",\"value\":\"3宗\"},{\"type\":\"Text\",\"value\":\"涉宅地块\"},{\"type\":\"Number\",\"value\":\"54%\"},{\"type\":\"Number\",\"value\":\"32.3亿元\"}],\"relations\":[{\"first\":{\"type\":\"地点\",\"value\":\"合肥\"},\"second\":{\"type\":\"Date\",\"value\":\"龙年\"},\"relation\":\"时间地点\"},{\"first\":{\"type\":\"Number\",\"value\":\"3宗\"},\"second\":{\"type\":\"Text\",\"value\":\"涉宅地块\"},\"relation\":\"数量类型\"},{\"first\":{\"type\":\"Number\",\"value\":\"54%\"},\"second\":{\"type\":\"Text\",\"value\":\"涉宅地块\"},\"relation\":\"属性数值\"},{\"first\":{\"type\":\"Number\",\"value\":\"32.3亿元\"},\"second\":{\"type\":\"Text\",\"value\":\"涉宅地块\"},\"relation\":\"价格类型\"}]}\n" +
            "input:";
    private final String promptEvent = "信息抽取中事件的定义：事件是发生在某个特定的时间段、某个特定的地域范围内，由一个或者多个角色参与的一个或者多个动作组成的事件或者状态的改变。事件类型：不同动作或者状态的改变代表不同类型的事件。事件元素：同一类型的事件中不同的时间、地点、角色代表了不同的事件实例。你需要提取出事件的类型和事件发生的时间，并按照时序关系（即事件发生的时间顺序升序）输出，未给出具体时间的事件不要输出，下面的示例中一个输入可能会有多个事件，按照示例的格式输出。\n" +
            "input:1992年10月3日，奥巴马与米歇尔在三一联合基督教堂结婚。\n" +
            "output:[{\"event\":\"结婚\",\"time\":\"1992年10月3日\"}]\n" +
            "input:《每日经济新闻》获悉，近日英国富时罗素指数公司公布了富时全球股票指数半年度指数审查报告。根据报告，富时全球股票指数系列将新调入A股76只、调出1只。据悉，此次被调入的大盘股共有10只，包括首创证券、石药集团、国博电子、海光信息、建元信托、华大智造、荣昌生物、联影医疗、江波龙、唯捷创芯等；微盘股有2只，分别为容知日新和科净源；同时还调入了数十只小盘股。根据近日市值计算，A股占富时罗素新兴市场指数市值将从约5.71%增加至约6.18%，占富时全球指数市值从约0.55%增加至约0.61%。调整措施将于3月4日生效，3月15日进行指数调仓。\n" +
            "output:[{\"event\":\"调整措施将于3月4日生效\",\"time\":\"3月4日\"},{\"event\":\"3月15日进行指数调仓\",\"time\":\"3月15日\"}]\n" +
            "input:来源：格隆汇格隆汇2月29日丨金花股份(600080.SH)公布，截止2024年2月29日，公司通过集中竞价交易方式累计回购公司股份1123.29万股，已回购股份占公司总股本的3.00%，与上次披露数相比增加0.48%，回购成交的最高价为7.79元/股，最低价为6.90元/股，已支付的资金总额为人民币8386.34万元（不含印花税、交易佣金等交易费用）。\n" +
            "output:[{'event': '公司回购股份', 'time': '2024年2月29日'}, {'event': '回购股份占公司总股本的3.00%', 'time': '2024年2月29日'}, {'event': '回购成交的最高价为7.79元/股，最低价为6.90元/股', 'time': '2024年2月29日'}, {'event': '已支付的资金总额为人民币8386.34万元', 'time': '2024年2月29日'}]\n" +
            "input:";

    public final String getPromptKG(String question) {
        return promptKG + question + "\n" + "output:";
    }

    private String getPromptEvent(String question) {
        return promptEvent + question + "\n" + "output:";
    }

    @Override
    public KGResult getKGResult(String content) {
        int maxRetries = 2; // 最大重试次数
        int retryCount = 0;
        KGResult kgResult = null;
        while (retryCount < maxRetries) {
            try {
                String answer = aiService.sendQuestion(getPromptKG(content));
                answer = answer.replace('【','{');
                answer = answer.replace('】','}');

                log.info("大模型回答结果:   " + answer);
                log.info("json转换:   "+JSON.parseObject(answer, KGResult.class));
                kgResult = JSON.parseObject(answer, KGResult.class);  //这里有问题
                break; // 如果成功解析结果，则跳出循环
            } catch (Exception e) {
                log.info("Exception caught: " + e.getMessage());
                retryCount++;
                if (retryCount < maxRetries) {
                    log.info("Get kg retrying... Attempt " + retryCount);
                } else {
                    log.info("Get kg max retries exceeded. Exiting loop.");
                }
            }
        }
        return kgResult;
    }

    @Override
    public List<Event> getEvents(String content) {
        int maxRetries = 1; // 最大重试次数
        int retryCount = 0;
        List<Event> events = null;
        while (retryCount < maxRetries) {
            try {
                String answer = aiService.sendQuestion(getPromptEvent(content));
                events = JSONArray.parseArray(answer, Event.class);
                break; // 如果成功解析结果，则跳出循环
            } catch (Exception e) {
                return null;
            }
        }
        return events;
    }

}
