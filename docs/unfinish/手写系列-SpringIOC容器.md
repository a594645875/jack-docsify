## 手写SpringIOCXML版本

准备一个spring xml文件`spring.xml`

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:p="http://www.springframework.org/schema/p"
	xmlns:context="http://www.springframework.org/schema/context"
	xmlns:aop="http://www.springframework.org/schema/aop" xmlns:tx="http://www.springframework.org/schema/tx"
	xsi:schemaLocation="http://www.springframework.org/schema/beans
    	 http://www.springframework.org/schema/beans/spring-beans.xsd
     	 http://www.springframework.org/schema/context
         http://www.springframework.org/schema/context/spring-context.xsd
         http://www.springframework.org/schema/aop
         http://www.springframework.org/schema/aop/spring-aop.xsd
         http://www.springframework.org/schema/tx
     	 http://www.springframework.org/schema/tx/spring-tx.xsd">

	<bean id="student" class="top.czcheng.projectdemo.biz.entity.Student"></bean>
</beans>
```

手写context类，根据beanId读取xml文件生成对应的类实例

```java
public class ClassPathXmlApplicationContext {

    private String xmlPath;

    public ClassPathXmlApplicationContext(String xmlPath) {
        this.xmlPath = xmlPath;
    }

    /**
     * 根据beanId生成对应的类实例
     * @param beanId
     * @return
     * @throws Exception
     */
    public Object getBean(String beanId) throws Exception{
        //读取配置文件
        List<Element> elements = readXml();
        // 使用beanId查找对应的class地址
        String beanClass = findXmlByIDClass(elements, beanId);
        //返回实例
        Class<?> forName = Class.forName(beanClass);
        return forName.newInstance();
    }

    /**
     * 根据beanId查询class
     * @param elements
     * @param beanId
     * @return
     */
    private String findXmlByIDClass(List<Element> elements, String beanId) {
        for (Element element : elements) {
            //获取元素中的id属性值
            String id = element.attributeValue("id");
            if (StringUtils.isNotEmpty(id) && beanId.equals(id)) {
                //获得class
                String className = element.attributeValue("class");
                if (StringUtils.isNotEmpty(className)) {
                    return className;
                }
            }
        }
        throw new RuntimeException("使用该beanId为查找到元素");
    }

    /**
     * 获得xml文件内的所有元素
     * @return
     * @throws DocumentException
     */
    public List<Element> readXml() throws DocumentException {
        SAXReader saxReader = new SAXReader();
        if (StringUtils.isEmpty(xmlPath)) {
            throw new RuntimeException("xml路径为空");
        }
        Document read = saxReader.read(getClassXmlInputStream(xmlPath));
        //获取根节点信息
        Element rootElement = read.getRootElement();
        //获取子节点
        List<Element> elements = rootElement.elements();
        if (CollectionUtils.isNotEmpty(elements)) {
            return elements;
        } else {
            throw new RuntimeException("该配置文件没有子元素");
        }
    }

    /**
     * 读取配置文件
     * @param xmlPath
     * @return
     */
    public InputStream getClassXmlInputStream(String xmlPath) {
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(xmlPath);
        return resourceAsStream;
    }
}
```

测试类：

```java
public class SpringXmlTest {

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        Student student = (Student)context.getBean("student");
        System.out.println(student);
    }
}
```

生成了一个属性都为null的student对象实例。

