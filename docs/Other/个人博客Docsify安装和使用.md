## 1. 安装

### 1. 安装docsify

安装node，然后建议全局安装docsify-cli，有助于在本地初始化和预览网站。

```
npm i docsify-cli -g
```

安装后，检查安装结果，输入`docsify`，会显示命令帮助，则说明安装成功

```shell
# docsify 
Usage: docsify <init|serve> <path>
...
```

### 2. 安装git(略)

### 3. 初始化并运行

- 在git中创建一个仓库，记得勾选初始化Readme，然后clone到服务器里。

  ```shell
  # git clone https://github.com/xxxxx/projectname.git
  
  # cd projectname/
  
  # docsify init ./docs
  
  Initialization succeeded! Please run docsify serve ./docs
  
  # docsify serve ./docs
  
  Serving /usr/local/blog/jack-docsify/docs now.
  Listening at http://localhost:3000
  ```

- 打开浏览器访问 ip:3000，访问到Readme.md的内容，就证明搭建成功。

更多操作参考[官方中文文档](https://docsify.js.org/#/zh-cn/)