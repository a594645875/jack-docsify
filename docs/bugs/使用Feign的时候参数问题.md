#### 使用Feign的时候参数问题

问题:使用feign做负载均衡时,测试带参数的url总是报错`PathVariable annotation was empty on param 0`,不带参数的url顺利通过

分析:使用Feign的时候,如果参数中带有`@PathVariable`形式的参数,则要用value=""标明对应的参数,否则会抛出`IllegalStateException`异常

解决:在feign接口上的参数上添加valus,把`@PathVariable Long deptNo`改为`@PathVariable(value = "deptNo") Long deptNo`