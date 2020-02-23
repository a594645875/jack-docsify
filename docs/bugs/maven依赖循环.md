#### maven项目中springcloud中的健康检查spring-boot-starter-actuator和jdbc之间形成循环依赖

报错:依赖循环

```
Description:

The dependencies of some of the beans in the application context form a cycle:

   servletEndpointRegistrar defined in class path resource [org/springframework/boot/actuate/autoconfigure/endpoint/web/ServletEndpointManagementContextConfiguration.class]
      ↓
   healthEndpoint defined in class path resource [org/springframework/boot/actuate/autoconfigure/health/HealthEndpointConfiguration.class]
      ↓
   org.springframework.boot.actuate.autoconfigure.jdbc.DataSourceHealthIndicatorAutoConfiguration
┌─────┐
|  dataSource
↑     ↓
|  scopedTarget.dataSource defined in class path resource [org/springframework/boot/autoconfigure/jdbc/DataSourceConfiguration$Hikari.class]
↑     ↓
|  org.springframework.boot.autoconfigure.jdbc.DataSourceInitializerInvoker
└─────┘
```

分析:SpringBoot2.0.1的bug,

办法:把

```
<parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.0.1.RELEASE</version>
</parent>
```

中的2.0.1.RELEASE,改成2.0.2.RELEASE即可解决