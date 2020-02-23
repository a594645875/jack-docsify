# POI处理表格出现异常找不到类XSSFWorkbookFactory

使用poi导入表格处理数据的时候，出现了以下异常

```java
ClassNotFoundException: org.apache.poi.xssf.usermodel.XSSFWorkbookFactory
```

经过百度查询，知道使用07版Excel只导入poi依赖还不够，还需要导入poi-ooxml，如下

```xml
<!-- https://mvnrepository.com/artifact/org.apache.poi/poi -->
<dependency>
  <groupId>org.apache.poi</groupId>
  <artifactId>poi</artifactId>
  <version>4.0.0</version>
</dependency>
<!-- https://mvnrepository.com/artifact/org.apache.poi/poi-ooxml -->
<dependency>
  <groupId>org.apache.poi</groupId>
  <artifactId>poi-ooxml</artifactId>
  <version>4.0.0</version>
</dependency>
```

