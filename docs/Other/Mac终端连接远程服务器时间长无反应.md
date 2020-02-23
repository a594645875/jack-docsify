mac终端连接阿里云，离开时间长了再切回来的时候就无法操作了，随便的敲几下键盘要等一会才有反应说已经断开了ssh连接。

编辑 /etc/ssh/ssh_config 添加以下设置可解决这个问题：`sudo vi  /etc/ssh/ssh_config`，需要输入密码。

 ```shell
# 断开时重试连接的次数
ServerAliveCountMax 5

# 每隔5秒自动发送一个空的请求以保持连接
ServerAliveInterval 5
 ```

