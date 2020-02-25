#### springcloud config客户端无法获取云配置

问题:`pring cloud config`客户端一直无法获取云配置

分析:一开始以为是配置文件出现问题,经过多重检查,发现是依赖错了,

解决:把

```
<dependency>
<groupId>org.springframework.cloud</groupId>
<artifactId>spring-cloud-config</artifactId>
</dependency>
```

更换为:

```
<dependency>
<groupId>org.springframework.cloud</groupId>
<artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

就好了