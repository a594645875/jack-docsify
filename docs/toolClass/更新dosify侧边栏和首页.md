```java
/**
 * @Author czc
 * @Date 2020/2/25 22:48
 * @Version 1.0
 */
public class DocsifyAuto {

    public static void main(String[] args) throws IOException {
        String rootPath = "C:\\Users\\chenzecheng\\Desktop\\document\\study\\jack-docsify\\docs";
        File file = new File(rootPath);
        File[] array = file.listFiles();
        if (array == null) {
            return;
        }
        List<IndexMsg> indexMsgs = new ArrayList<>();
        for (File subfile : array) {
            if ("unfinish".equals(subfile.getName())) {
                continue;
            }
            //如果是文件夹
            if (subfile.isDirectory()) {
                recreateSidebar(subfile,indexMsgs);
                //reNameFile(subfile);

            }
        }
        //拼接首页Index，文章列表
        reflashIndex(rootPath,indexMsgs);
    }

    private static void reflashIndex(String rootPath,List<IndexMsg> indexMsgs) {
        String path = rootPath + "\\README.md";
        StringBuilder builder = new StringBuilder();
        builder.append("# [Jackson's Java之旅](/)\n");
        builder.append("## 最新文章\n");
        List<IndexMsg> collect = indexMsgs.stream().sorted(Comparator.comparing(IndexMsg::getLastModify).reversed()).collect(Collectors.toList());
        collect.forEach(x -> builder.append("- [").append(x.getDocName().replace(".md", ""))
                .append("](/").append(x.getDirName()).append("/").append(x.getDocName()).append(")\t")
                .append("修改于")
                .append(LocalDateTime.ofEpochSecond(x.getLastModify() / 1000, 0,
                        ZoneOffset.ofHours(8)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .append("\n"));
        try (PrintStream sidebarStream = new PrintStream(path)) {
            sidebarStream.print(builder.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 去掉文件名的空格
     *
     * @param subfile
     */
    private static void reNameFile(File subfile) {
        File[] docList = subfile.listFiles();
        if (docList == null) {
            return;
        }
        List<String> docNameList = new ArrayList<>();
        for (File doc : docList) {
            if (doc.getName().endsWith(".md")) {
                if (doc.getName().contains(" ")) {
                    File file = new File(doc.getAbsolutePath().replace(" ", ""));
                    doc.renameTo(file);
                }
            }
        }
    }

    private static void recreateSidebar(File subfile,List<IndexMsg> indexMsgs) {
        File[] docList = subfile.listFiles();
        if (docList == null) {
            return;
        }
        List<String> docNameList = new ArrayList<>();
        for (File doc : docList) {
            if ("_sidebar.md".equalsIgnoreCase(doc.getName())) {
                doc.delete();
            } else if ("readme.md".equalsIgnoreCase(doc.getName())) {
                doc.delete();
            } else if ("readme.md".equalsIgnoreCase(doc.getName())) {

            } else if (doc.getName().endsWith(".md")) {
                if (doc.getName().contains(" ")) {
                    System.out.println(doc.getName().replace(" ", ""));
                }
                indexMsgs.add(new IndexMsg(doc.getName(),doc.lastModified(),subfile.getName()));
                docNameList.add(doc.getName());
            }
        }
        String sidebar = toSidebar(subfile.getName(), docNameList);
        String sidebarPath = subfile.getAbsolutePath() + "\\_sidebar.md";
        String readmePath = subfile.getAbsolutePath() + "\\README.md";
        try (PrintStream sidebarStream = new PrintStream(sidebarPath);
             PrintStream readmeStream = new PrintStream(readmePath)) {
            sidebarStream.print(sidebar);
            readmeStream.print(sidebar);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * - [Java核心](/JavaBase/)
     * - [成员变量和局部变量的区别](/JavaBase/成员变量和局部变量的区别.md)
     * - [值传递和地址传递问题](/JavaBase/值传递和地址传递问题.md.md)
     * - [JDK8函数式编程](/JavaBase/JDK8函数式编程.md)
     * - [JDK9ReactiveStream](/JavaBase/JDK9ReactiveStream.md)
     * - [JVM深入理解-张龙](/JavaBase/JVM深入理解-张龙.md)
     */
    private static String toSidebar(String directoryName, List<String> docNameList) {
        StringBuilder builder = new StringBuilder();
        builder.append("- [").append(directoryName).append("](/").append(directoryName).append("/)\n");
        for (String docName : docNameList) {
            builder.append("\t").append("- [").append(docName.replace(".md","")).append("](/").append(directoryName).append("/").append(docName).append(")\n");
        }
        return builder.toString();
    }

    @Data
    @AllArgsConstructor
    static class IndexMsg{
        private String docName;
        private long lastModify;
        private String dirName;
    }

}

```

