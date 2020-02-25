#### Mybatis Plus 插入数据类型为numeric的数据时出错

- 问题: 在使用String向Pgsql插入数据类型为numeric的数据时出错,显示类型不匹配
- 分析: 看到旧代码上用的是BigDecimal映射数据库的numeric
- 解决: 把对应的实体类属性由String改为BigDecimal.