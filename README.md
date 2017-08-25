# 前言
>此项目主要用于爬取新浪新闻的的标题，内容，网址，并且对内容进行倒排，实现模糊查询。

# 设计思想
>１、将爬取到的新闻标题，内容存放到mysql数据库,再将每条新闻的ｉｄ，title,centent,存放到hdfs<br>
>2、对hdfs中的文章内容进行分词，并记录每个词的所在句的id写在单词后面，用逗号隔开。<br>
>３、模糊查询，显示单词索引的文章内容<br>

# 目录
>1. SinaNewsSpider主要用于爬去新闻和入库（数据库和hdfs）<br>
>2. SinaNewsSearch 用于建立倒排索引，前端显示

# 环境
>１、hadoop 2.6.0<br>
>2、mysql5.7<br>
>3、jdk1.8

#运行流程

### 先运行SinaNewsSpider工程
>1、执行org.liky.sina.craw.URLDemo类<br>
>2、执行org.liky.sina.index.IndexCreator类

### 再运行　SinaNewsSearch工程



