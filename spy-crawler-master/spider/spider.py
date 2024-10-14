'''
获取 新浪滚动新闻 的链接
存储为csv文件
'''
from concurrent.futures import ThreadPoolExecutor

import requests
from bs4 import BeautifulSoup
import pika
import json
import time
import random


def read_id_from_file():
    try:
        with open("id.txt", "r") as file:
            return int(file.read())
    except FileNotFoundError:
        return 500


def write_id_to_file(id_value):
    with open("id.txt", "w") as file:
        file.write(str(id_value))


def get_id():
    id_value = read_id_from_file()
    id_value += 1
    write_id_to_file(id_value)
    return id_value


def get_headers():
    headers_list = [
        'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36',
        'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:91.0) Gecko/20100101 Firefox/91.0',
        'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36 Edg/93.0.961.47'
    ]
    # 随机获取一个headers
    headers = {'User-Agent': random.choice(headers_list)}
    return headers


def get_URL(i):
    init_url = 'https://feed.mix.sina.com.cn/api/roll/get?pageid=153&lid=2509&k=&num=50&page={}'  # 全部
    # init_url = 'https://feed.mix.sina.com.cn/api/roll/get?pageid=153&lid=2513&k=&num=50&page={}'#娱乐
    # init_url = 'https://feed.mix.sina.com.cn/api/roll/get?pageid=153&lid=2511&k=&num=50&page={}'#国际
    # init_url = 'https://feed.mix.sina.com.cn/api/roll/get?pageid=153&lid=2512&k=&num=50&page={}'#体育
    # init_url = 'https://feed.mix.sina.com.cn/api/roll/get?pageid=153&lid=2514&k=&num=50&page={}'#军事
    # init_url = 'https://feed.mix.sina.com.cn/api/roll/get?pageid=153&lid=2515&k=&num=50&page={}'  # 科技
    # init_url = 'https://feed.mix.sina.com.cn/api/roll/get?pageid=153&lid=2516&k=&num=50&page={}'#财经
    page = requests.get(url=init_url.format(i), headers=get_headers()).json()
    links = []
    for j in range(50):
        urls = page['result']['data'][j]['url']
        links.append(urls)
    return links


# 获取单条新闻的内容
def get_detail(url):
    res = requests.get(url)
    # print(res.encoding)
    res.encoding = 'utf-8'
    news = {}
    soup = BeautifulSoup(res.text, 'html.parser')
    try:
        text = soup.find('div', id='artibody')
        if not text:
            text = soup.find('div', id='article_content')
        all_p_tags = text.find_all('p')
        content = ''
        for p_tag in all_p_tags:
            if 'class' in p_tag.attrs and 'article-editor' in p_tag['class']:
                continue
            content += p_tag.get_text().strip()
        title = soup.select(".main-title")[0].text
        if not title:
            title = soup.select('#artibodyTitle')[0].text
        date = soup.select('.date')[0].text
        if not date:
            date = soup.select('#pub_date')[0].text
        source = soup.select('.source')[0].text
        if not source:
            source = soup.select('[data-sudaclick="media_name"]')[0].text
        news['id'] = get_id()
        news['url'] = url
        news['title'] = title
        news['content'] = content
        # news['date'] = soup.select('.date-source')[0].contents[1].text
        news['date'] = date
        # news['source'] = soup.select('.date-source a')[0].text
        news['source'] = source
        return news
    except Exception as e:
        return news


# news_num: 获取新闻的数量 page_num: 获取新闻链接的页数
# 默认1页50条新闻，但是不一定每页都有50条新闻
# 返回的结果不一定有nes_num条，因为会过滤掉空值
def get_news(news_num=100, page_num=50):
    link_list = []
    for i in range(1, page_num + 1):
        try:
            links = get_URL(i)
            link_list = link_list + links
        except:
            # print("第" + str(i) + "页链接获取失败")
            pass
        else:
            # print("第" + str(i) + "页链接已经全部获取")
            pass
    # res = []
    # with ThreadPoolExecutor(16) as executor:
    #     res = list(executor.map(get_detail, link_list))
    res = []
    for i in range(0, news_num):
        news = get_detail(link_list[i])
        if all(news.values()):
            res.append(news)
    return res


# data是一个list
def send_to_rabbitmq(data, queue_name):
    credentials = pika.PlainCredentials('admin', 'adminspy')  # mq用户名和密码
    # 虚拟队列需要指定参数 virtual_host，如果是默认的可以不填。
    connection = pika.BlockingConnection(
        pika.ConnectionParameters(host='123.60.77.90', port=5672, virtual_host='/', credentials=credentials))
    channel = connection.channel()
    # 声明消息队列，消息将在这个队列传递，如不存在，则创建
    channel.queue_declare(queue=queue_name, durable=True)

    # 遍历JSON列表，发送每个条目到 RabbitMQ
    for item in data:
        # 将条目转换为 JSON 字符串
        message = json.dumps(item)
        # 发送消息到队列
        channel.basic_publish(exchange='', routing_key=queue_name, body=message.encode('utf-8'))
        time.sleep(10)
    connection.close()


if __name__ == "__main__":
    while True:
        news_num = random.randint(90, 110)
        data = get_news(news_num, 50)
        send_to_rabbitmq(data, 'python_news')
        send_to_rabbitmq(data, 'python_news_neo4j')
        send_to_rabbitmq(data, 'test')
        # 每隔一天爬取一次
        time.sleep(24 * 60 * 60)
