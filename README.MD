mybatis生成工具拓展
1、generator工程中,是生成工具类的核心,在mybatis-generator的基础上添加自己的一些拓展
比如：mybatis分页查询,和查询单个,条件查询,添加日期起始日期查询等。
2、原理就是继承PluginAdapter实现自已的逻辑
3、MybatisGeneratorUtilForIDEA的main方法为入口，需要在resoures中配置数据库的一些信息
还有实体类信息,generatorConfig.xml中大部分和mybatis-generator原始配置一样,稍作修改的是对应的配置路径
以及一些参数。# generator-extend