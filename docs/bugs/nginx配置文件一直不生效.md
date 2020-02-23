#### 问题分析

准备给前端搭环境，于是在nginx的server加上一个location

 ```shell
        location /front {
             alias /usr/local/project/fsdjpt/front;
             # root /usr/local/project/fsdjpt/front; #一个server只能有一个root，其他的用alias
        }
 ```

然后`nginx -s reload`，以为可以了，

然而，一直试都是404！

开始排查，先检查配置文件：`nginx -t -c /usr/local/nginx/conf/nginx.conf`,结果显示success，没有错。

然后试了各种配置修改，甚至修改原来的配置，发觉也没影响nginx的运行，我就开始怀疑是不是没有使用`/usr/local/nginx/conf/nginx.conf``这个配置，但是用whereis nginx.conf只找到一些叫nginx的文件夹

然后我看到``nginx -t`会检查默认的配置，我就跑一下

```shell
[root@xxxx front]# nginx -t
nginx: the configuration file /etc/nginx/nginx.conf syntax is ok
nginx: configuration file /etc/nginx/nginx.conf test is successful
```

找到了！原来用的是这个配置`/etc/nginx/nginx.conf`，改之，成之！

#### 总结

1. 需要增加静态文件路径只需要增加一个location

```shell
        location /front {
             alias /usr/local/project/fsdjpt/front;
             # root /usr/local/project/fsdjpt/front; #一个server只能有一个root，其他的用alias
        }
```

2. 一个server中如果存在一个root路径，其他的用alias，同样有效

3. 修改nginx配置前，先确定在运行的nginx到底用的是哪个配置文件！用检查默认配置正确性的命令可以查到！`nginx -t`