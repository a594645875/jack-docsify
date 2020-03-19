## 使用编程事务实现

建表，Mybatis-Plus代码生成，略。。。

创建事务管理工具类：

```java
@Component
public class TransactionUtils {

    @Autowired
    private DataSourceTransactionManager dataSourceTransactionManager;

    /**
     * 开启事务
     * @return
     */
    public TransactionStatus begin() {
        return dataSourceTransactionManager.getTransaction(new DefaultTransactionAttribute());
    }

    /**
     * 提交事务
     * @param transactionStatus
     */
    public void commit(TransactionStatus transactionStatus) {
        dataSourceTransactionManager.commit(transactionStatus);
    }

    /**
     * 回滚事务
     * @param transactionStatus
     */
    public void rollback(TransactionStatus transactionStatus) {
        dataSourceTransactionManager.rollback(transactionStatus);
    }
}
```

service两次操作数据中使用事务管理工具类：

```java
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements IStudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private TransactionUtils transactionUtils;

    @Override
    public void addTwo() {
        TransactionStatus transactionStatus = null;
        try {
            transactionStatus =  transactionUtils.begin();
            studentMapper.insert(new Student(2, "小明", 18));
            //int a = 1 / 0;
            studentMapper.insert(new Student(3, "小红", 18));
            transactionUtils.commit(transactionStatus);
        } catch (Exception e) {
            e.printStackTrace();
            transactionUtils.rollback(transactionStatus);
        }
    }
}
```

## AOP技术封装手动事务

是在编程事务的基础上进行封装

AOP切面类：

```java
@Component
@Aspect
public class AopTransaction {

    @Autowired
    private TransactionUtils transactionUtils;

    @AfterThrowing("execution(* top.czcheng.projectdemo.biz.service.IStudentService.aopAdd(..))")
    public void afterThrowing() {
        System.out.println("程序已经回滚");
        // 获取程序当前事务 进行回滚
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    }

    @Around("execution(* top.czcheng.projectdemo.biz.service.IStudentService.aopAdd(..))")
    public void around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        System.out.println("开启事务");
        TransactionStatus begin = transactionUtils.begin();
        proceedingJoinPoint.proceed();
        transactionUtils.commit(begin);
        System.out.println("提交事务");
    }
}
```

service两次操作数据中使用AOP切面类：

```java
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements IStudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private TransactionUtils transactionUtils;

    @Override
    public void aopAdd() {
        studentMapper.insert(new Student(2, "小明", 18));
        System.out.println("保存第一条数据");
        int a = 1 / 0;
        studentMapper.insert(new Student(3, "小红", 18));
        System.out.println("保存第二条数据");
    }
}
```

## Spring注解版本事务

是在AOP事务的基础上进行封装

注解类：

```java
@Target(value = {ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExtTransaction {
}
```

AOP切面类：

```java
Component
@Aspect
@Scope("prototype") // 设置成原型解决线程安全
public class AopTransaction {

    @Autowired
    private TransactionUtils transactionUtils;
//此回滚有问题
//    @AfterThrowing("execution(* top.czcheng.projectdemo.biz.service.*.*.*(..))")
//    public void afterThrowing() {
//        TransactionStatus transactionStatus = TransactionAspectSupport.currentTransactionStatus();
//        if (null != transactionStatus) {
//            System.out.println("程序发生异常，进行回滚");
//            // 获取程序当前事务 进行回滚
//            transactionStatus.setRollbackOnly();
//        }
//    }

    @Around("execution(* top.czcheng.projectdemo.biz.service.*.*.*(..))")
    public void around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        //1. 查询目标方法是否加上注解
        ExtTransaction extTransaction = getExtTransaction(proceedingJoinPoint);
        //2. 如果有注解，就开启事务
        TransactionStatus transactionStatus = null;
        if (null != extTransaction) {
            System.out.println("自定义注解 开启事务");
            transactionStatus = transactionUtils.begin();
        }
        //3. 执行目标方法
        proceedingJoinPoint.proceed();
        //4. 如果开启了事务，就提交事务
        if (null != transactionStatus) {
            System.out.println("自定义注解 提交事务");
            transactionUtils.commit(transactionStatus);
        }
    }

    /**
     * 获得方法上的@ExtTransaction注解
     * @param proceedingJoinPoint
     * @return
     * @throws NoSuchMethodException
     */
    private ExtTransaction getExtTransaction(ProceedingJoinPoint proceedingJoinPoint) throws NoSuchMethodException {
        //获取目标对象
        Class<?> classTarget = proceedingJoinPoint.getTarget().getClass();
        //获取方法名称
        String methodName = proceedingJoinPoint.getSignature().getName();
        //获取目标对象类型
        Class[] parameterTypes = ((MethodSignature) proceedingJoinPoint.getSignature()).getParameterTypes();
        // 获取目标对象方法
        Method method = classTarget.getMethod(methodName, parameterTypes);
        //获得方法上的指定注解
        return method.getDeclaredAnnotation(ExtTransaction.class);
    }
}
```

执行方法上：

```java
    @Override
    @ExtTransaction
    public void aopAdd() {
        studentMapper.insert(new Student(2, "小明", 18));
        System.out.println("保存第一条数据");
        //int a = 1 / 0;
        studentMapper.insert(new Student(3, "小红", 18));
        System.out.println("保存第二条数据");
    }
```

执行结果：

```
自定义注解 开启事务
保存第一条数据
保存第二条数据
自定义注解 提交事务
```

