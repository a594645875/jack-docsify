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

