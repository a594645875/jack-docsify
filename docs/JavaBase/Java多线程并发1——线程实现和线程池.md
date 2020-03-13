## JAVA并发包架构

![](..\image\线程基础并发包.png)

## 线程实现/创建方式

### 集成Thread类

略

### 实现Runnable接口

略

### 有返回值线程ExcutorService、Callable\<Class\>、Future

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

### 使用线程池

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


## 并发队列

在并发队列上JDK提供了两套实现，一个是以ConcurrentLinkedQueue为代表的高性能队列非阻塞，一个是以BlockingQueue接口为代表的阻塞队列，无论哪种都继承自Queue。

### 阻塞队列与非阻塞队

阻塞队列与普通队列的区别在于，当队列是空的时，从队列中获取元素的操作将会被阻塞，或者当队列是满时，往队列里添加元素的操作会被阻塞，直到其他的线程使队列重新变得空闲起来，如从队列中移除一个或者多个元素，或者完全清空队列.
1.ArrayDeque, （数组双端队列） 
2.PriorityQueue, （优先级队列） 
3.ConcurrentLinkedQueue, （基于链表的并发队列） 
4.DelayQueue, （延期阻塞队列）（阻塞队列实现了BlockingQueue接口） 
5.ArrayBlockingQueue, （基于数组的并发阻塞队列） 
6.LinkedBlockingQueue, （基于链表的FIFO阻塞队列） 
7.LinkedBlockingDeque, （基于链表的FIFO双端阻塞队列） 
8.PriorityBlockingQueue, （带优先级的无界阻塞队列） 
9.SynchronousQueue （并发同步阻塞队列）

实战：使用BlockingQueue模拟生产者与消费者

```java
class ProducerThread implements Runnable {
    private BlockingQueue<String> blockingQueue;
    private AtomicInteger count = new AtomicInteger();
    private volatile boolean flag = true;

    public ProducerThread(BlockingQueue<String> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    @Override
    public void run() {
        while (flag) {
            String data = this.count.incrementAndGet() + "";
            try {
                boolean offer = blockingQueue.offer(data, 2, TimeUnit.SECONDS);
                if (offer) {
                    System.out.println(Thread.currentThread().getName() + ",生产队列 " + data + "成功");
                } else {
                    System.out.println(Thread.currentThread().getName() + ",生产队列 " + data + "失败");
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(Thread.currentThread().getName() + ",生产者线程停止...");
    }

    public void stop() {
        this.flag = false;
    }
}

class ConsumerThread implements Runnable {
    private BlockingQueue<String> blockingQueue;
    private volatile boolean flag = true;

    public ConsumerThread(BlockingQueue<String> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    @Override
    public void run() {
        while (flag) {
            String data = null;
            try {
                data = this.blockingQueue.poll(2, TimeUnit.SECONDS);
                if (StringUtils.isEmpty(data)) {
                    flag = false;
                    System.out.println("消费者超过2秒未获得到消息,停止了");
                    return;
                }
                System.out.println("消费者获得队列消息成功，data：" + data);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

public class BlockingQueueTest {

    public static void main(String[] args) {
        BlockingQueue<String> blockingQueue = new LinkedBlockingDeque<>(3);
        ProducerThread producerThread = new ProducerThread(blockingQueue);
        ConsumerThread consumerThread = new ConsumerThread(blockingQueue);
        Thread t1 = new Thread(producerThread);
        Thread t2 = new Thread(consumerThread);
        t1.start();
        t2.start();
        try {
            Thread.sleep(10*1000);
            producerThread.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
```
## 4种线程池

Executor是线程池的顶级接口，但严格来说只是执行线程的工具，不是线程池。
ExecutorService是真正的线程池接口。

![](..\image\线程池UML.png)

### newCachedThreadPool 

调用 execute 将重用以前构造的线程（如果线程可用）。如果现有线程没有可用的，则创建一个新线程并添加到池中。终止并从缓存中移除那些已有 60 秒钟未被使用的线程。 

### newFixedThreadPool  

创建一个可重用固定线程数的线程池，以共享的无界队列方式来运行这些线程。如果在关闭前的执行期间由于失败而导致任何线程终止，那么一个新线程将代替它执行后续的任务（如果需要）。在某个线程被显式地关闭之前，池中的线程将一直存在。  

### newScheduledThreadPool  

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

### newSingleThreadExecutor  

这个线程池只有一个线程 ,这个线程池可以在线程死后（或发生异常时）重新启动一个线程来替代原来的线程继续执行下去  

### 自定义线程池

如果当前线程池中的线程数目小于corePoolSize，则每来一个任务，就会创建一个线程去执行这个任务；
如果当前线程池中的线程数目>=corePoolSize，则每来一个任务，会尝试将其添加到任务缓存队列当中，若添加成功，则该任务会等待空闲线程将其取出去执行；若添加失败（一般来说是任务缓存队列已满），则会尝试创建新的线程去执行这个任务；
如果队列已经满了，则在总线程数不大于maximumPoolSize的前提下，则创建新的线程
如果当前线程池中的线程数目达到maximumPoolSize，则会采取任务拒绝策略进行处理；
如果线程池中的线程数量大于 corePoolSize时，如果某线程空闲时间超过keepAliveTime，线程将被终止，直至线程池中的线程数目不大于corePoolSize；如果允许为核心池中的线程设置存活时间，那么核心池中的线程空闲时间超过keepAliveTime，线程也会被终止。

```java
public class ThreadPoolTest {
    public static void main(String[] args) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                1, 2, 60L,
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(3));
        for (int i = 0; i < 5; i++) {
            MyThread2 t1 = new MyThread2("任务" + i);
            executor.execute(t1);
        }
        executor.shutdown();
    }
}

class MyThread2 implements Runnable {

    private String taskName;

    public MyThread2(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + taskName);
    }
}
```



## 线程的生命周期

![](..\image\线程的生命周期.png)

## 终止线程的4种方法

- 程序运行结束
- 使用退出标志，打断循环（也就是使程序运行结束）
- 使用 interrupt()方法  
  - 处于阻塞状态时，会抛出InterruptedException ，然后在catch中用break结束线程
  - 未处于阻塞状态，使用isInterrupted()方法设置中断循环的标注，使用interrupt()就能退出循环终止线程
- 使用thread.stop()，线程不安全。强制释放锁，可能会导致数据被破坏线程安全
- 

## sleep和wait区别

|       | 属于对象 |  锁  |       恢复       |
| :---: | :------: | :--: | :--------------: |
| sleep |  Thread  | 保持 |    设定时间后    |
| wait  |  Object  | 释放 | 调用notify()方法 |

## start和run区别

start开启一条线程，run在当前线程执行代码。

## 守护线程

为用户线程提供公共服务，在没有用户线程可服务时会自动离开。  与系统“同生共死”。

- setDaemon(true)  可将用户线程设置成守护线程

- 在 Daemon 线程中产生的新线程也是 Daemon 的。  

- 垃圾回收线程就是一个经典的守护线程  