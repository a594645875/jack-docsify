## 9. JAVA锁

参考文献: [JAVA锁有哪些种类](https://blog.csdn.net/nalanmingdian/article/details/77800355)

### 9.1 锁分类

这些分类并不是全是指锁的状态，有的指锁的特性，有的指锁的设计思想。

**按性质分类：**

- #### 乐观锁/悲观锁

乐观锁：每次去拿数据的时候不会上锁，在更新的时候会使用版本号等机制判断有没有别人更新了这个数据，乐观锁适用于多读的应用场景，这样可以提高吞吐量。
例子：在Java中`java.util.concurrent.atomic`包下面的原子变量类就是使用了乐观锁的一种实现方式`CAS`(`Compare and Swap` 比较并交换)实现的。

悲观锁：每次去拿数据的时候都认为别人会修改，所以每次拿数据都会上锁，别人想拿数据就会阻塞直到它拿到锁。
例子：Java里面的同步原语synchronized关键字的实现就是悲观锁。

- #### 公平锁/非公平锁  

公平锁：多个线程按照申请锁的顺序来获取锁。

非公平锁：多个线程获取锁的顺序并不是按照申请锁的顺序，有可能后申请的线程比先申请的线程优先获取锁，缺点可能会造成优先级反转或者饥饿现象。优点在于吞吐量比公平锁大。

例子：`ReetrantLock`通过构造函数指定该锁是否是公平锁，默认是非公平锁。`Synchronized`也是一种非公平锁，并且不能使其变成公平锁。

- #### 独享锁/共享锁

独享锁：该锁一次只能被一个线程所持有。例子：`ReadWriteLock`的写锁，`ReentrantLock`，`Synchronized`

共享锁：该锁可被多个线程所持有。例子：`ReadWriteLock`的读锁（作者觉得这严格来说不叫锁。）

- #### 互斥锁/读写锁

独享锁/共享锁是一种广义的说法，互斥锁/读写锁是具体的实现。

- #### 可重入锁

又名递归锁，是指在同一个线程在外层方法获取锁的时候，在进入内层方法会自动获取锁。例子：看名字就知道的`ReetrantLock`和`Synchronized`



**按设计思想分类：**

- #### 自旋锁/自适应自旋锁

自旋锁是指尝试获取锁的线程不会立即阻塞，而是采用循环的方式去尝试获取锁，这样的好处是减少线程上下文切换的消耗，缺点是循环会消耗CPU。

- #### 偏向锁/轻量级锁/重量级锁

这三种锁是指锁的状态，并且是针对`Synchronized`。是在Java 5通过引入锁升级（也叫锁膨胀，只有升级没有降级）的机制来实现高效`Synchronized`。这三种锁的状态是通过对象监视器在对象头中的字段来表明的。

**偏向锁**：一段同步代码一直被一个线程所访问，那么该线程会自动获取锁。降低获取锁的代价。

**轻量级锁**：当锁是偏向锁的时候，被另一个线程所访问，偏向锁就会升级为轻量级锁，其他线程会通过自旋的形式尝试获取锁，不会阻塞，提高性能。

**重量级锁**：当锁为轻量级锁的时候，另一个线程虽然是自旋，但自旋不会一直持续下去，当自旋一定次数的时候，还没有获取到锁，就会进入阻塞，该锁膨胀为重量级锁。重量级锁会让他申请的线程进入阻塞，性能降低。

- #### 锁粗化/锁消除

锁粗化：一系列的连续操作都对同一个对象反复加锁和解锁，甚至加锁操作是出现在循环体中的，即使没有线程竞争，频繁地进行互斥同步操作也会导致不必要的性能损耗。当虚拟机探测到有这样一串零碎的操作都对同一个对象加锁，将会把加锁同步的范围扩展（粗化）到整个操作序列的外部。

锁消除：虚拟机即时编译器在运行时，对一些代码上要求同步，但是被检测到不可能存在共享数据竞争的锁进行消除。

- #### 分段锁

分段锁的设计目的是细化锁的粒度，当操作不需要更新整个数组的时候，就仅仅针对数组中的一项进行加锁操作。例子：`ConcurrentHashMap`中的分段锁称为`Segment`，它即类似于`HashMap`（JDK7与JDK8中`HashMap`的实现）的结构，即内部拥有一个Entry数组，数组中的每个元素又是一个链表；同时又是一个`ReentrantLock`（Segment继承了`ReentrantLock`)。当需要put元素的时候，并不是对整个`hashmap`进行加锁，而是先通过`hashcode`来知道他要放在那一个分段中，然后对这个分段进行加锁，所以当多线程put的时候，只要不是放在一个分段中，就实现了真正的并行的插入

### 9.2 常用锁

#### 9.2.1 Synchronized

synchronized 它可以把任意一个非 NULL 的对象当作锁。 他属于独占式的悲观锁，同时属于可重
入锁。

**Synchronized 作用范围**  

1. 作用于方法时，锁住的是对象的实例(this)；

2. 当作用于静态方法时，锁住的是Class实例，又因为Class的相关数据存储在永久带PermGen（jdk1.8 则是 metaspace），永久带是全局共享的，因此静态方法锁相当于类的一个全局锁，会锁所有调用该方法的线程；

3. synchronized 作用于一个对象实例时，锁住的是所有以该对象为锁的代码块。 它有多个队列，当多个线程一起访问某个对象监视器的时候，对象监视器会将这些线程存储在不同的容器中。

**实战**

synchronized可重入锁验证

```java
public class MyLockTest implements Runnable {
    public synchronized void get() {
        System.out.println(Thread.currentThread().getName() + " get方法体开始");
        set();
        System.out.println(Thread.currentThread().getName() + " get方法体结束");
    }
    public synchronized void set() {
        System.out.println(Thread.currentThread().getName() + " set方法体 ");
    }
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " 开启线程，即将执行get方法");
        get();
    }
    public static void main(String[] args) {
        MyLockTest test = new MyLockTest();
        for (int i = 0; i < 10; i++) {
            new Thread(test, "thread-" + i).start();
        }
    }
}
```

运行结果

```shell
thread-0 开启线程，即将执行get方法
thread-1 开启线程，即将执行get方法
thread-0 get方法体开始
thread-0 set方法体 
thread-0 get方法体结束
thread-3 开启线程，即将执行get方法
thread-2 开启线程，即将执行get方法
thread-1 get方法体开始
thread-1 set方法体 
thread-1 get方法体结束
thread-2 get方法体开始
thread-2 set方法体 
thread-2 get方法体结束
thread-3 get方法体开始
thread-3 set方法体 
thread-3 get方法体结束
thread-5 开启线程，即将执行get方法
thread-5 get方法体开始
thread-5 set方法体 
thread-5 get方法体结束
thread-6 开启线程，即将执行get方法
thread-6 get方法体开始
thread-6 set方法体 
thread-6 get方法体结束
thread-7 开启线程，即将执行get方法
thread-7 get方法体开始
thread-7 set方法体 
thread-7 get方法体结束
thread-9 开启线程，即将执行get方法
thread-9 get方法体开始
thread-9 set方法体 
thread-9 get方法体结束
thread-4 开启线程，即将执行get方法
thread-4 get方法体开始
thread-4 set方法体 
thread-4 get方法体结束
thread-8 开启线程，即将执行get方法
thread-8 get方法体开始
thread-8 set方法体 
thread-8 get方法体结束
```

- get()方法中顺利进入了set()方法，说明synchronized的确是可重入锁。

- 分析打印Log，thread-1、thread-0同时启动，thread-0先进入get方法体，这个时候thread-1等待进入
- 没有按照thread-7、thread-8、thread-9的顺序进入get方法体，说明sychronized的确是非公平锁。
- 在一个线程进入get方法体后，其他线程只能等待，无法同时进入，验证了synchronized是独占锁。

#### 9.2.2 ReentrantLock

ReentrantLock既可以构造公平锁又可以构造非公平锁，默认为非公平锁，将上面的代码改为用ReentrantLock实现，再次运行。

```java
public class ReentrantTest implements Runnable{
    private ReentrantLock reentrantLock = new ReentrantLock();
    public void get() {
        reentrantLock.lock();
        System.out.println(Thread.currentThread().getName() + " get方法体开始");
        set();
        System.out.println(Thread.currentThread().getName() + " get方法体结束");
        reentrantLock.unlock();//实际使用中，应该放在finally代码块里
    }
    public void set() {
        reentrantLock.lock();
        System.out.println(Thread.currentThread().getName() + " set方法体");
        reentrantLock.unlock();//实际使用中，应该放在finally代码块里
    }
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " 线程启动");
        get();
    }
    public static void main(String[] args) {
        ReentrantTest test = new ReentrantTest();
        for (int i = 0; i < 10; i++) {
            new Thread(test, "thread->" + i).start();
        }
    }
}
```

运行结果

```
thread->0 线程启动
thread->2 线程启动
thread->1 线程启动
thread->0 get方法体开始
thread->0 set方法体
thread->0 get方法体结束
thread->2 get方法体开始
thread->2 set方法体
thread->2 get方法体结束
thread->1 get方法体开始
thread->1 set方法体
thread->1 get方法体结束
thread->3 线程启动
thread->3 get方法体开始
thread->3 set方法体
thread->4 线程启动
thread->3 get方法体结束
thread->4 get方法体开始
thread->4 set方法体
thread->4 get方法体结束
thread->5 线程启动
thread->5 get方法体开始
thread->5 set方法体
thread->5 get方法体结束
thread->8 线程启动
thread->8 get方法体开始
thread->8 set方法体
thread->8 get方法体结束
thread->9 线程启动
thread->9 get方法体开始
thread->9 set方法体
thread->9 get方法体结束
thread->6 线程启动
thread->6 get方法体开始
thread->6 set方法体
thread->6 get方法体结束
thread->7 线程启动
thread->7 get方法体开始
thread->7 set方法体
thread->7 get方法体结束
```

可重入锁，非公平锁，实锤了！

改成公平锁只需要在构造的时候传入true值

```java
ReentrantLock reentrantLock = new ReentrantLock(true);
```

#### 9.2.3 ReadWriteLock

Java并发包中ReadWriteLock是一个接口，主要有两个方法readLock()，writeLock()；

ReetrantReadWriteLock实现了ReadWriteLock接口并添加了可重入的特性。

```java
public class ReadAndWriteLockTest {

    public static void get(Thread thread) {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        lock.readLock().lock();
        System.out.println("start time:" + System.currentTimeMillis());
        for (int i = 0; i < 5; i++) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(thread.getName() + ":正在进行读操作……");
        }
        System.out.println(thread.getName() + ":读操作完毕！");
        System.out.println("end time:" + System.currentTimeMillis());
        lock.readLock().unlock();
    }
    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                get(Thread.currentThread());
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                get(Thread.currentThread());
            }
        }).start();
    }
}
```

读写锁有两种加锁方法，读锁的时候可以有多个线程共同持有。

既然可以多条线程共同持有，那么读锁和不加锁有什么区别？

模拟100人抢5件商品，作死不加锁：

```java
public class ReadLockTest implements Runnable {
    private int count = 5;
    public static void main(String[] args) {
        ReadLockTest test = new ReadLockTest();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            new Thread(test, "Thread->" + i).start();
        }
        System.out.println("耗时："+ (System.currentTimeMillis() - start )+ "ms");
    }
    public void countDown() {
        if (count > 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName()+"抢到了"+count+"号商品");
            count--;
        }
        if (count < 0) {
            System.out.println(Thread.currentThread().getName() + "超卖了！" + count);
        }
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        countDown();
        System.out.println("耗时："+ (System.currentTimeMillis() - start )+ "ms");
    }
}
```

结果。。。超卖了93件特价商品，赶紧删库跑路！

```java
...
超卖了！-93
```

不加锁的后果很严重，那么加读锁呢？

```java
public class ReadLockTest implements Runnable {
    private int count = 5;
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    
    public static void main(String[] args) {
        ReadLockTest test = new ReadLockTest();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            new Thread(test, "Thread->" + i).start();
        }
        System.out.println("耗时："+ (System.currentTimeMillis() - start )+ "ms");
    }
    public void countDown() {
        lock.readLock().lock();
        if (count > 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName()+"抢到了"+count+"号商品");
            count--;
        }
        lock.readLock().unlock();
        if (count < 0) {
            System.out.println(Thread.currentThread().getName() + "超卖了！" + count);
        }
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        countDown();
        System.out.println("耗时："+ (System.currentTimeMillis() - start )+ "ms");
    }
}
```

结果，呵呵。。。超卖95！这和不加锁没区别好吧！赶紧删库。

```
...
超卖了！-95
```

老老实实加写锁吧

```java
public class ReadLockTest implements Runnable {
    private int count = 5;
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public static void main(String[] args) {
        ReadLockTest test = new ReadLockTest();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            new Thread(test, "Thread->" + i).start();
        }
        System.out.println("耗时："+ (System.currentTimeMillis() - start )+ "ms");
    }
    public void countDown() {
        lock.writeLock().lock();
        if (count > 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName()+"抢到了"+count+"号商品");
            count--;
        }
        lock.writeLock().unlock();
        if (count < 0) {
            System.out.println(Thread.currentThread().getName() + "超卖了！" + count);
        }
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        countDown();
        System.out.println("耗时："+ (System.currentTimeMillis() - start )+ "ms");
    }
}
```

这次终于不用删库跑路了。

```
耗时：12ms
Thread->0抢到了5号商品
耗时：1005ms
Thread->1抢到了4号商品
耗时：2013ms
Thread->2抢到了3号商品
耗时：3013ms
Thread->3抢到了2号商品
耗时：4013ms
Thread->4抢到了1号商品
耗时：5014ms
耗时：4994ms //其他的线程都是5秒左右结束
```

