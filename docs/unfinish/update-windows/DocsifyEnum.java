public enum DocsifyEnum {
    /**
     - [Java����](/JavaBase/)
     - [���ÿ��](/PopularFrameworks/)
     - [���ݹ���](/DataAdministration/)
     - [������](/toolClass/)
     - [�ȿӼ���](/bugs/)
     - [�ֲ�ʽ�ܹ�](/DistributedArchitecture/)
     - [������](/BigData/)
     - [������ά](/DeploymentOperations/)
     - [��������](/DeveloperKits/)
     - [����](/network/)
     - [ǰ��](/FrontEnd/)
     - [����](/Test/)
     - [����](/Other/)
     */
    JAVA("JavaBase","Java����"),
    POPULAR_FRAMEWORKS("PopularFrameworks","���ÿ��"),
    DATA_ADMINISTRATION("DataAdministration","���ݹ���"),
    TOOL_CLASS("toolClass","������"),
    BUGS("bugs","�ȿӼ���"),
    DISTRIBUTED_ARCHITECTURE("DistributedArchitecture","�ֲ�ʽ�ܹ�"),
    BIG_DATA("BigData","������"),
    DEPLOYMENT_OPERATIONS("DeploymentOperations","������ά"),
    DEVELOPER_KITS("DeveloperKits","��������"),
    NETWORK("network","����"),
    FRONT_END("FrontEnd","ǰ��"),
    TEST("Test","����"),
    OTHER("Other","����"),
    ;

    private String enName;
    private String cnName;

    DocsifyEnum(String enName, String cnName) {
        this.enName = enName;
        this.cnName = cnName;
    }

    public String getEnName() {
        return enName;
    }

    public String getCnName() {
        return cnName;
    }
}