package com.example.news.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.news.pojo.vo.common.ResultResponse;
import com.example.news.pojo.vo.neo4j.NewsInfoVO;
import com.example.news.service.Neo4jService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/neo4j")
//@SaCheckLogin
public class Neo4jController {
    @Autowired
    private Neo4jService neo4jService;

    @GetMapping("/getNews/{newId}")
    public ResultResponse func(@PathVariable("newId") Long newId) {
        NewsInfoVO newsInfo = neo4jService.getNewsInfo(newId);
        if (newsInfo == null) {
            return ResultResponse.error("大模型生成知识图谱失败");
        } else {
            return ResultResponse.success(newsInfo);
        }
    }

//    //根据newsId，返回实体的categories
//    @GetMapping("/category/{newId}")
//    public ResultResponse func1(@PathVariable("newId") Long newId) {
//        return ResultResponse.success(neo4jService.getCategoryByNewsId(newId));
//    }
//
//    //根据newsId，返回实体的linkData
//    @GetMapping("/relation/{newId}")
//    public ResultResponse func2(@PathVariable("newId") Long newId) {
//        return ResultResponse.success(neo4jService.getLinkDataByNewsId(newId));
//    }
//
//    //根据newsId，返回实体节点Data
//    @GetMapping("/node/{newId}")
//    public ResultResponse func3(@PathVariable("newId") Long newId) {
//        return ResultResponse.success(neo4jService.getEntityNodeByNewsId(newId));
//    }


}
