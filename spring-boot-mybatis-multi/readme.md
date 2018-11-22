## 概述
先聊一聊业务背景,随着系统服务的不断开发,我们的系统会充斥着各种个样的业务.这种时候,我们应该要开始考虑一下如何将系统的粒度细化.举个常见的例子: 电商系统可以拆分为 商品模块,订单模块,地址模块等等.这些模块都可以独立抽取出来,形成一个单独的服务.这就会涉及到各个模块之间的通信问题,一些简单的服务,我们可以通过 `rpc` 接口 直接进行通信,但是有些服务却不适用这种模式.本文主要讲一下在`多数据源`路上遇到的一些坑.


#### 多数据源

![](https://user-gold-cdn.xitu.io/2018/11/20/167301bb7e114fc9?w=536&h=305&f=png&s=17192)


#### 项目结构
源码地址: https://github.com/jaycekon/SpringBoot/tree/master/spring-boot-mybatis-multi

![目录结构](https://user-gold-cdn.xitu.io/2018/11/21/1673569baf8ab7ae?w=448&h=580&f=png&s=55863)

配置文件: `DataSourceConfig`
``` java
    @Bean(name = "masterDataSource")
    @Qualifier("masterDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource masterDataSource() {
        return DataSourceBuilder.create().build();
    }


    @Bean(name = "slaveDataSource")
    @Qualifier("slaveDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.db2")
    public DataSource slaveDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    public DynamicDataSource dataSource(@Qualifier("masterDataSource") DataSource master,
                                        @Qualifier("slaveDataSource") DataSource slave) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DatabaseType.db1, master);
        targetDataSources.put(DatabaseType.db2, slave);

        DynamicDataSource dataSource = new DynamicDataSource();
        dataSource.setTargetDataSources(targetDataSources);// 该方法是AbstractRoutingDataSource的方法
        dataSource.setDefaultTargetDataSource(master);// 默认的datasource设置为myTestDbDataSource

        return dataSource;
    }


    @Bean
    public SqlSessionFactory sqlSessionFactory(@Qualifier("masterDataSource") DataSource myTestDbDataSource,
                                               @Qualifier("slaveDataSource") DataSource myTestDb2DataSource) throws Exception {
        SqlSessionFactoryBean fb = new SqlSessionFactoryBean();
        fb.setDataSource(this.dataSource(myTestDbDataSource, myTestDb2DataSource));
        fb.setTypeAliasesPackage(env.getProperty("mybatis.type-aliases-package"));
        fb.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(env.getProperty("mybatis.mapper-locations")));
        return fb.getObject();
    }
```

项目创建流程可以参: [《Spring-Mybatis 读写分离》](https://juejin.im/post/5a61a0475188257324724345)

#### 数据库

test_1:
``` sql
CREATE TABLE `school` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `school_name` varchar(255) DEFAULT NULL,
  `province` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
```
test_2:
```sql
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
```



## 1、数据库链接异常

此数据库链接异常,指的是在 `切换数据源` 时,数据库链接异常

启动我们的服务:

![](https://user-gold-cdn.xitu.io/2018/11/21/167357fe6d11e5c9?w=1767&h=87&f=png&s=55325)

说明我们的服务配置是没有什么问题的,那么所谓的数据库链接异常又是什么回事呢?

Test:
```java
    @Autowired
    private SchoolService schoolService;

    @Autowired
    private UserService userService;

    @Test
    public void addUser() {
        userService.inserUser("root2","root2");
    }
    
    @Test
    public void addSchool() {
        schoolService.addSchool("ceshi1", "ceshi1");
    }
```
通过注解设置数据源:
```
@Service
@DataSource("db2")
public class UserService

@Service
@DataSource("db1")
public class SchoolService
```

我们创建了一个测试类,来检测两个数据源处理情况

![](https://user-gold-cdn.xitu.io/2018/11/21/1673583872296918?w=1613&h=345&f=png&s=124129)

从结果来看:

1、`schoolService` 成功了 (db:`test_1`)

2、`UserService` 失败了( db:`test_2`)

errorMessage:
```
org.springframework.jdbc.BadSqlGrammarException: 
### Error updating database.  Cause: java.sql.SQLSyntaxErrorException: Table 'test_1.user' doesn't exist
### The error may involve com.jaycekon.mybatis.multi.mapper.UserMapper.insert-Inline
### The error occurred while setting parameters
### SQL: INSERT INTO `user`(`username`, `password`)          VALUES ( ?, ?);
### Cause: java.sql.SQLSyntaxErrorException: Table 'test_1.user' doesn't exist
; bad SQL grammar []; nested exception is java.sql.SQLSyntaxErrorException: Table 'test_1.user' doesn't exist
```

上述异常,即我们可能会遇到的第一个坑: `UserService` 中的数据源链接异常

### 异常分析
1、数据源链接的是 `test_1` 说明没有成功切换数据源

2、观察切面方法,监听的是 `dataSource`
```
 @Before("@annotation(com.jaycekon.mybatis.multi.config.DataSource)")
```

3、`@DataSource`
```
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE})
public @interface DataSource 
```

通过上述注解可以发现,我们注解对象为 TYPE(类),而在 `AspectJ` 中的注解监听,只支持方法注解监听,并不能监听类的注解.因此,在上述我们通过注解整个类的方式,并不能做到数据源动态切换:
```
@Service
@DataSource("db2")
public class UserService

@Service
@DataSource("db1")
public class SchoolService
```

### 解决办法
1、修改 `DataSource` 为方法注解,对每个需要切换数据源的方法进行监听.该方法 比较**蠢**.

2、通过`@Pointcut("execution(* com.jaycekon.demo.mapper.*.*(..))")` 通过Pointcut 的形式,可以监听到某个包下面的所有类,所有方法.这个方法还行,但是每次如果创建了新的类,有可能需要修改配置.

3、目前采用的方式为,将不同数据源的`mapper`,`type-aliases`,`config` 分开
配置方式可参考: [传送门](https://www.bysocket.com/?p=1712)

修改后目录(配置文件只需保留两项即可):


![](https://user-gold-cdn.xitu.io/2018/11/22/16739158992fbe3e?w=431&h=718&f=png&s=67200)

## 2、Mapper 映射异常

在我们修改新的配置文件后,可以参考下面代码(db2 类似):
```
@Configuration
@MapperScan(value = "com.jaycekon.mybatis.multi.mapper.db1")
@EnableTransactionManagement
public class DataSourceConfig {

    private static final String MAPPER_LOCATION = "mybatis.mapper-locations.db1";

    @Autowired
    private Environment env;


    @Bean(name = "masterDataSource")
    @Qualifier("masterDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource masterDataSource() {
        return DataSourceBuilder.create().build();
    }



    @Bean(name = "db1SqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("masterDataSource") DataSource myTestDbDataSource) throws Exception {
        SqlSessionFactoryBean fb = new SqlSessionFactoryBean();
        fb.setDataSource(myTestDbDataSource);
        fb.setTypeAliasesPackage(env.getProperty("mybatis.type-aliases-package"));
        fb.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(env.getProperty(MAPPER_LOCATION)));
        return fb.getObject();
    }


    @Bean
    public DataSourceTransactionManager transactionManager(@Qualifier("masterDataSource") DataSource myTestDbDataSource) {
        return new DataSourceTransactionManager(myTestDbDataSource);
    }
}
```

其实这里的配置文件隐藏了一个坑,在我们启动编译时,并不会出现什么问题,但是当我们访问 `(db2)` 的时候,问题就来了:

![](https://user-gold-cdn.xitu.io/2018/11/22/16739421b032913a?w=1802&h=444&f=png&s=213590)

```
org.apache.ibatis.binding.BindingException: Invalid bound statement (not found): com.jaycekon.mybatis.multi.mapper.db2.UserMapper.insert
```

我们可以看到,`db1(school)` 的单元测试没有问题,但是 `db2(user)` 却出了问题.

### 异常分析
1、`Mapper` 扫描没有找到对应的 `XML` 文件

2、多数据源存在多个 `SqlSessionFactory` ,需要将 `Mapper`文件绑定到对应的 `SqlSessionFactory`

3、解决办法,在扫描 `Mapper` 时,将其绑定到对应的 `SqlSessionFactory` :
```
@MapperScan(value = "com.jaycekon.mybatis.multi.mapper.db2", sqlSessionFactoryRef = "db2SqlSessionFactory")
```
在 `@MapperScan` 中可以看到对应的解释:
```
   * Specifies which {@code SqlSessionFactory} to use in the case that there is
   * more than one in the spring context. Usually this is only needed when you
   * have more than one datasource.
```


启动测试类--`pass` ,启动程序-- `pass`

![](https://user-gold-cdn.xitu.io/2018/11/22/1673949a7cd3291d?w=306&h=101&f=png&s=11533)

![](https://user-gold-cdn.xitu.io/2018/11/22/167394a03ed1fa2d?w=1765&h=83&f=png&s=51901)

如果你觉得这个坑到这里就结束了,你就太小看我了~

## 2.1 TypeAliases 映射
正常来说,我们单元测试 & 服务都没有问题,讲道理是能够正常进行接下来的开发了.但是,我们如果使用的是 `Spring-Boot` 进行开发,那我们在发布前就还需要做一个操作 打包 `Jar包` ,随后用命令行启动服务:

`java -jar target/spring-boot-mybatis-multi.jar`

And Then,然后就会出现下述问题:
```
Failed to parse mapping resource: 'class path resource [mybatis-mappers/db2/UserMapper.xml]';
nested exception is org.apache.ibatis.builder.BuilderException: Error parsing Mapper XML.
Cause: org.apache.ibatis.builder.BuilderException: Error resolving class. 
Cause: org.apache.ibatis.type.TypeException: Could not resolve type alias 'User'. 
Cause: java.lang.ClassNotFoundException: Cannot find class: User

```

在配置 `SqlSessionFactory` 我们已经设置了 `TypeAliasesPackage` 的扫描路径:
```
    @Bean(name = "db1SqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("masterDataSource") DataSource myTestDbDataSource) throws Exception {
        ...
        fb.setTypeAliasesPackage(env.getProperty("mybatis.type-aliases-package"));
        ...
    }
```

但是他并没有起任何作用,这是为什么呢?

### 异常分析
1、别名扫描没有起作用

2、到Github 查找相关内容,会发现有相同的经历: [传送门](https://github.com/mybatis/spring-boot-starter/issues/38)

### 解决办法
1、不使用别名(`不是个好办法`)

![](https://user-gold-cdn.xitu.io/2018/11/22/1673953fcf49c31d?w=718&h=154&f=png&s=29347)

2、在`mybatis/spring-boot-starter` 这个项目中,提出了一个官方的 [Demo](https://github.com/mybatis/spring-boot-starter/blob/a92ba00d578221acb7dd01ef7a7e5fa25456d467/mybatis-spring-boot-autoconfigure/src/main/java/org/mybatis/spring/boot/autoconfigure/MybatisAutoConfiguration.java#L118-L120)

我们截取中间比较关键的一部分代码:
```
    SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
    factory.setDataSource(dataSource);
    factory.setVfs(SpringBootVFS.class);
```

我们采用`方法2` 尝试一下,看看能不能解决问题:

![](https://user-gold-cdn.xitu.io/2018/11/22/167395df0477a5eb?w=1838&h=105&f=png&s=54433)
关于 `VFS` 的一些解释:
```
虚拟文件系统(VFS),用来读取服务器里的资源
```
个人理解为,新创建的 `SqlSessionFactory` 没有能够加载配置文件,导致除 `@Primary` 外的所有 `SqlSessionFactory` 都没办法加载相关配置文件.

## 3、Config 异常
一路配置下来,单元测试跑通了,服务启动也成功了,接下来就是一顿骚操作,各种功能开发~ 在开发完成后,进入测试阶段.一看数据返回,坑爹啊~~

![](https://user-gold-cdn.xitu.io/2018/11/22/16739825c739bedf?w=1088&h=395&f=png&s=48755)

怎么返回了个空数据?

### 异常分析
1、数据有返回,服务没有问题

2、`schoolName` 对应 数据库 `school_name`,中间转换需要使用驼峰命名转换

![](https://user-gold-cdn.xitu.io/2018/11/22/1673984322f884f7?w=640&h=355&f=png&s=442674)

驼峰命名转换 `mybatis.configuration.map-underscore-to-camel-case` 出问题了.

### 解决办法
1、添加配置 `mybatis.configuration.map-underscore-to-camel-case=true`

2、创建 `MybatisConfig` 配置类(`db2` 类似):
```
    @Bean
    @ConfigurationProperties(prefix = "mybatis.configuration")
    @Scope("prototype")
    public org.apache.ibatis.session.Configuration globalConfiguration() {
        return new org.apache.ibatis.session.Configuration();
    }
    
    @Bean(name = "db1SqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("masterDataSource") DataSource myTestDbDataSource,
                                               org.apache.ibatis.session.Configuration config) throws Exception {
        ...
        fb.setConfiguration(config);
        ...
    }
```

3、` @Scope("prototype")` 这里配置类使用的是多实例作用域,主要是为了解决单例模式会影响到数据源的链接.



### 数据库连接超时
当你屁颠屁颠的把项目发布到服务器,接口调试都没有问题.过了一晚突然发现,服务挂了,what happen?
```
{
    "msg": "\n### Error updating database.  Cause: com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException: No operations allowed after connection closed.\n### SQL: ******\n###
    Cause: com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException: No operations allowed after connection closed.\n; SQL [];
    No operations allowed after connection closed.; nested exception is com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException: 
    No operations allowed after connection closed.",
    "code": 500
}
```

`MySQL5.0`以后针对超长时间DB连接做了一个处理，如果一个`DB连接`在无任何操作情况下过了8个小时后(Mysql 服务器默认的“wait_timeout”是8小时)，Mysql会自动把这个连接关闭。这就是问题的所在，在连接池中的`connections`如果空闲超过8小时，mysql将其断开，而连接池自己并不知道该`connection`已经失效，如果这时有 `Client`请求`connection`，连接池将该失效的`Connection`提供给`Client`，将会造成上面的异常。
所以配置datasource时需要配置相应的连接池参数，定时去检查连接的有效性，定时清理无效的连接。[引用](https://www.jianshu.com/p/1626d41572f2)

解决办法-完善相关配置:
```
spring.datasource.jdbcUrl=jdbc:mysql://localhost:3306/test_1
spring.datasource.username=root
spring.datasource.password=123456
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.default-auto-commit = false
spring.datasource.default-read-only = true
spring.datasource.max-idle = 10
spring.datasource.max-wait = 10000
spring.datasource.min-idle = 5
spring.datasource.initial-size = 5
spring.datasource.validation-query = SELECT 1
spring.datasource.test-on-borrow = false
spring.datasource.test-while-idle = true
spring.datasource.time-between-eviction-runs-millis = 18800


spring.datasource.db2.jdbcUrl=jdbc:mysql://localhost:3306/test_2
spring.datasource.db2.username=root
spring.datasource.db2.password=123456
spring.datasource.db2.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.db2.default-auto-commit = false
spring.datasource.db2.default-read-only = true
spring.datasource.db2.max-idle = 10
spring.datasource.db2.max-wait = 10000
spring.datasource.db2.min-idle = 5
spring.datasource.db2.initial-size = 5
spring.datasource.db2.validation-query = SELECT 1
spring.datasource.db2.test-on-borrow = false
spring.datasource.db2.test-while-idle = true
spring.datasource.db2.time-between-eviction-runs-millis = 18800

```



## 4、事务异常
由于我们在多数据源中,采用了多 `sqlSessionFactory` 方式,因此在事务管理这块,会出现事务管理异常相关问题,有兴趣的童鞋可以参考:https://www.atomikos.com/Main/WebHome ,推荐一个整合的 [Demo](https://my.oschina.net/u/1760791/blog/1605367)



## 总结
上述的所有问题,都是在开发过程中所遇到,可能各位或多或少有遇到过,希望能给各位相关帮助.

如对个人见解有所异议,欢迎指正.

Demo地址: