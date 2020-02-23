# JVM——内存结构

![](https://note.youdao.com/yws/public/resource/645f12f2d7721a2961eec0f980f92af9/xmlnote/32160D87D28A415181750A10E70BE70F/22)

- 程序计数器
- 虚拟机栈
- 本地方法栈
- 堆
- 方法区
## 1. 程序计数器
### 1.1 定义

Program Counter Register 程序计数器（寄存器）

作用:记住下一条jvm的指令地址

特点
- 线程私有
- 不会产生内存溢出

## 2. 虚拟机栈

### 2.1 定义

Java Virtual Machine Stacks Java虚拟机栈

- 每个线程只能有一个活动栈帧，对应着当前正在执行的那个方法

问题辨析

1. 垃圾回收是否涉及栈内存？

   不会，因为栈帧运行结束后会自动弹栈被回收。

2. 栈内存分配越大越好吗？

   1. -Xss1m/1024k/1048576 调整栈内存大小 
   2. linux，macOS，Oracle默认栈内存1024k，Windows根据虚拟内存决定
   3. 栈内存越大，虚拟机的最高线程数越小，所以栈内存不是越大越好

3. 方法内的局部变量是否线程安全？

   1. 如果方法内局部变量没有逃离方法的作用访问，它是线程安全的
   2. 如果是局部变量引用了对象，并逃离方法的作用范围，需要考虑线程安全

### 2.2 栈内存溢出

- 栈帧过多导致内存溢出

  ```java
  /**
   * 演示栈内存溢出 java.lang.StackOverflowError
   * -Xss256k
   */
  public class Demo1_2 {
      private static int count;
  
      public static void main(String[] args) {
          try {
              method1();
          } catch (Throwable e) {
              e.printStackTrace();
              System.out.println(count);
          }
      }
  
      private static void method1() {
          count++;
          method1();
      }
  }	
  ```

- 栈帧过大导致内存溢出

### 2.3 线程运行诊断

- 用top定位哪个进程对cpu的占用过高
- ps H -eo pid,tid,%cpu | grep 进程id（用ps命令进一步定位是哪个**线程**引起的cpu占用过高）
- jstack 进程id
  - 可以根据**线程**id（jstack输出的是16进制，需要转换进制）找到有问题的线程，进一步定位到问题代码的源码行号
  - 如果有死锁信息也会显示出来

## 3. 本地方法栈

### 3.1 定义

Native Method Stacks 本地方法栈

- 本地方法：不是由Java代码编写，用C或者C++语言编写的，用来与操作系统底层交互的方法。通常用`native`修饰。

- 本地方法栈：本地方法运行的空间

## 4. 堆

### 4.1 定义

Heap 堆

- 通过new关键字，创建对象都会使用堆内存

特点

- 它是线程共享的，堆中对象都需要考虑线程安全的问题
- 有垃圾回收机制

### 4.2 堆内存溢出

- 因为有垃圾回收机制，所以不断产生**有人使用**的对象才会产生堆内存溢出

  ```java
  /**
   * 演示堆内存溢出 java.lang.OutOfMemoryError: Java heap space
   * -Xmx8m
   */
  public class Demo1_5 {
  
      public static void main(String[] args) {
          int i = 0;
          try {
              List<String> list = new ArrayList<>();
              String a = "hello";
              while (true) {
                  list.add(a); // hello, hellohello, hellohellohellohello ...
                  a = a + a;  // hellohellohellohello
                  i++;
              }
          } catch (Throwable e) {
              e.printStackTrace();
              System.out.println(i);
          }
      }
  }
  ```

- 如果堆内存设置较大的时候，不容易发现产生堆内存溢出的代码，排查时可以设置较小的堆内存（-Xmx8m），以便尽早发现导致堆内存溢出的代码

### 4.3 堆内存诊断

1. jps 工具

   - 查看当前系统有哪些java进程

   ```shell
   # jps
   18196 Demo1_4
   1272
   13848 Jps
   444 Launcher
   ```

2. jmap 工具

   - 查看堆内存的瞬时占用情况

   ```shell
   # jmap -heap 18196
   Attaching to process ID 18196, please wait...
   Debugger attached successfully.
   Server compiler detected.
   JVM version is 25.191-b12
   
   using thread-local object allocation.
   Parallel GC with 4 thread(s)
   
   Heap Configuration:
      MinHeapFreeRatio         = 0
      MaxHeapFreeRatio         = 100
      MaxHeapSize              = 2120220672 (2022.0MB)
      NewSize                  = 44564480 (42.5MB)
      MaxNewSize               = 706740224 (674.0MB)
      OldSize                  = 89653248 (85.5MB)
      NewRatio                 = 2
      SurvivorRatio            = 8
      MetaspaceSize            = 21807104 (20.796875MB)
      CompressedClassSpaceSize = 1073741824 (1024.0MB)
      MaxMetaspaceSize         = 17592186044415 MB
      G1HeapRegionSize         = 0 (0.0MB)
   
   Heap Usage:
   PS Young Generation
   Eden Space:
      capacity = 34078720 (32.5MB)
      used     = 3415568 (3.2573394775390625MB)
      free     = 30663152 (29.242660522460938MB)
      10.0225830078125% used
   From Space:
      capacity = 5242880 (5.0MB)
      used     = 0 (0.0MB)
      free     = 5242880 (5.0MB)
      0.0% used
   To Space:
      capacity = 5242880 (5.0MB)
      used     = 0 (0.0MB)
      free     = 5242880 (5.0MB)
      0.0% used
   PS Old Generation
      capacity = 89653248 (85.5MB)
      used     = 0 (0.0MB)
      free     = 89653248 (85.5MB)
      0.0% used
   
   1725 interned Strings occupying 155488 bytes.
   ```

3. jconsole 工具

   - 图形界面的，多功能的检测工具，可以连续检测

   ```shell
   # jconsole
   （弹出图形化界面）
   ```

4. jvisualvm 工具
   - 可以dump出内存快照之后，进行内存分析，筛选出最大的20个对象

## 5. 方法区

### 5.1 定义

- 所有Java线程共享的区，存储跟类的结构相关的信息（**运行时常量池**、成员变量、方法数据、成员方法以及构造器方法的代码部分，包括特殊方法即构造方法）
- 特性：
  - 虚拟机启动时创建，逻辑上是堆的组成部分（实现上不一定是，不同厂商可能不同）
  - 方法区是JVM的规范，永久代、元空间等是JVM的实现方式而已。

### 5.2 组成

![](https://note.youdao.com/yws/public/resource/645f12f2d7721a2961eec0f980f92af9/xmlnote/AF77E4AD7B1A410E9020B250DC718037/21)

- JDK1.6及之前，方法区存放在永久代（PermGen）中，包括运行时常量池（含StringTable）、Class、ClassLoader

- JDK1.8之后，方法区（含CLass、ClassLoader、运行时常量池）存放在元空间Metaspace（用的是系统内存，不用jvm内存），StringTable存放在堆Heap中。

### 5.3 方法区内存溢出

- 1.8 以前会导致永久代内存溢出

  ```java
  /**
   * 演示永久代内存溢出  java.lang.OutOfMemoryError: PermGen space
   * -XX:MaxPermSize=8m
   */
  public class Demo1_8 extends ClassLoader {
      public static void main(String[] args) {
          int j = 0;
          try {
              Demo1_8 test = new Demo1_8();
              for (int i = 0; i < 20000; i++, j++) {
                  ClassWriter cw = new ClassWriter(0);
                  cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC, "Class" + i, null, "java/lang/Object", null);
                  byte[] code = cw.toByteArray();
                  test.defineClass("Class" + i, code, 0, code.length);
              }
          } finally {
              System.out.println(j);
          }
      }
  }
  ```

- 1.8 之后会导致元空间内存溢出

  ```java
  /**
   * 演示元空间内存溢出 java.lang.OutOfMemoryError: Metaspace
   * -XX:MaxMetaspaceSize=8m
   */
  public class Demo1_8 extends ClassLoader { // 可以用来加载类的二进制字节码
      public static void main(String[] args) {
          int j = 0;
          try {
              Demo1_8 test = new Demo1_8();
              for (int i = 0; i < 10000; i++, j++) {
                  // ClassWriter 作用是生成类的二进制字节码
                  ClassWriter cw = new ClassWriter(0);
                  // 版本号， public， 类名, 包名, 父类， 接口
                  cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, "Class" + i, null, "java/lang/Object", null);
                  // 返回 byte[]
                  byte[] code = cw.toByteArray();
                  // 执行了类的加载
                  test.defineClass("Class" + i, code, 0, code.length); // Class 对象
              }
          } finally {
              System.out.println(j);
          }
      }
  }
  ```

### 5.4 运行时常量池

- 常量池，就是一张表，虚拟机指令根据这张常量表找到要执行的类名、方法名、参数类型、字面量

  等信息

- 运行时常量池，常量池是 *.class 文件中的，当该类被加载，它的常量池信息就会放入运行时常量

  池，并把里面的符号地址变为真实地址

### 5.5 StringTable

#### 5.5.1 StringTable 面试题：

```java
String s1 = "a"; 
String s2 = "b"; 
String s3 = "a" + "b"; 
String s4 = s1 + s2; 
String s5 = "ab"; 
String s6 = s4.intern(); 

// 问 
System.out.println(s3 == s4); 
System.out.println(s3 == s5); 
System.out.println(s3 == s6); 

String x2 = new String("c") + new String("d"); 
String x1 = "cd"; 
x2.intern(); 

// 问，如果调换了【最后两行代码】的位置呢，如果是jdk1.6呢 
System.out.println(x1 == x2);
```

#### 5.5.2 StringTable 特性

- 常量池中的字符串仅是符号，第一次用到时才变为对象

- 利用串池的机制，来避免重复创建字符串对象

- 字符串变量拼接的原理是 StringBuilder （1.8）

- 字符串常量拼接的原理是编译期优化

- 可以使用 intern 方法，主动将串池中还没有的字符串对象放入串池

  - 1.8 将这个字符串对象尝试放入串池，如果有则并不会放入，如果没有则放入串池， 会把串

    池中的对象返回

  - 1.6 将这个字符串对象尝试放入串池，如果有则并不会放入，如果没有会把此对象复制一份，

    放入串池， 会把串池中的对象返回

#### 5.5.3 StringTable 位置

- 1.6，在永久代的方法区的常量池中
- 1.8，在堆中，提高StringTable的垃圾回收效率，优化内存使用

#### 5.5.4 StringTable 垃圾回收

```java
/**
 * Java 1.8
 * 演示 StringTable 垃圾回收
 * -Xmx10m -XX:+PrintStringTableStatistics -XX:+PrintGCDetails -verbose:gc
 */
public class Demo1_7 {
    public static void main(String[] args) throws InterruptedException {
        int i = 0;
        try {
            for (int j = 0; j < 100000; j++) { // j=100, j=10000
                String.valueOf(j).intern();
                i++;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            System.out.println(i);
        }

    }
}
```

```java
/**
 * Java 1.6
 * 演示 StringTable 垃圾回收
 * -XX:MaxPermSize=10m -XX:+PrintStringTableStatistics -XX:+PrintGCDetails -verbose:gc
 */
public class Demo1_7 {


    // 共向StringTable添加500000字符串，但是10m只能存进1754
    public static void main(String[] args) throws InterruptedException {
        int i = 0;
        try {
            for (int j = 0; j < 500000; j++) { // j=10, j=1000000
                String.valueOf(j).intern();
                i++;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            System.out.println(i);
        }

    }
}
```

#### 5.5.5 StringTable 性能调优

- 增大桶个数，调整 -XX:StringTableSize=桶个数

  ```java
  /**
   * 演示串池大小对性能的影响
   * -Xms500m -Xmx500m -XX:+PrintStringTableStatistics -XX:StringTableSize=1009
   * linux.words 是一个有48万个单词的文本
   */
  public class Demo1_24 {
  
      public static void main(String[] args) throws IOException {
          try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("linux.words"), "utf-8"))) {
              String line = null;
              long start = System.nanoTime();
              while (true) {
                  line = reader.readLine();
                  if (line == null) {
                      break;
                  }
                  line.intern();
              }
              System.out.println("cost:" + (System.nanoTime() - start) / 1000000);
          }
      }
  }
  ```

- 有大量字符串的系统（地址，姓名等），可以考虑将字符串放进StringTable

  ```java
  /**
   * 演示 intern 减少内存占用
   * -XX:StringTableSize=200000 -XX:+PrintStringTableStatistics
   * -Xsx500m -Xmx500m -XX:+PrintStringTableStatistics -XX:StringTableSize=200000
   * linux.words 是一个有48万个单词的文本
   */
  public class Demo1_25 {
  
      public static void main(String[] args) throws IOException {
  
          List<String> address = new ArrayList<>();
          System.in.read();
          for (int i = 0; i < 10; i++) {
              try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("linux.words"), "utf-8"))) {
                  String line = null;
                  long start = System.nanoTime();
                  while (true) {
                      line = reader.readLine();
                      if(line == null) {
                          break;
                      }
                      address.add(line.intern());
                  }
                  System.out.println("cost:" +(System.nanoTime()-start)/1000000);
              }
          }
          System.in.read();
      }
  }
  ```

## 6. 直接内存

### 6.1 定义

Direct Memory 直接内存

- 常见于 NIO 操作时，用于数据缓冲区

- 分配回收成本较高，但读写性能高

- 不受 JVM 内存回收管理

### 6.2 分配和回收原理

- 使用了 Unsafe 对象完成直接内存的分配回收，并且回收需要主动调用 freeMemory 方法
- ByteBuffer 的实现类内部，使用了 Cleaner （虚引用）来监测 ByteBuffer 对象，一旦ByteBuffer 对象被垃圾回收，那么就会由 ReferenceHandler 线程通过 Cleaner 的 clean 方法调用 freeMemory 来释放直接内存

