# 后端项目



## 数据更新配置说明（2024-3-11）
1. 在自己的MySQL数据库中删库并重新建库。
```drop database news; create database news;```
2. 在application.yml文档中将数据库的密码改成自己的MySQL数据库密码。
3. 第一次运行时将enable-init-data属性值改为true，运行后将数据导入数据库，之后运行后端时再将其改为false，以免多次导入重复数据
4. 注意本机开放端口：9010
5. 本次数据更新后的新闻总数应为4981条
6. 首次运行时将enable-init-data属性值改为true，将省份数据导入数据库

