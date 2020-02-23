#### 使用mybatis无法增改json类型数据

问题:查询的时候String是可以接受json类型的数据的,向pgsql插入String类型的数据到json类型的列的时候,会报错类型错误  
解决:1.使用类型转换器TypeHandler(比较麻烦,还没掌握)  
		 2.使用`::json`转换类型sql符号,  
		 例如:`insert into test VALUES('a','b','{"a":"b"}'::json)`,在mapper.xml中使用`#{a}::json`即可插入

