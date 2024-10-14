package com.example.news.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hankcs.hanlp.restful.HanLPClient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyHanLPClient extends HanLPClient {
    private String url;
    private String auth;
    private String language;
    private int timeout;
    private ObjectMapper mapper;

    public MyHanLPClient(String url, String auth, String language, int timeout) {
        super(url, auth, language, timeout);
    }

    public MyHanLPClient(String url, String auth) {
        super(url, auth);
    }


}
