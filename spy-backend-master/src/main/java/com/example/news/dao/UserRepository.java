package com.example.news.dao;

import com.example.news.pojo.entity.mysql.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    //List<User> findByRole(User.Role role);
    Boolean existsByUserId(String userId);
}
