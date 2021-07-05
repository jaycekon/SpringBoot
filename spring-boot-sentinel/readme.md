本文已参与好文召集令活动，点击查看：[后端、大前端双赛道投稿，2万元奖池等你挑战！](https://juejin.cn/post/6978685539985653767)

## 概述

书接上回：[ 你来说说什么是限流？](https://juejin.cn/post/6978887936569770020) ,限流的整体概述中，描述了 `限流是什么`，`限流方式`和`限流的实现`。在文章尾部的 `分布式限流`,没有做过多的介绍，选择了放到这篇文章中。给大伙细细讲解一下 `Sentinel`



<div align=center ><img src="https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/999bd67295d6470a8eec329f9e6eb83d~tplv-k3u1fbpfcp-watermark.image"/ width=400></div>



附带最权威的官方wiki： [《Alibaba-Sentinel,新手指南》](https://github.com/alibaba/Sentinel/wiki/%E6%96%B0%E6%89%8B%E6%8C%87%E5%8D%97)

本篇文章源码地址： https://github.com/jaycekon/SpringBoot


## Sentinel 是啥？

> 分布式系统的流量防卫兵

再引用一下之前我画的图：


![image.png](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/0e8207ef6fd14b8eb6486cc37a31dca4~tplv-k3u1fbpfcp-watermark.image)

`流量防卫兵` 它具备了哪些能力？


![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/1a16985926fc41289685833d34965a53~tplv-k3u1fbpfcp-watermark.image)



### Sentinel 的生态环境

随着 `Alibaba` 的 Java 生态建设，包括 `Spring Cloud Alibaba`，`Rocket`，`Nacos`等多项开源技术的贡献，目前` Sentinel` 对分布式的各种应用场景都有了良好的支持和适配，这也是为什么我们选择 `Sentinel` 学习的原因之一（学习成本低，应用场景多）

![image.png](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/ec510bf98d66454286d8dd78ffacaa24~tplv-k3u1fbpfcp-watermark.image)




<div align=center ><img src="https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/c6f0ccb1c49c42c7a745da25dac1f309~tplv-k3u1fbpfcp-watermark.image"/ width=400></div>


### Sentinel 核心概念

#### 1、资源

`资源` 是 `Sentinel` 中的**核心概念之一**。最常用的资源是我们代码中的 `Java 方法`,`一段代码`，或者`一个接口`。

Java方法:

```java
@SentinelResource("HelloWorld")
public void helloWorld() {
    // 资源中的逻辑
    System.out.println("hello world");
}
```

一段代码：

```java
        // 1.5.0 版本开始可以直接利用 try-with-resources 特性，自动 exit entry
try (Entry entry = SphU.entry("HelloWorld")) {
            // 被保护的逻辑
            System.out.println("hello world");
	} catch (BlockException ex) {
            // 处理被流控的逻辑
	    System.out.println("blocked!");
	}
```

一个接口：

```java
@RestController
public class TestController {
    @GetMapping("/test")
    public String test(){
        return "test";
    }
}
```

配合控制台使用：

![image.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/c243b7ee78a540e98e5453aff7650f2f~tplv-k3u1fbpfcp-watermark.image)

#### 2、规则

`Sentinel` 中的`规则` 提供给用户，针对不同的场景而制定不同的保护动作，规则的类型包括：

- `流量`控制规则
- `熔断`降级规则
- `系统保护`规则
- 来源访问控制规则
- 热点参数规则

本文主要会讲解 `流量`，`熔断` 和`系统保护`这三个规则。

定义规则：

```java
    private static void initFlowRules(){
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule();
        //绑定资源
        rule.setResource("HelloWorld");
        //限流阈值类型
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        //数量级别
        rule.setCount(20);
        //添加到本地内存
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }
```

`限流规则`重要属性说明：

| Field           | 说明                                                         | 默认值                        |
| --------------- | ------------------------------------------------------------ | ----------------------------- |
| resource        | 资源名，资源名是限流规则的作用对象                           |                               |
| count           | 限流阈值                                                     |                               |
| grade           | 限流阈值类型，QPS 模式（1）或并发线程数模式（0）             | QPS 模式                      |
| limitApp        | 流控针对的调用来源                                           | `default`，代表不区分调用来源 |
| strategy        | 调用关系限流策略：直接、链路、关联                           | 根据资源本身（直接）          |
| controlBehavior | 流控效果（直接拒绝/WarmUp/匀速+排队等待），不支持按调用关系限流 | 直接拒绝                      |
| clusterMode     | 是否集群限流                                                 | 否                            |





## Sentinel 限流

### 1、单机限流

#### 1.1、引入依赖

在上一篇文章中，有提到过 `RateLimiter`  实现的单机限流， 这里介绍一下，使用 `Sentinel` 实现的单机限流

```xml
//项目中引入 sentinel-core 依赖
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-core</artifactId>
    <version>1.8.1</version>
</dependency>
```



#### 1.2、定义限流规则

定义保护规则:

```java
    private static void initFlowRules(){
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule();
        //绑定资源
        rule.setResource("HelloWorld");
        //限流阈值类型
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        //数量级别
        rule.setCount(20);
        //添加到本地内存
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }
```



#### 1.3、定义限流资源

根据上面描述的 `资源`划分， 我们这里主要将 `代码块`  定义为资源。 

```java
public static void main(String[] args) {
    // 配置规则.
    initFlowRules();

    while (true) {
        // 1.5.0 版本开始可以直接利用 try-with-resources 特性，自动 exit entry
        try (Entry entry = SphU.entry("HelloWorld")) {
            // 被保护的逻辑
            System.out.println("hello world");
	} catch (BlockException ex) {
            // 处理被流控的逻辑
	    System.out.println("blocked!");
	}
    }
}
```



#### 1.4、运行结果

> Demo 运行之后，我们可以在日志 `~/logs/csp/${appName}-metrics.log.xxx` 里看到下面的输出:



![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/4f3ecf7c502f4fe6bda1eef03ce7124e~tplv-k3u1fbpfcp-watermark.image)



```
➜  csp cat com-jaycekon-sentinel-demo-FlowRuleDemo-metrics.log.2021-07-03

|--timestamp-|------date time----|-resource-|p |block|s |e|rt
1625294582000|2021-07-03 14:43:02|HelloWorld|20|1720|20|0|2|0|0|0
1625294583000|2021-07-03 14:43:03|HelloWorld|20|5072|20|0|0|0|0|0
1625294584000|2021-07-03 14:43:04|HelloWorld|20|6925|20|0|0|0|0|0
```



- `p` 代表通过的请求
- `block` 代表被阻止的请求
- `s` 代表成功执行完成的请求个数
- `e` 代表用户自定义的异常
- `rt` 代表平均响应时长





<div align=center ><img src="https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/277ebb0c20884448ad67485f3aa389f6~tplv-k3u1fbpfcp-watermark.image"/ width=400></div>



`Sentinel` 的单机限流 ，和 `RateLimiter` 有什么区别呢？



| Field    | 分布式环境下实现难度 | 空间复杂度 | 时间复杂度 | 限制突发流量 | 平滑限流 |
| -------- | -------------------- | ---------- | ---------- | ------------ | -------- |
| 令牌桶   | 高                   | 低O(1)     | 高O(N)     | 是           | 是       |
| 滑动窗口 | 中                   | 高O(N)     | 中O(N)     | 是           | 相对实现 |



附录：[《Sentinel - 滑动窗口实现原理》](https://www.cnblogs.com/dingwpmz/p/12548792.html?utm_source=tuicool&utm_medium=referral)





### 2、控制台限流

#### 2.1、客户端接入控制台

超详细文档，参考：[《Sentinel - 控制台》](https://github.com/alibaba/Sentinel/wiki/%E6%8E%A7%E5%88%B6%E5%8F%B0] )



Sentinel 提供一个轻量级的开源控制台，它提供机器发现以及健康情况管理、监控（单机和集群），规则管理和推送的功能。



下载`Jar 包(21M)`，或者下载`源码(4M)` 后自行进行编译（不建议，编译花的时间比直接下载jar包还要久）

> https://github.com/alibaba/Sentinel/releases



编译后，启动命令

```
java -Dserver.port=8000 -Dcsp.sentinel.dashboard.server=localhost:8000 -Dproject.name=sentinel-dashboard -jar sentinel-dashboard-1.8.1.jar
```



![8608912f0070c8c93df53f6c3cb21952.gif](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/0770889210454b2aa6de4f155bb98d93~tplv-k3u1fbpfcp-watermark.image)



进入控制台

![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/e5547a3b5aa749fda43f02929f7a1b0e~tplv-k3u1fbpfcp-watermark.image)



#### 

#### 2.2、引入依赖

客户端需要引入 `Transport` 模块来与 `Sentinel` 控制台进行通信。您可以通过 `pom.xml` 引入 JAR 包

```xml
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-transport-simple-http</artifactId>
    <version>1.8.1</version>
</dependency>

//重要的依赖，还是提前先写上吧，避免小伙伴找不到了
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-web-servlet</artifactId>
    <version>1.8.1</version>
</dependency>

```



然后！！！ 烦了我一下午的地方来了！！ 在官方文档中，指出了需要引入`对应的依赖配置` , 好家伙，那么重要的话，你如此轻描淡写，脑壳疼啊！！！



<div align=center ><img src="https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/70dd65404acc4a9c91a909a1456284e9~tplv-k3u1fbpfcp-watermark.image"/ width=400></div>





![image.png](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/6b6546ec68f64e728ebf6c63498ec213~tplv-k3u1fbpfcp-watermark.image)



对应的适配依赖有

- [云原生微服务体系](https://github.com/alibaba/Sentinel/wiki/主流框架的适配#云原生微服务体系)
- [Web 适配](https://github.com/alibaba/Sentinel/wiki/主流框架的适配#web-适配)
- [RPC 适配](https://github.com/alibaba/Sentinel/wiki/主流框架的适配#rpc-适配)
- [HTTP client 适配](https://github.com/alibaba/Sentinel/wiki/主流框架的适配#http-client-适配)
- [Reactive 适配](https://github.com/alibaba/Sentinel/wiki/主流框架的适配#reactive-适配)
- [Reactive 适配](https://github.com/alibaba/Sentinel/wiki/主流框架的适配#reactive-适配)
- [Apache RocketMQ](https://github.com/alibaba/Sentinel/wiki/主流框架的适配#apache-rocketmq)



好家伙，基本上所有业务场景都覆盖到了！ 由于我的` Demo` 项目是基于 `SpringBoot` ，然后想看看 云原生微服务体系下的视频，好家伙，要用 `SpringCloud` , 想要了解的，可以参考:  [《Spring-Cloud-Sentinel》](https://github.com/sentinel-group/sentinel-guides/tree/master/sentinel-guide-spring-cloud)



#### 2.3、定义资源

```Java
@SpringBootApplication
@Configuration
@RestController
public class SpringBootSentinelApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootSentinelApplication.class, args);
    }

    @Bean
    public FilterRegistrationBean sentinelFilterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new CommonFilter());
        registration.addUrlPatterns("/*");
        registration.setName("sentinelFilter");
        registration.setOrder(1);

        return registration;
    }
    
    @RequestMapping("/index")
    public String index(){
        return "hello index";
    }
    
}
```



在概述中，我们有提到过，需要被保护的资源，可以是 `一个代码块`，`一个方法`或者`一个接口`。这里通过 `Filter` 的 方式，将所有请求都定义为资源 （`/*`）, 那么我们在请求的过程就会变成这样子：



![image.png](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/5f3a9dbd4a974d4e95e4074db682a5a8~tplv-k3u1fbpfcp-watermark.image)

#### 2.4 运行结果

添加启动参数

> -Dserver.port=8088 -Dcsp.sentinel.dashboard.server=localhost:8080 -Dproject.name=jaycekon-sentinel

参数说明:

- `server.port` : 服务启动端口
- `csp.sentinel.dashboard.server` : 状态上报机器ip:端口
- `project.name` : 监控项目名称

![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/220460ebfad648339e36cdc346b513e6~tplv-k3u1fbpfcp-watermark.image)


运行结果：
![image.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/28457a7f2a2b42d7af029f6376b65f13~tplv-k3u1fbpfcp-watermark.image)




#### 2.5 限流配置


![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/ed59b2e272054d3da2fb238d4ebc69e9~tplv-k3u1fbpfcp-watermark.image)

流控效果

- 1、快速失败：直接失败
- 2、Warm Up：预热模式，根据codeFactory的值（默认3），从阈值/codeFactory，经过预热时长，才达到设置的QPS阈值。比如设置QPS为90，设置预热为10秒，则最初的阈值为90/3=30，经过10秒后才达到90。
- 3、排队等待：比如设置阈值为10，超时时间为500毫秒，当第11个请求到的时候，不会直接报错，而是等待500毫秒，如果之后阈值还是超过10，则才会被限流。




运行结果：

![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/5111c8eebe05434e8761dbb1e47144e4~tplv-k3u1fbpfcp-watermark.image)

![image.png](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/a93f93a946b248ef84cd9502d1863bac~tplv-k3u1fbpfcp-watermark.image)





### 3、集群限流

讲了那么多，终于要到核心的 `集群限流`方案了， 在`秒杀系统`设计中，我们谈到很多场景都是以单机作为具体案例进行分析，如果我们的系统要扩容，那么如何做好`限流方案`。假设集群中有 10 台机器，我们给每台机器设置单机限流阈值为` 10 QPS`，理想情况下整个集群的限流阈值就为` 100 QPS`。不过实际情况下流量到每台机器可能会`不均匀`，会导致总量没有到的情况下某些机器就开始限流。因此仅靠单机维度去限制的话会无法`精确`地限制总体流量。而`集群流控`可以精确地控制整个集群的调用总量，结合`单机限流兜底`，可以更好地发挥流量控制的效果。


介绍一下集群限流的核心角色：

- `Token Client`：集群流控客户端，用于向所属 Token Server 通信请求 token。集群限流服务端会返回给客户端结果，决定是否限流。
- `Token Server`：即集群流控服务端，处理来自 Token Client 的请求，根据配置的集群规则判断是否应该发放 token（是否允许通过）。


在嵌入模式下的结构图：



![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/f133778a23ac497e91b25ec5fd6b160f~tplv-k3u1fbpfcp-watermark.image)

在独立模式下的结构图：

![image.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/313ac2aff35c4edaa61ddb9a16224aeb~tplv-k3u1fbpfcp-watermark.image)


`内嵌模式`，即 发Token 的操作，有其中某一个实例完成，其他 Client 通过向 Server 请求，获取访问许可。

`独立模式`，即作为独立的 token server 进程启动，独立部署，隔离性好，但是需要额外的部署操作。

#### 3.1、阿里云AHAS

在上述示例代码中，使用了本地模式的 `Demo`, 在集群限流的场景，这里用一下 阿里云提供的 `AHAS` 服务。

>控制台地址： https://ahas.console.aliyun.com/index?ns=default&region=public

引入依赖：

```xml
//sentinel ahas 依赖，包括了sentinel的使用依赖
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>ahas-sentinel-client</artifactId>
    <version>1.8.8</version>
</dependency>

```

这里有个要注意的点， `AHAS` 的依赖，包含了 `Sentinel` ，所需要使用到的依赖，包括 `sentinel-core`,`sentinel-web-servlet`和`sentinel-transport-simple-http`。

否则会出现 `Spi` 异常 , 如果对 `Spi` 不太了解，建议加群提问，嘿嘿～

> com.alibaba.csp.sentinel.spi.SpiLoaderException

![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/977aead2bb1d42e8adee217fdb2f5c7b~tplv-k3u1fbpfcp-watermark.image)


![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/073a6b9279ac4defb3d764f5c4ed6d05~tplv-k3u1fbpfcp-watermark.image)


#### 3.2、开启阿里云AHAS 服务

这里有官方的开通文档，我就不赘述了，[文档地址](https://help.aliyun.com/document_detail/90323.html?spm=a2c6h.12873639.0.0.10ba7a4ckxr7Tx)


在应用防护这里找到 `Lincense` ,然后添加启动参数：

> -Dserver.port=8092 -Dproject.name=jaycekon-sentinel -Dahas.license=d1e21b0c8f2e4d87b5ac460b118dc58d -Dcsp.sentinel.log.use.pid=true

![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/21644603eae646029fb7872a042e4c53~tplv-k3u1fbpfcp-watermark.image)

由于我们要本地启动多实例， 因此需要修改服务的多个端口：

```
java -Dserver.port=8090 -Dproject.name=jaycekon-sentinel -Dahas.license=d1e21b0c8f2e4d87b5ac460b118dc58d  -Dcsp.sentinel.log.use.pid=true -jar sentinel-ahas-0.0.1-SNAPSHOT.jar

java -Dserver.port=8091 -Dproject.name=jaycekon-sentinel -Dahas.license=d1e21b0c8f2e4d87b5ac460b118dc58d  -Dcsp.sentinel.log.use.pid=true -jar sentinel-ahas-0.0.1-SNAPSHOT.jar

java -Dserver.port=8092 -Dproject.name=jaycekon-sentinel -Dahas.license=d1e21b0c8f2e4d87b5ac460b118dc58d  -Dcsp.sentinel.log.use.pid=true -jar sentinel-ahas-0.0.1-SNAPSHOT.jar
```


产生访问流量后，可以在大盘看到机器的链接状态：

> http://localhost:8092/index

![image.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/aa6c23391b94445181d4080c43a3855c~tplv-k3u1fbpfcp-watermark.image)





#### 3.3、集群流控规则配置


![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/b7573e2e90634e3396ea848bd6241b50~tplv-k3u1fbpfcp-watermark.image)

这里有个两个概念：

- 集群阀值：指的是，我们集群总体能通过的访问量，可能存在分配不均的情况（能避免单机误限）。
- 退化单机：当 Token Server 访问超时，即无法从远端获取令牌时，回退到单机限流

> 测试限流, 只访问 http://localhost:8092/index

通过手刷（手速过硬～），触碰到限流的临界值，然后整体限流跟我们预期一致。

![image.png](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/da6fd14e8e674060b81e9605c96b683f~tplv-k3u1fbpfcp-watermark.image)



**退化单机**

在集群流控这里，有个 Token 请求超时时间，`Client` 请求 `Server` ，然后返回数据结果。整个流程会有网络请求的耗时，在上面的测试流程中，我将超时时间调大了，每次请求都能拿到Token， 通过修改请求超时时间，触发退化 `单机限流` 。


![image.png](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/cde404ff107e4249bbcd33d0da9bce18~tplv-k3u1fbpfcp-watermark.image)


运行结果：

![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/10caca72cf2745db88c5a721f2b4685a~tplv-k3u1fbpfcp-watermark.image)



#### 3.4、Server 角色转换



在内嵌模式下，通过 HTTP API的方式，将角色转换为 `Server` 或 `client`

```
http://<ip>:<port>/setClusterMode?mode=<xxx>
```

其中 mode 为 `0` 代表 client，`1` 代表 server，`-1` 代表关闭。注意应用端需要引入集群限流客户端或服务端的相应依赖。

在独立模式下，我们可以直接创建对应的 `ClusterTokenServer` 实例并在 main 函数中通过 `start` 方法启动 Token Server。



## Sentinel 熔断



在`秒杀系统` 的案例中，一个完整的链路可能包含了 `下订单`，`支付` 和`物流对接`等多个服务（实际上不止那么少）。在一个完整的链路中，各个系统通过 rpc/http的形式进行交互，在下面的链路图中，如果用户选择的 支付方式，存在`延时过高`，`服务不稳定`,或`服务异常`等情况，会导致整个链路没办法完成。最终的结果就是，用户明明抢到了，但是没办法支付，导致订单丢失。



![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/a751f18f321848db8f57950dd21a12db~tplv-k3u1fbpfcp-watermark.image)



现代微服务架构都是`分布式`的，由非常多的`服务`组成。不同服务之间相互调用，组成复杂的`调用链路`。以上的问题在链路调用中会产生放大的效果。复杂链路上的某一环`不稳定`，就可能会`层层级联`，最终导致`整个链路`都不可用。因此我们需要对不稳定的**弱依赖服务调用**进行熔断降级，暂时切断不稳定调用，`避免局部不稳定因素导致整体的雪崩`。`熔断降级`作为保护自身的手段，通常在`客户端（调用端）`进行配置。



### 1、熔断降级

添加测试代码

```java
    @RequestMapping("/myError")
    public String error(){
        if (true){
            throw new RuntimeException("sentinel run error");
        }
        return "error";
    }
```



在 `Sentinel-Dashboard `中配置降级规则

![image-20210706005136317](/Users/huangweijie/Library/Application Support/typora-user-images/image-20210706005136317.png)



![image-20210706005159415](/Users/huangweijie/Library/Application Support/typora-user-images/image-20210706005159415.png)





降级保护效果：

用户通过访问接口 `/myError` , 出现一次异常后，在接下来的`10秒` ，都会走降级策略，直接返回。能够很好的保护服务端避免异常过多，占用机器资源。同时快速响应用户请求。
![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/60899487c81641e187f3f26a9e05ed1d~tplv-k3u1fbpfcp-watermark.image)



### 2、熔断策略

Sentinel 提供以下几种熔断策略：

- 慢调用比例 (`SLOW_REQUEST_RATIO`)：选择以慢调用比例作为阈值，需要设置允许的慢调用 RT（即最大的响应时间），请求的响应时间大于该值则统计为慢调用。当单位统计时长（`statIntervalMs`）内请求数目大于设置的最小请求数目，并且慢调用的比例大于阈值，则接下来的熔断时长内请求会自动被熔断。经过熔断时长后熔断器会进入探测恢复状态（HALF-OPEN 状态），若接下来的一个请求响应时间小于设置的慢调用 RT 则结束熔断，若大于设置的慢调用 RT 则会再次被熔断。
- 异常比例 (`ERROR_RATIO`)：当单位统计时长（`statIntervalMs`）内请求数目大于设置的最小请求数目，并且异常的比例大于阈值，则接下来的熔断时长内请求会自动被熔断。经过熔断时长后熔断器会进入探测恢复状态（HALF-OPEN 状态），若接下来的一个请求成功完成（没有错误）则结束熔断，否则会再次被熔断。异常比率的阈值范围是 `[0.0, 1.0]`，代表 0% - 100%。
- 异常数 (`ERROR_COUNT`)：当单位统计时长内的异常数目超过阈值之后会自动进行熔断。经过熔断时长后熔断器会进入探测恢复状态（HALF-OPEN 状态），若接下来的一个请求成功完成（没有错误）则结束熔断，否则会再次被熔断。

## 总结

本文主要详细讲解了一下 如何通过 Sentinel 去实际接触 限流和熔断，对于限流的底层实现，后续会有专门的源码分析篇。对于熔断，本文也没有细说个究竟，下一篇文章会给大家带来，熔断是什么，在系统中到底是怎么实际使用，以及常见的熔断策略。



项目源码地址： https://github.com/jaycekon/SpringBoot 
欢迎 `Star` 和 `Fork`

![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/fba66b3a3cbe46c49ad2cdaa7172eff3~tplv-k3u1fbpfcp-watermark.image)



## 点关注，不迷路

好了各位，以上就是这篇文章的全部内容了，我后面会每周都更新几篇高质量的大厂面试和常用技术栈相关的文章。感谢大伙能看到这里，如果这个文章写得还不错，  求三连！！！ 创作不易，感谢各位的支持和认可，我们下篇文章见！

我是 `九灵` ,有需要交流的童鞋可以 加我wx，`Jayce-K`,关注公众号：`Java 补习课`，掌握第一手资料！

如果本篇博客有任何错误，请批评指教，不胜感激 ！



