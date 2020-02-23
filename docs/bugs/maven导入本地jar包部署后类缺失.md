项目中需要引入maven中没有得jar包，就把包拷贝到项目中的一个文件夹，用`scope`和`systemPath`标志就可以导入

```xml
        <dependency>
            <groupId>com.pingan.traffic.corder</groupId>
            <artifactId>threedes</artifactId>
            <version>1.0.0</version>
            <scope>system</scope>
            <systemPath>${basedir}/lib/threedes-1.0.0.jar</systemPath>
        </dependency>
```

以上做法对本地运行是没问题的，但是打包部署后，当代码运行时使用到导入的jar包中的类的时候就会发现系统报错，类找不到ClassNotFound！而且整个打包部署运行都不会报任何错误！

解决办法：pom中的maven插件加上`includeSystemScope`属性，打包的时候，就会带上导入的本地jar

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <mainClass>com.pa.city.traffic.fsdjpt.biz.FsdjptApplication</mainClass>
        <fork>true</fork>
        <includeSystemScope>true</includeSystemScope>
    </configuration>
</plugin>
```

