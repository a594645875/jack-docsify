import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author czc
 * @Date 2020/2/25 22:48
 * @Version 1.0
 */
public class DocsifyAuto {

    private static final String DOC_PATH = "C:\\Users\\chenzecheng\\Desktop\\document\\study\\jack-docsify\\docs";

    public static void main(String[] args) throws IOException {
        File file = new File(DOC_PATH);
        File[] array = file.listFiles();
        if (array == null) {
            return;
        }
        List<IndexMsg> indexMsgs = new ArrayList<>();
        for (File subfile : array) {
            //���Ե��ļ���
            if ("image".equals(subfile.getName())) {
                continue;
            }
            if ("unfinish".equals(subfile.getName())) {
                continue;
            }
            //������ļ���
            if (subfile.isDirectory()) {
                //����ÿ���ļ��еĲ��������ҳ
                recreateSidebar(subfile,indexMsgs);
                //reNameFile(subfile);
            }
        }
        System.out.println("���²��������ҳ��ɡ�");
        //ƴ����ҳIndex�������б�
        reflashIndex(DOC_PATH,indexMsgs);
        System.out.println("������ҳ����������ɡ�");
    }

    private static void reflashIndex(String rootPath,List<IndexMsg> indexMsgs) {
        String path = rootPath + "/README.md";
        StringBuilder builder = new StringBuilder();
        builder.append("# [Jackson's Java֮��](/)\n");
        builder.append("## ȫ���ʼ�\n");
        List<IndexMsg> collect = indexMsgs.stream().sorted(Comparator.comparing(IndexMsg::getLastModify).reversed()).collect(Collectors.toList());
        collect.forEach(x -> builder.append("- [").append(x.getDocName().replace(".md", ""))
                .append("](/").append(x.getDirName()).append("/").append(x.getDocName()).append(")\n"));
//                .append("�޸���")
//                .append(LocalDateTime.ofEpochSecond(x.getLastModify() / 1000, 0,
//                        ZoneOffset.ofHours(8)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
//                .append("\n"));
        try (PrintStream sidebarStream = new PrintStream(path)) {
            sidebarStream.print(builder.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * ȥ���ļ����Ŀո�
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
        //�����޸�ʱ�䵹��
        //List<File> collect = Arrays.stream(docList).sorted(Comparator.comparing(File::lastModified).reversed()).collect(Collectors.toList());
        List<File> collect = Arrays.stream(docList).sorted(Comparator.comparing(File::getName)).collect(Collectors.toList());
        List<String> docNameList = new ArrayList<>();
        for (File doc : collect) {
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
        String sidebarPath = subfile.getAbsolutePath() + "/_sidebar.md";
        String readmePath = subfile.getAbsolutePath() + "/README.md";
        try (PrintStream sidebarStream = new PrintStream(sidebarPath);
             PrintStream readmeStream = new PrintStream(readmePath)) {
            sidebarStream.print(sidebar);
            readmeStream.print(sidebar);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * - [Java����](/JavaBase/)
     * - [��Ա�����;ֲ�����������](/JavaBase/��Ա�����;ֲ�����������.md)
     * - [ֵ���ݺ͵�ַ��������](/JavaBase/ֵ���ݺ͵�ַ��������.md.md)
     * - [JDK8����ʽ���](/JavaBase/JDK8����ʽ���.md)
     * - [JDK9ReactiveStream](/JavaBase/JDK9ReactiveStream.md)
     * - [JVM�������-����](/JavaBase/JVM�������-����.md)
     */
    private static String toSidebar(String directoryName, List<String> docNameList) {
        StringBuilder builder = new StringBuilder();
        String directoryCnName = directoryName;
        for (DocsifyEnum value : DocsifyEnum.values()) {
            if (value.getEnName().equalsIgnoreCase(directoryName)) {
                directoryCnName = value.getCnName();
            }
        }
        builder.append("- [").append(directoryCnName).append("](/)\n");
        for (String docName : docNameList) {
            builder.append("\t").append("- [").append(docName.replace(".md","")).append("](/").append(directoryName).append("/").append(docName).append(")\n");
        }
        return builder.toString();
    }
    
    static class IndexMsg{
        private String docName;
        private long lastModify;
        private String dirName;

        public IndexMsg(String docName, long lastModify, String dirName) {
            this.docName = docName;
            this.lastModify = lastModify;
            this.dirName = dirName;
        }

        public String getDocName() {
            return docName;
        }

        public void setDocName(String docName) {
            this.docName = docName;
        }

        public long getLastModify() {
            return lastModify;
        }

        public void setLastModify(long lastModify) {
            this.lastModify = lastModify;
        }

        public String getDirName() {
            return dirName;
        }

        public void setDirName(String dirName) {
            this.dirName = dirName;
        }
    }

}