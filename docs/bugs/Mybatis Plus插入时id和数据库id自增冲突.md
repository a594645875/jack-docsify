#### Mybatis Plus插入时id和数据库id自增冲突

- 问题: 使用Mybatis Plus批量插入时,显示id类型不匹配
- 分析: Mybatis Plus默认会在插入的时候生成一个id,所以出错是因为没有调用数据库的主键自增函数
- 解决办法: 在id上加上`@TableId`注解,并定义id模式

```
   AUTO->"数据库ID自增"
   INPUT-> 用户输入ID"
   ID_WORKER->"全局唯一ID"
   UUID->"全局唯一ID"
```