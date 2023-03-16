import requests as rq
from bs4 import BeautifulSoup as bs
from urllib import parse
import random
import time
import json
from scrapy import Selector

headers = [
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36",
    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/22.0.1207.1 Safari/537.1",
    "Mozilla/5.0 (X11; CrOS i686 2268.111.0) AppleWebKit/536.11 (KHTML, like Gecko) Chrome/20.0.1132.57 Safari/536.11",
    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.6 (KHTML, like Gecko) Chrome/20.0.1092.0 Safari/536.6",
    "Mozilla/5.0 (Windows NT 6.2) AppleWebKit/536.6 (KHTML, like Gecko) Chrome/20.0.1090.0 Safari/536.6",
    "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/19.77.34.5 Safari/537.1",
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.9 Safari/536.5",
    "Mozilla/5.0 (Windows NT 6.0) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.36 Safari/536.5",
    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1063.0 Safari/536.3",
    "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1063.0 Safari/536.3",
    "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; SE 2.X MetaSr 1.0; SE 2.X MetaSr 1.0; .NET CLR 2.0.50727; SE 2.X MetaSr 1.0)",
    "Mozilla/5.0 (Windows NT 6.2) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1062.0 Safari/536.3",
    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1062.0 Safari/536.3",
    "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; 360SE)",
    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1061.1 Safari/536.3",
    "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1061.1 Safari/536.3",
    "Mozilla/5.0 (Windows NT 6.2) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1061.0 Safari/536.3",
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.24 (KHTML, like Gecko) Chrome/19.0.1055.1 Safari/535.24",
    "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/535.24 (KHTML, like Gecko) Chrome/19.0.1055.1 Safari/535.24"
]


ids = []
count = 0

film_listurl = 'https://movie.douban.com/j/new_search_subjects?sort={sorttype}&range=0,10&tags=%E7%94%B5%E5%BD%B1&start={page}&countries={country}'
film_url = 'https://movie.douban.com/subject/{id}/'
countrylist = ['中国大陆','香港','台湾']
# 可选择的排序方法
# U近期热门，S评分最高，R最新上映，T标记最多
sortlist = ['U','S','R','T']

proxies = {'https':'http://127.0.0.1:7980',
            'http':'http://127.0.0.1:7980'
}

fileObject = open('movie.json', 'a+', encoding='utf-8')

def start_requests():
    for sorttype in sortlist:
        for country in countrylist:
            country = parse.quote(country)
            for i in range(0,500):
                header = random.choice(headers)
                response = rq.request('Get', film_listurl.format(page=i * 20, country=country, sorttype=sorttype), headers={"User-Agent": header})
                parse_html(response)



def parse_html(response):
    """
    解析页面，获取电影id，构造电影链接
    :param response: Response对象
    :return:
    """
    result = json.loads(response.text)
    if result.get('data'):
        filmlist = result.get('data')
        if filmlist != []:
            for film in filmlist:
                # 获取电影id
                id = film.get('id')
                title = film.get('title')
                # 已经爬好的id不再爬
                if(id in ids):
                    continue
                time.sleep(0.3)
                ids.append(id)
                global count
                count += 1
                header = random.choice(headers)
                response = rq.request('Get', film_url.format(id=id), headers={"User-Agent": header})
                parse_film(id, response)


def parse_film(id, response):
    """
    解析单部电影详情信息
    :param response: Response对象
    :return:
    """
    selector = Selector(response=response)
    # id
    # 电影名字
    title = selector.xpath('//span[@property="v:itemreviewed"]/text()').extract()[0]
    # 年份
    try:
        year = selector.xpath('//*[@id="content"]/h1/span[2]/text()').extract()[0][1:5]
    except IndexError:
        year = None
    #图片
    try:
        picture = selector.xpath('//*[@id="mainpic"]/a/img').attrib['src']
    except IndexError:
        picture = None
    # 制片国家
    region = selector.xpath('//*[@id="info"]').re('制片国家/地区:</span>\s(.*)<br>')
    # 语言
    language = selector.xpath('//*[@id="info"]').re('语言:</span>\s(.*)<br>')
    # 导演
    director = selector.xpath('//*[@rel="v:directedBy"]/text()').extract()
    # 类型
    type = selector.xpath('//*[@property="v:genre"]/text()').extract()
    # 演员
    actor = selector.xpath('//*[@rel="v:starring"]/text()').extract()
    # 上映日期
    date = selector.xpath('//span[@property="v:initialReleaseDate"]/text()').extract()
    # 片长
    runtime = selector.xpath('//span[@property="v:runtime"]/text()').extract()
    # 评分
    try:
        rate = selector.xpath('//strong[@property="v:average"]/text()').extract()[0]
    except IndexError:
        rate = None
    # 评价人数
    try:
        rating_num = selector.xpath('//span[@property="v:votes"]/text()').extract()[0]
    except IndexError:
        rating_num = None
    global count
    field_map = {
        'cnt': count, 'id': id, 'title': title, 'picture':picture, 'year': year, 'region': region, 'language': language,
        'director': director, 'type': type, 'actor': actor, 'date': date,
        'runtime': runtime, 'rate': rate, 'rating_num': rating_num
    }

    jsObj = json.dumps(field_map, ensure_ascii=False, indent=4)
    fileObject.write(jsObj)

start_requests()
fileObject.close()