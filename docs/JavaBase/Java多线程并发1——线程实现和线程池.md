## 1. JAVA并发包架构

![](C:\Users\chenzecheng\Desktop\document\study\jack-docsify\docs\image\线程基础并发包.png)

## 2. 线程实现/创建方式

### 2.1 集成Thread类

略

### 2.2 实现Runnable接口

略

### 2.3 有返回值线程ExcutorService、Callable\<Class\>、Future

```java
public class FutureTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //创建一个线程池（此处阿里规约会提示建议手动创建线程池，先不管后面再深入研究）
        ExecutorService pool = Executors.newFixedThreadPool(50);
        List<Future> list = new ArrayList<>();
        long start = System.currentTimeMillis();//开始计时0
        for (int i = 1; i < 101; i++) {
            Callable c = new Mycallable(i + "");
            //执行任务并获取Future对象
            Future f = pool.submit(c);
            list.add(f);
        }
        //关闭线程
        pool.shutdown();
        System.out.println("运行时长："+(System.currentTimeMillis() - start));//86毫秒
        //获得所有并发任务的运行结果
        for (Future future : list) {
            //打印时需要等待线程处理结束
            System.out.println(future.get().toString());
        }
        System.out.println("打印时长："+(System.currentTimeMillis() - start)); //2110毫秒
    }
}

class Mycallable implements Callable {
    private String str;
    public Mycallable(String str) {
        this.str = str;
    }
    @Override
    public Object call() throws Exception {
        //模拟处理了1秒的事务
        Thread.sleep(1000);
        return this.str;
    }
}
```

### 2.4 使用线程池

线程和数据库连接是非常珍贵的资源，频繁创建销毁很耗费性能。
使用线程池可以尽可能减少线程的创建和销毁。

```java
public class PoolTest {
    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        while (true) {
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName() + " is running...");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
```

## 3. 4种线程池

Executor是线程池的顶级接口，但严格来说只是执行线程的工具，不是线程池。
ExecutorService是真正的线程池接口。

![](C:\Users\chenzecheng\Desktop\document\study\jack-docsify\docs\image\线程池UML.png)

### 3.1newCachedThreadPool 

调用 execute 将重用以前构造的线程（如果线程可用）。如果现有线程没有可用的，则创建一个新线程并添加到池中。终止并从缓存中移除那些已有 60 秒钟未被使用的线程。 

### 3.2 newFixedThreadPool  

创建一个可重用固定线程数的线程池，以共享的无界队列方式来运行这些线程。如果在关闭前的执行期间由于失败而导致任何线程终止，那么一个新线程将代替它执行后续的任务（如果需要）。在某个线程被显式地关闭之前，池中的线程将一直存在。  

### 3.3 newScheduledThreadPool  

创建一个线程池，它可安排在给定延迟后运行命令或者定期地执行。

```java
public class SchedulePoolTest {
    public static void main(String[] args) {
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(3);
        scheduledThreadPool.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("延迟3秒");
            }
        }, 3, TimeUnit.SECONDS);
        scheduledThreadPool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.out.println("延迟1秒，每3秒执行一次");
            }
        },1,2,TimeUnit.SECONDS);
    }
}
```

### 3.4 newSingleThreadExecutor  

这个线程池只有一个线程 ,这个线程池可以在线程死后（或发生异常时）重新启动一个线程来替代原来的线程继续执行下去  

## 4. 线程的生命周期

![](C:\Users\chenzecheng\Desktop\document\study\jack-docsify\docs\image\线程的生命周期.png)

## 5. 终止线程的4种方法

- 程序运行结束
- 使用退出标志，打断循环（也就是使程序运行结束）
- 使用 interrupt()方法  
  - 处于阻塞状态时，会抛出InterruptedException ，然后在catch中用break结束线程
  - 未处于阻塞状态，使用isInterrupted()方法设置中断循环的标注，使用interrupt()就能退出循环终止线程
- 使用thread.stop()，线程不安全。强制释放锁，可能会导致数据被破坏线程安全
- 

## 6. sleep和wait区别

|       | 属于对象 |  锁  |       恢复       |
| :---: | :------: | :--: | :--------------: |
| sleep |  Thread  | 保持 |    设定时间后    |
| wait  |  Object  | 释放 | 调用notify()方法 |

## 7. start和run区别

start开启一条线程，run在当前线程执行代码。

## 8. 守护线程

为用户线程提供公共服务，在没有用户线程可服务时会自动离开。  与系统“同生共死”。

- setDaemon(true)  可将用户线程设置成守护线程

- 在 Daemon 线程中产生的新线程也是 Daemon 的。  

- 垃圾回收线程就是一个经典的守护线程  