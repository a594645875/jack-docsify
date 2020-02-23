## 前端

做一个简单的文件上传页面

1. 创建一个SpringBoot项目， 在`resources/static`创建`index.html`，放在这个位置的目的：项目启动后，访问`ip:port`就可以访问该页面。

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <script src="js/jquery-3.4.1.min.js"></script>
    <script src="js/test.js"></script>
    <title>文件上传</title>
    <style>
        .myBtn{
            padding: 5px 10px;
            background: rgb(92,184,92);
            color: white;
            outline: none;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }
    </style>
</head>
<body>
<input type="file" id="file" style="display: none" onchange="upload(this)">
<button class="myBtn" onclick="fileBtn()">
    上传文件
</button>

<img src="" id="img" style="width: 200px">
</body>
</html>
```

2. 下载[`jquery-3.4.1.min.js`](http://www.jq22.com/jquery/jquery-3.4.1.zip)放在`resources/static/js`文件夹下
3. 在`resources/static/js`下创建文件`test.js`

```js
function fileBtn() {
    document.getElementById('file').click()
}

function upload(file) {
    let img = document.getElementById('img');
    let formData = new FormData();
    let temp = file.files[0];
    if (temp){
        formData.append('file',temp);
        img.src = window.URL.createObjectURL(temp);
        $.ajax({
            url:"/test/excel",
            type:"POST",
            data: formData,
            processData: false, // 告诉jQuery不要去处理发送的数据
            contentType: false, // 告诉jQuery不要去设置Content-Type请求头
            success: function(result){
                alert(result);
            }
        })
    }
}
```

启动项目，访问`localhost:8080`，点击上传按钮，弹出选择文件窗口，选择文件后，浏览器控制窗（按F12，选择network窗口）监控到请求`localhost:8080/test/excel`，说明前端成功。

## 后端

创建`TestController`处理前端传入Excel文件（也可以通过读取本地Excel文件，转换成流，本文不做描述）

```java
@RequestMapping("/test")
@RestController
public class TestController {

    @PostMapping(value = "/excel")
    public  List<Student> excel(@RequestParam("file") MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        List<Student> list = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            if (null != workbook && workbook.getNumberOfSheets() > 0) {
                //读取第一个工作表
                Sheet sheet = workbook.getSheetAt(0);
                for (Row row : sheet) {
                    //读取每一行数据,从第二行开始录入数据
                    if (row.getRowNum() >= 1) {
                        Student student = toBean(row);
                        if (null != student) {
                            list.add(student);
                        }

                    }
                }
            }
        }
        return list;
    }

    /**
     * 将一行数据转换成一个对象的属性
     * @param row
     * @return
     */
    private Student toBean(Row row) {
        Student student = new Student();
        int emptyHashCode = student.hashCode();
        for (Cell cell : row) {
            //逐格读取数据,根据列号填入属性
            switch (cell.getColumnIndex()) {
                case 0:
                    String sex = (String) ExcelUtil.getValue(cell);
                    if (!StringUtils.isEmpty(sex)) {
                        student.setSex(sex);
                    }
                    break;
                case 1:
                    String name = (String) ExcelUtil.getValue(cell);
                    if (!StringUtils.isEmpty(name)) {
                        student.setName(name);
                    }
                default:
                    break;
            }
        }
        //去掉空的对象
        if (emptyHashCode == student.hashCode()) {
            return null;
        }
        return student;
    }
    
}
```

使用到的工具类`ExcelUtil`

```java
public class ExcelUtil {
    
    public static Object getValue(Cell cell) {
        Object value = null;
        DecimalFormat decimalFormat = new DecimalFormat("#.####");
        switch (cell.getCellType()) {
            case STRING:
                value = cell.getRichStringCellValue().getString();
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    //也可将日期数据转换为指定格式
                    value = cell.getDateCellValue().toString();
                } else {
                    value = decimalFormat.format(cell.getNumericCellValue());
                }
                break;
            case BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case FORMULA:
                value = cell.getCellFormula();
                break;
            default:
                value = cell.getStringCellValue();
        }
        return value;
    }
    
}
```

## 测试

创建一个表格文件，内容如下（实测`xls`和`xlsx`两种表格格式都没问题）

| 性别 | 名字 |
| ---- | ---- |
| 男   | 小明 |
| 女   | 小红 |

访问`localhost:8080`,上传创建好的表格文件，debug跟踪后端代码，顺利转换成对象数据则说明成功。