#### 1. 数据库设计要合理(3F)
- 第一范式(确保每列保持原子性)
- 第二范式(确保表中的每列都和主键相关)
- 第三范式(确保每列都和主键列直接相关,而不是间接相关)
#### 2. 添加索引(普通索引,主键索引,唯一索引,全文索引)
- explain 搜索计划,可以查看sql语句执行有没有使用索引
- 索引优缺点:
    - 优点 :提高程序效率 
    - 缺点: 增删慢,索引文件需要更新,
- 使用注意:
    - 组合索引,使用两个条件或者第一个条件可以用到索引,使用第二个条件不会用到索引
    - 使用like '%xxx%'不会使用索引,使用`'xxx%'`可以使用索引
    - 使用or 需要所有条件都有索引,任一条件无索引则全表扫描
    - 判断null,要使用` is null`,不要使用` = null`
    - group by 会全表扫描
    - 分组的时候,禁止排序,可以提高查询效率,order by null
    - 使用`>=`的时候,会进行两次全表扫描,所以尽量用`>`
    - in 和 not in 会全表扫描,尽量不要使用
    - 查询量非常大,缓存,分表,分页
#### 3. 分表分库技术(取模分表,水平分割,垂直分割)
- 什么时候分库
电商项目将一个项目进行拆分,拆分多个小项目,每个小的项目有自己单独数据库,互不影响----垂直分割 会员数据库,订单数据库,支付数据库
- 什么时候分表
水平分割 分表根据业务需求
例如 存放日志(每年存放)根据年份表;腾讯QQ号,根据位数不均匀,手机号前三位,`取模算法(保证均匀)`
#### 4. 读写分离
#### 5. 存储过程
#### 6. 配置mysql最大连接数(my.ini)
#### 7. mysql服务器升级
#### 8. 随时清理碎片化
- optimize table 表名: 清理碎片化,myisam删除时,不是真实删除
#### `9. sql语句优化`
#### 其他
- mysql存储引擎
- Myisam,InnoDB,Memory
- InnoDB主流,有事务功能
- Myisam有全文索引
- Myisam表锁,InnoDB行锁
- 都支持B-Tree数据结构