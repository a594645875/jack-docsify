```yaml
#    1. 一个家庭有爸爸、妈妈、孩子。
#    2. 这个家庭有一个名字（family-name）叫做“happy family”
#    3. 爸爸有名字(name)和年龄（age）两个属性
#    4. 妈妈有两个别名
#    5. 孩子除了名字(name)和年龄（age）两个属性，还有一个friends的集合
#    6. 每个friend有两个属性：hobby(爱好)和性别(sex)
```
翻译成YAML
```yaml
family:
  family-name: "happy family"
  father:
    name: zimug
    age: 18
  mother:
    alias:
      - lovely
      - ailice
  child:
    name: zimug
    age: 5
    friends:
      - hobby: football
        sex:  male
      - hobby: basketball
        sex: female
```
或者是friends的部分写成

```yaml
 friends:
      - {hobby: football,sex:  male},
      - {hobby: basketball,sex: female}
```

### 规则1：字符串的单引号与双引号

- 双引号；不会转义字符串里面的特殊字符；特殊字符会作为本身想表示的意思，如：
  ​ name: “zhangsan \n lisi”：输出；zhangsan 换行 lisi
- 单引号；会转义特殊字符，特殊字符最终只是一个普通的字符串数据，如：
  ​ name: ‘zhangsan \n lisi’：输出；zhangsan \n lisi

### 规则2：支持松散的语法

松散语法 = 横杠和驼峰和下划线等价

family-name = familyName = family_name

## 二、配置文件占位符

Spring Boot配置文件支持占位符，一些用法如下

### 2.1 随机数占位符

```yaml
${random.value}
${random.int}
${random.long}
${random.int(10)}
${random.int[1024,65536]}
```

### 2.2 默认值

占位符获取之前配置的值，如果没有可以是用“冒号”指定默认值
格式例如，xxxxx.yyyy是属性层级及名称，如果该属性不存在，冒号后面填写默认值

```yaml
${xxxxx.yyyy:默认值}
```