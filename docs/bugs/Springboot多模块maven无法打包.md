从网上下载了一个api工程，把该api工程以模块的形式新构建了一个工程。在运行的时候能正常运行，但是maven打包的时候却出现各种问题无法打包。

其实主要几点：
切记父文件不需要打包和对应的模块不要写build属性，在主模块写就行。
因为对应的模块文件不需要打包，他们只需要install就可以了，因为我的主模块需要到对应包中的依赖，打包了就找不到了

```xml
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <mainClass>com.czc.wedid.App</mainClass>
                    <fork>true</fork>
                    <fork>true</fork>
                </configuration>
            </plugin>
        </plugins>
    </build>
```

