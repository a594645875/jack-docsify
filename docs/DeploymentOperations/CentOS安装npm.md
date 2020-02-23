### 1. 安装NodeSource yum存储库

当前LTS版本的Node.js是版本10.x。如果你想安装的版本8只更改`setup_10.x`与`setup_8.x`下面的命令。

运行以下[curl命令](https://linuxize.com/post/curl-command-examples/)，将NodeSource yum存储库添加到您的系统中：

```shell
curl -sL https://rpm.nodesource.com/setup_10.x | sudo bash -
```

### 2. 安装Node.js和npm

启用NodeSource存储库后，通过键入以下命令安装Node.js和npm：

```shell
sudo yum install -y nodejs
```

### 3. 测试

要检查安装是否成功，请运行以下命令，这些命令将打印Node.js和npm版本。

打印Node.js版本：

```
node --version
v10.13.0
```

打印npm版本：

```
npm --version
6.4.1
```