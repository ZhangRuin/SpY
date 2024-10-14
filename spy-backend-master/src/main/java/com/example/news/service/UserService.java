package com.example.news.service;

public interface UserService {
    Boolean loginCheck(String userId, String password);

    Boolean register(String userId, String password);
}
