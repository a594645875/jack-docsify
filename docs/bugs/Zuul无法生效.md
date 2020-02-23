#### Zuul无法生效

问题:zuul启动后, 输入`http://myzuul.com:9001/SPRINGCLOUD-PROVIDER-DEPT/dept/findAll`路由功能无法生效

分析:URL要区分大小写,应该用小写

解决:把url中的服务名称改成路由映射的名称,映射为全小写,把`http://myzuul.com:9001/SPRINGCLOUD-PROVIDER-DEPT/dept/findAll`改为`http://myzuul.com:9001/springcloud-provider-dept/dept/findAll`就可以了