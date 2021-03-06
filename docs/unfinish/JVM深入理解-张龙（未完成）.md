### Java虚拟机与生命周期
- 在如下几种情况下,Java虚拟机将结束生命周期
    - 执行了System.exit()方法
    - 程序正常执行结束
    - 程序在执行过程中遇到异常或错误而异常终止
    - 由于操作系统出现错误而导致Java虚拟机进程终止
### 类加载
- 类的加载指的是将类的.class文件中的二进制数据读入到内存中,将其放在运行时数据区的方法区内,然后在内存中创建一个java.lang.Class对象(规范并未说明Class对象位于哪里,HotSpot虚拟机将其放在了方法区了)用来封装类的方法区内的数据结构
-  在Java代码中，类型的加载、连接与初始化过程都是在程序运行期间完成的
    - 加载: 查找并加载类的二进制数据,加载.class文件的方式
        - 从本地系统中直接加载
        - 通过网络下载.class文件
        - 从zip,jar等归档文件中加载.class文件(常用导包)
        - 从专有数据库中提取.class文件
        - `将Java源文件动态编译为.class文件`(jsp,动态代理)
    - 连接
        - 验证: 确保被加载的类的正确性
        - 准备: 为类的`静态变量`分配内存,并将其初始化为`默认值`
        - 解析: `把类中的符号引用转换为直接引用`
    - `初始化: 为类的静态变量赋予正确的初始值`
- 使用
     - 所有的Java虚拟机实现必须在每个类或接口被Java程序`首次主动使用`时才初始化他们
    - 主动使用(七种)
        - 创建类的实例
        - 访问某个类或接口的静态变量,或者对该静态变量赋值
        - 调用类的静态方法
        - 反射(如Class.forName("com.test.Test"))
        - 初始化一个类的子类
        - Java虚拟机启动时被标明为启动类的类(Java Test)
        - JDK7开始提供动态语言支持: java.lang.invoke.MethodHandle实例的解析结果REF_getStatic,REF_putStatic,REF_invokeStatic句柄对应的类没有初始化,则初始化
    - 被动使用

- 卸载
- 提供了更大的灵活性，增加了更多的可能性

