## XML版本

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

## 注解版本

注解@ExtService：

```java
@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExtService {
}
```

注解@ExtAutoware:

```java
@Target(value = {ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExtAutoware {
}
```

控制器层：

```java
@ExtService
public class StudentController {

    @ExtAutoware
    private IStudentService iStudentService;

	//为了测试能看到效果而加的打印方法
    @Override
    public String toString() {
        return "StudentController{" +
                "iStudentService=" + iStudentService +
                '}';
    }
}
```

接口层：

```java
public interface IStudentService extends IService<Student> {
}
```

接口实现层：

```
@ExtService
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements IStudentService {
	//内容不重要，略。。。
}
```

IOC核心类：

```java
public class AnnotationApplicationContext {

    private String packageName;

    //实例容器
    private ConcurrentHashMap<String, Object> beanCollect = null;

    public AnnotationApplicationContext(String packageName) {
        this.packageName = packageName;
    }

    /**
     * 主要方法：根据beanId获取对象
     * @param beanId
     * @return
     */
    public Object getBean(String beanId) throws InstantiationException, IllegalAccessException {
        //使用反射机制获取该包下所有的类，已经存在bean的注解类
        List<Class> classList = findClassExiService();
        //使用反射机制初始化对象
        beanCollect = initBean(classList);
        //根据beanId获取bean
        Object object = beanCollect.get(beanId);
        //给类中有注解的属性赋值
        attriAssign(object);
        return object;
    }

    private void attriAssign(Object object) throws IllegalAccessException {
        //查询类的属性是否有特定注解
        Class<?> classInfo = object.getClass();
        Field[] fields = classInfo.getDeclaredFields();
        for (Field field : fields) {
            ExtAutoware annotation = field.getDeclaredAnnotation(ExtAutoware.class);
            if (null != annotation) {
                String fieldClassName = toLowerCaseFirstOne(field.getType().getSimpleName());
                Object bean = beanCollect.get(fieldClassName);
                if (null != bean) {
                    //设置允许访问
                    field.setAccessible(true);
                    //给属性赋值
                    field.set(object, bean);
                }
            }
        }
    }

    private ConcurrentHashMap<String, Object> initBean(List<Class> classList) throws IllegalAccessException, InstantiationException {
        ConcurrentHashMap<String, Object> beaanCollect = new ConcurrentHashMap<>(16);
        for (Class classInfo : classList) {
            Annotation declaredAnnotation = classInfo.getDeclaredAnnotation(ExtService.class);
            if (null != declaredAnnotation) {
                Object newInstance = classInfo.newInstance();
                //如果实现了接口，就取接口名字
                String beanId;
                if (classInfo.getInterfaces().length > 0) {
                    beanId = toLowerCaseFirstOne(classInfo.getInterfaces()[0].getSimpleName());
                } else {
                    beanId = toLowerCaseFirstOne(classInfo.getSimpleName());
                }
                beaanCollect.put(beanId, newInstance);
            }
        }
        if (CollectionUtils.isEmpty(beaanCollect)) {
            throw new RuntimeException("初始化bean为空");
        }
        return beaanCollect;
    }

    private static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0))) {
            return s;
        } else {
            return String.valueOf(Character.toLowerCase(s.charAt(0))) + s.substring(1);
        }
    }

    /**
     * //使用反射机制获取该包下所有的类，已经存在bean的注解类
     *
     * @return
     */
    private List<Class> findClassExiService() {
        //判断packageName
        if (StringUtils.isEmpty(packageName)) {
            throw new RuntimeException("扫包地址不能为空！");
        }
        //使用反射技术获取当前包下所有的类
        List<Class<?>> classesByPackageName = CusClassUtil.getClasses(packageName);
        //将带指定注解的类放入返回集合中
        List<Class> classList = new ArrayList<>();
        for (Class classInfo : classesByPackageName) {
            Annotation annotation = classInfo.getDeclaredAnnotation(ExtService.class);
            if (null != annotation) {
                classList.add(classInfo);
            }
        }
        if (CollectionUtils.isNotEmpty(classList)) {
            return classList;
        } else {
            throw new RuntimeException("没有需要初始化的bean");
        }
    }
}
```

需要用到的工具类：

```java
public class CusClassUtil {
    /**
     * 取得某个接口下所有实现这个接口的类
     */
    public static List<Class> getAllClassByInterface(Class c) {
        List<Class> returnClassList = null;

        if (c.isInterface()) {
            // 获取当前的包名
            String packageName = c.getPackage().getName();
            // 获取当前包下以及子包下所以的类
            List<Class<?>> allClass = getClasses(packageName);
            if (allClass != null) {
                returnClassList = new ArrayList<Class>();
                for (Class classes : allClass) {
                    // 判断是否是同一个接口
                    if (c.isAssignableFrom(classes)) {
                        // 本身不加入进去
                        if (!c.equals(classes)) {
                            returnClassList.add(classes);
                        }
                    }
                }
            }
        }

        return returnClassList;
    }

    /*
     * 取得某一类所在包的所有类名 不含迭代
     */
    public static String[] getPackageAllClassName(String classLocation, String packageName) {
        // 将packageName分解
        String[] packagePathSplit = packageName.split("[.]");
        String realClassLocation = classLocation;
        int packageLength = packagePathSplit.length;
        for (int i = 0; i < packageLength; i++) {
            realClassLocation = realClassLocation + File.separator + packagePathSplit[i];
        }
        File packeageDir = new File(realClassLocation);
        if (packeageDir.isDirectory()) {
            String[] allClassName = packeageDir.list();
            return allClassName;
        }
        return null;
    }

    /**
     * 从包package中获取所有的Class
     *
     * @param packageName
     * @return
     */
    public static List<Class<?>> getClasses(String packageName) {

        // 第一个class类的集合
        List<Class<?>> classes = new ArrayList<Class<?>>();
        // 是否循环迭代
        boolean recursive = true;
        // 获取包的名字 并进行替换
        String packageDirName = packageName.replace('.', '/');
        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            // 循环迭代下去
            while (dirs.hasMoreElements()) {
                // 获取下一个元素
                URL url = dirs.nextElement();
                // 得到协议的名称
                String protocol = url.getProtocol();
                // 如果是以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    // 获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    // 以文件的方式扫描整个包下的文件 并添加到集合中
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                } else if ("jar".equals(protocol)) {
                    // 如果是jar包文件
                    // 定义一个JarFile
                    JarFile jar;
                    try {
                        // 获取jar
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        // 从此jar包 得到一个枚举类
                        Enumeration<JarEntry> entries = jar.entries();
                        // 同样的进行循环迭代
                        while (entries.hasMoreElements()) {
                            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            // 如果是以/开头的
                            if (name.charAt(0) == '/') {
                                // 获取后面的字符串
                                name = name.substring(1);
                            }
                            // 如果前半部分和定义的包名相同
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                // 如果以"/"结尾 是一个包
                                if (idx != -1) {
                                    // 获取包名 把"/"替换成"."
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                // 如果可以迭代下去 并且是一个包
                                if ((idx != -1) || recursive) {
                                    // 如果是一个.class文件 而且不是目录
                                    if (name.endsWith(".class") && !entry.isDirectory()) {
                                        // 去掉后面的".class" 获取真正的类名
                                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                                        try {
                                            // 添加到classes
                                            classes.add(Class.forName(packageName + '.' + className));
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes;
    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName
     * @param packagePath
     * @param recursive
     * @param classes
     */
    public static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive,
                                                        List<Class<?>> classes) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(new FileFilter() {
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            @Override
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        // 循环所有文件
        for (File file : dirfiles) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive,
                        classes);
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    // 添加到集合中去
                    classes.add(Class.forName(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
```

测试类：

```java
public class AnnotationTest {
    public static void main(String[] args) throws IllegalAccessException, InstantiationException {
        ///top.czcheng.projectdemo.biz为加了注解的类在的包
        AnnotationApplicationContext context = new AnnotationApplicationContext("top.czcheng.projectdemo.biz");
        StudentController bean = (StudentController) context.getBean("studentController");
        System.out.println(bean);
    }
}
```

结果：

```
StudentController{iStudentService=top.czcheng.projectdemo.biz.service.impl.StudentServiceImpl@2344fc66}
```

StudentController类和iStudentService类都成功注入了！