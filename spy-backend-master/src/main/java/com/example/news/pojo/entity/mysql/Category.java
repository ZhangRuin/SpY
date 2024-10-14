package com.example.news.pojo.entity.mysql;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Category {
    //财经、彩票、房产、股票、家居、教育、科技、社会、时尚、时政、体育、星座、游戏、娱乐
    //成员
    FINANCE("财经"),
    LOTTERY("彩票"),
    HOUSE("房产"),
    STOCK("股票"),
    HOME("家居"),
    EDUCATION("教育"),
    TECHNOLOGY("科技"),
    SOCIETY("社会"),
    FASHION("时尚"),
    POLITICS("时政"),
    SPORTS("体育"),
    CONSTELLATION("星座"),
    GAME("游戏"),
    ENTERTAINMENT("娱乐");

    // 成员变量
//    @JsonValue
    private String name;

    // 构造方法
    private Category(String name) {
        this.name = name;
    }
    // get set 方法
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public static Category string2Enum(String name) {
        for (Category category : Category.values()) {
            if (category.getName().equals(name)) {
                return category;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }
}
