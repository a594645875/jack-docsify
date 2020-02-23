1. 永久性生效，重启后不会复原

开启： chkconfig iptables on

关闭： chkconfig iptables off

2. 即时生效，重启后复原

开启： service iptables start

关闭： service iptables stop

3. 查询TCP连接情况：

 netstat -n | awk '/^tcp/ {++S[$NF]} END {for(a in S) print a, S[a]}'

4. 查询端口占用情况：

 netstat  -anp  |  grep portno*（例如：netstat –apn | grep 80）*