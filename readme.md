# Spring Web项目开发
在接触Spring boot 前，如果我们需要开发一个简单的Spring Web 程序，需要经历以下步骤：

- 一个项目结构，其中有一个包含必要依赖的Maven或者Gradle构建文件，最起码要有SpringMVC和Servlet API这些依赖。
- 一个web.xml文件（或者一个WebApplicationInitializer实现），其中声明了Spring的DispatcherServlet。
- 一个启用了Spring MVC的Spring配置。
- 一个控制器类，以“Hello World”响应HTTP请求。
- 一个用于部署应用程序的Web应用服务器，比如Tomcat。

在完成上述步骤的操作后，终于可以启动我们的Spring Web 程序。虽然步骤繁琐，但是在后续的开发过程中，这些步骤确实为我们节省了很多其他花销。但是Spring 的劣势还是很明显的，例如，我们只需要开发一个Hello world接口。那么在经历了上述那么多的步骤后，只有一个控制层 是和HelloWorld相关的。这让我们很难接受。


为解决这种复杂的问题，Spring boot 很好的提供了Spring boot CLI进行处理。首先我们看一下基于Groovy 的控制器类：

```
@RestController
class HelloController {
@RequestMapping("/")
def hello() {
return "Hello World"
}
```

相较于一个完整的Spring 项目来说，简洁了不少。然后我们使用Spring boot 的命令行界面（Command Line Interface），可以像下面这样运行 HelloController：


```
$ spring run HelloController.groovy
```



# Spring Boot 概述

## Spring Boot 精要

- **自动配置**：针对很多Spring 应用程序厂家爱你的应用功能
- **起步依赖**：告诉Spring Boot需要什么功能，它就能引入需要的库。
- **命令行界面**：这是Spring Boot的可选特性，借此你只需写代码就能完成完整的应用程序，无需传统项目构建。
- **Actuator**：让你能够深入运行中的Spring Boot应用程序，一探究竟。

 
###  1、自动配置

我们在使用Spring 进行配置时，可能会使用到一下的配置Bean 方法，下面这段代码非常简单的生命了一个JdbcTemplate 实例，并注入一个DataSource依赖。当然，我们还需要一个DataSource 的Bean，下述的DataSource 在创建时，会默认执行一些sql 方法。 由Datasource 和JdbcTemplate 共同完成了Spring 的Bean 配置。
```
@Bean
public JdbcTemplate jdbcTemplate(DataSource dataSource) {
return new JdbcTemplate(dataSource);
}


@Bean
public DataSource dataSource() {
return new EmbeddedDatabaseBuilder()
.setType(EmbeddedDatabaseType.H2)
.addScripts('schema.sql', 'data.sql')
.build();
}

```

区别与普通的Spring 配置，Spring boot 在应用程序的ClassPath里，如果发现了JdbcTemplate，那么他会为我们自动配置相应的Bean 实例。你无需关心Bean 的配置，Spring boot 会随时都能将其注入到你的Bean 里。


###  2、起步依赖

Spring Boot通过起步依赖为项目的依赖管理提供帮助。起步依赖其实就是特殊的Maven依赖和Gradle依赖，利用了传递依赖解析，把常用库聚合在一起，组成了几个为特定功能而定制的依赖。

例如，如果你需要使用到SpringMVC ，JSONObject以及其他相关的Web 依赖，你可能需要依次导入相关依赖，但是采用Spring-boot 的话，只需要利用起步依赖，即可将其他相关的依赖引入。

```
org.springframework.boot:spring-boot-starter-web
```

在导入了 spring-boot-starter-web 依赖后，会将一系列的相关依赖导入


### 3、命令行界面

Spring Boot CLI利用了起步依赖和自动配置，让你专注于代码本身。CLI能检测到你使用了哪些类，它知道要向Classpath中添加哪些起步依赖才能让它运转起来。一旦那些依赖出现在Classpath中，一系列自动配置就会接踵而来，确保启用DispatcherServlet和Spring MVC，这样控制器就能响应HTTP请求了。



### 4、Actuator 
Spring Boot的最后一块“拼图”是Actuator，其他几个部分旨在简化Spring开发，而Actuator则要提供在运行时检视应用程序内部情况的能力。

Actuator 具有以下特性：
- Spring应用程序上下文里配置的Bean
- Spring Boot的自动配置做的决策
- 应用程序取到的环境变量、系统属性、配置属性和命令行参数
- 应用程序里线程的当前状态
- 应用程序最近处理过的HTTP请求的追踪情况
- 各种和内存用量、垃圾回收、Web请求以及数据源用量相关的指标
 

## 总结
简而言之，从本质上来说，Spring Boot就是Spring，它做了那些没有它你自己也会去做的Spring Bean配置。谢天谢地，幸好有Spring，你不用再写这些样板配置了，可以专注于应用程序的逻辑，这些才是应用程序独一无二的东西。