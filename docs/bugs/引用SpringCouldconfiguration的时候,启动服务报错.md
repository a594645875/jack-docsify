#### 报错:引用SpringCouldconfiguration的时候,启动服务报错

报错:引用`SpringCouldconfiguration`的时候,启动服务报错

```
Failed to auto-configure a DataSource: 'spring.datasource.url' is not specified and no embedded datasource could be auto-configured.
Reason: Failed to determine a suitable driver class
```

分析:没能启动云配置,缺少`spring-cloud-stater-config`依赖

解决办法:添加依赖

```
<dependency>
<groupId>org.springframework.cloud</groupId>
<artifactId>spring‐cloud‐starter‐config</artifactId>
</dependency>
```

