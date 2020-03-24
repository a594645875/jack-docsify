## 使用IDEA 启动tomcat 时控制台 中文乱码

### 设置项目编码 为utf-8 

File -> setting -> Editor -> File Encodings 

![](..\image\tomcat\乱码设置1.png)

### 在VM options填写-Dfile.encoding=UTF-8

Run -> Edit Configurations 

![](..\image\tomcat\乱码设置.png)

### 修改idea安装目录-bin启动配置

用记事本打开idea.exe.vmoptions和idea64.exe.vmoptions文件

在文件后面添加一行：-Dfile.encoding=UTF-8

![](..\image\tomcat\修改idea启动配置.png)

### 重启IDEA

然后就没乱码了。

> [参考文档](https://www.jianshu.com/p/2aa7284cb8a1)