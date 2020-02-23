#### Spring cloud测试报错: feign.codec.DecodeException

报错: 

```
feign.codec.DecodeException:
Type definition error:
[simple type, class entity.Result];
nested exception is com.fasterxml.jackson.databind.exc.InvalidDefinitionException:
Cannot construct instance of `entity.Result` (no Creators, like default construct, exist):
cannot deserialize from Object value (no delegate- or property-based Creator) at [Source: (PushbackInputStream); line: 1, column: 2]
```

分析: 由于pojo类Result没有写空参构造方法,导致无法构造Result实例

解决办法: 在Result类中添加空参构造方法

#### 