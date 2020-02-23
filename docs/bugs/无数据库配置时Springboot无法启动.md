#### 无数据库配置时Springboot无法启动

- 日志

```
Description:

Failed to configure a DataSource: 'url' attribute is not specified and no embedded datasource could be configured.
```

- 解决办法: 在启动类配置上排除数据库自动配置

```
@SpringBootApplication(scanBasePackages = "com.funtl.myshop",exclude = DataSourceAutoConfiguration.class)
```
