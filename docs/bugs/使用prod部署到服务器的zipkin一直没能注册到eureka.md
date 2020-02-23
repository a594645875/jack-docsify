#### Spring-Config1
场景:使用prod部署到服务器的zipkin一直没能注册到eureka，gitlab中的zipkin配置明明没有错误

错误分析:复制出`bootstrap-prod.yml`时，没有把里面的`spring.profiles.active=dev`修改为prod

解决办法:把`bootstrap-prod.yml`里的`spring.profiles.active=dev`修改为`prod`







