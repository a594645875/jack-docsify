### 手动安装最新版本

1. 安装依赖	
    ```shell
    yum install curl-devel expat-devel gettext-devel openssl-devel zlib-devel asciidoc

    yum install gcc
    ```

2. 卸载原来旧版本

    ```
    yum remove git	
    ```

3. 安装最新版本（[查看最新版本](https://mirrors.edge.kernel.org/pub/software/scm/git/)）

    ```
    cd /usr/local/src/
    
    wget https://mirrors.edge.kernel.org/pub/software/scm/git/git-2.9.5.tar.xz
    
    tar -vxf git-2.9.5.tar.xz
    
    cd git-2.9.5
    
    make prefix=/usr/local/git all
    
    make prefix=/usr/local/git install
    
    echo "export PATH=$PATH:/usr/local/git/bin" >> /etc/profile
    
    source /etc/profile
    ```

### 测试 

查询版本，成功！

```
# git --version
git version 2.9.5
```

### 异常

1. 如果解压出现以下问题，需要安装队xz的支持插件

    异常

    ```
    tar (child): xz: Cannot exec: No such file or directory
    ```

    安装

    ```
    yum install -y xz
    ```

2. make的过程出现以下问题

   ```
   /usr/bin/perl Makefile.PL PREFIX='/usr/local/git' INSTALL_BASE='' --localedir='/usr/local/git/share/locale'
   Can't locate ExtUtils/MakeMaker.pm in @INC (@INC contains: /usr/local/lib64/perl5 /usr/local/share/perl5 /usr/lib64/perl5/vendor_perl /usr/share/perl5/vendor_perl /usr/lib64/perl5 /usr/share/perl5 .) at Makefile.PL line 3.
   BEGIN failed--compilation aborted at Makefile.PL line 3.
   make[1]: *** [perl.mak] Error 2
   make: *** [perl/perl.mak] Error 2
   ```

   查询百度是是perl的问题，需要安装perl-devel

   ```
   yum -y install perl-devel
   ```

   安装完就可以make成功了。

3. git clone 报错SSL connect error

   因为缺少对应库，安装即可

   ```
   yum update -y nss curl libcurl
   ```
4. 使用yum命令直接安装git

    ```
    sudo yum install -y git
    ```

    安装完成查看版本，表示安装完成

    ```
    # git --version
    git version 1.7.1
    ```

    **使用以上命令安装的Git版本太低，导致拉代码的时候出现以下异常**

    ```shell
    # git clone https://github.com/a594645875/jack-docsify.git
    Initialized empty Git repository in /usr/local/blog/jack-docsify/.git/
    error:  while accessing https://github.com/a594645875/jack-docsify.git/info/refs
    
    fatal: HTTP request failed
    ```
    
    所以不能使用yum命令直接安装。
