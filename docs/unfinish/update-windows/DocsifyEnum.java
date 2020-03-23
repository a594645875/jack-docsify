public enum DocsifyEnum {
    /**
     - [Java核心](/JavaBase/)
     - [常用框架](/PopularFrameworks/)
     - [数据管理](/DataAdministration/)
     - [工具类](/toolClass/)
     - [踩坑集合](/bugs/)
     - [分布式架构](/DistributedArchitecture/)
     - [大数据](/BigData/)
     - [部署运维](/DeploymentOperations/)
     - [开发工具](/DeveloperKits/)
     - [网络](/network/)
     - [前端](/FrontEnd/)
     - [测试](/Test/)
     - [其他](/Other/)
     */
    JAVA("JavaBase","Java核心"),
    POPULAR_FRAMEWORKS("PopularFrameworks","常用框架"),
    DATA_ADMINISTRATION("DataAdministration","数据管理"),
    TOOL_CLASS("toolClass","工具类"),
    BUGS("bugs","踩坑集合"),
    DISTRIBUTED_ARCHITECTURE("DistributedArchitecture","分布式架构"),
    BIG_DATA("BigData","大数据"),
    DEPLOYMENT_OPERATIONS("DeploymentOperations","部署运维"),
    DEVELOPER_KITS("DeveloperKits","开发工具"),
    NETWORK("network","网络"),
    FRONT_END("FrontEnd","前端"),
    TEST("Test","测试"),
    OTHER("Other","其他"),
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