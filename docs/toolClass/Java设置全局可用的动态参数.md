在项目中需要用到一个参数，但是这个参数经常变动，所有想有一种办法经常更新这个值又可以在项目中全局使用

```java
/**
 * 动态参数工具类
 */
public class SystemParamUtil {

    private static final Map<String,Object> PARAMS = new HashMap<>();

    public static Object get(String key) {
        return PARAMS.get(key);
    }

    public static void put(String key, Object value) {
        PARAMS.put(key, value);
    }

}
```

