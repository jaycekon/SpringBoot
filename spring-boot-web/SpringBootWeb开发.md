# 构建项目
从技术角度来看，我们要用Spring MVC来处理Web请求，用Thymeleaf来定义Web视图，用
Spring Data JPA来把阅读列表持久化到数据库里，姑且先用嵌入式的H2数据库。

## 1、项目搭建

### Spring IO 官网搭建

我们可以进入到Spring 的官网：http://start.spring.io/
进入官网后，可以快速的构建Spring boot 的基础项目，这里可以选择Maven 项目或者Gradle 项目，然后设置项目相关的配置。

在选择Generate Project 进行项目下载后，会生成对应的zip 文件。后续只需要将Zip 文件解压，添加到IDE 中即可。
![image](E:/电子书/图片/test.png)


### IDEA 快速构建
除了在SpringIO 官网进行项目初始化外，还可以通过IDEA 进行项目的搭建。如下图所示，项目的搭建也是引用了 http://start.spring.io/ 

在后续的页面中，我们可以设置相关的配置信息，一些常用的依赖，也可以进行初始化。

![image](E:/电子书/图片/1O.png)
![image](E:/电子书/图片/2121.png)




### Spring Boot CLI

除了以上常用的项目创建方法以外，我们还可以通过CLI 进行项目的创建：

```
spring init -dweb,data-jpa,h2,thymeleaf --build gradle readinglist
```

CLI的init命令是不能指定项目根包名和项目名的。包名默认是demo，项目名默认是Demo。



## 2、目录结构

不管我们采用哪种方式进行项目的创建，在将项目导入IDE之后，我们可以看到整个项目结构遵循传统Maven或Gradle项目的布局，即主要应用程序代码位于src/main/java目录里，资源都在src/main/resources目录里，测试代码则在src/test/java目录里。此刻还没有测试资源，但如果有的话，要放在src/test/resources里。

![image](E:/电子书/图片/321.png)

<br>

### 文件介绍：
- **SpringBootWebApplication**: 应用程序的启动引导类(bootstrap class)，也是主要的Spring 配置类。
- **appliction.properties**：用于配置应用程序和Spring boot 的属性
- **SpringBootWebApplicationTests**：一个基本的集成测试类。
- **pom.xml**：项目依赖文件


## 3、文件介绍

### SpringBootWebApplication
  **Application** 类在Spring boot应用程序中有两个作用：配置和启动引导。
  
  
```
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication --开启组件扫描和自动配置
public class SpringBootWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebApplication.class, args); -- 负责启动引导应用程序
	}
}
```
我们在使用Spring boot 进行开发时，Application 类是我们启动服务的入口，起到关键作用的是 **@SpringBootApplication** 这一注解，实际上 @SpringBootApplication 包含了三个有用的注解：

- @Configuration：标明该类使用Spring 基于Java 的配置。
- @ComponentScan：启用组件扫描，这样你写的Web控制器类和其他组件才能被自动发现并注册为Spring 应用程序上下文中的Bean。
- @EnableAutoConfiguration：这一个配置开启了Spring boot 的自动配置。
  

这里使用到main 方法是需要提供一个@EnableAutoConfiguration 注解的引导类，来引导整个应用程序的启动。
  


### SpringBootWebApplicationTests
项目创建时问我们创建了一个带有上下文的测试类。

```
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest -- 通过SpringBoot 加载上下文
public class SpringBootWebApplicationTests {

	@Test
	public void contextLoads() {
	 -- 测试加载的上下文
	}

}
```

### application.properties
实际上，这个文件是可选的，你可以删掉它而不影响应用程序的运行。
我们可以通过向application.properties 中添加变量，从而改变程序的默认配置。例如：

```
server.port=8000
server.contextPath=SpringBootWeb
```

在上述代码中，我们将程序的默认端口（8080） 修改成为使用 8000 端口，并且将应用程序的项目名修改为 SpringBootWeb。

原访问地址：
http://127.0.0.1:8080/

修改后：
http://127.0.0.1:8000/SpringBootWeb/


除此之外 还可以配置多环境的变量设置等一系列的设置：

```
spring.profiles.active = dev
```

      

### pom.xml
在代码清单中，我们引用了 spring-boot-starter-parent 作为上一级，这样一来就能利用到Maven 的依赖管理功能，集成很多常用库的依赖，并且不需要知道版本。除此之外，也使用到了开篇所提到过的起步依赖，我们只需要引入 spring-boot-starter-web 这一依赖，就可以使用到Web 中常用的包。
 
```
<parent>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-parent</artifactId>
	<version>1.5.7.RELEASE</version>
	<relativePath/> <!-- lookup parent from repository -->
</parent>
	
	
	<dependencies>
	    <dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		...
	</dependencies>
```


如下图所示，我们使用到的 spring-boot-starter-web 依赖中，已经集成了常用的mvc json 等相关依赖。
```
org.springframework.boot:spring-boot-starter-web:jar:1.5.7.RELEASE:compile
[INFO] |  +- org.springframework.boot:spring-boot-starter-tomcat:jar:1.5.7.RELEASE:compile
[INFO] |  |  +- org.apache.tomcat.embed:tomcat-embed-core:jar:8.5.20:compile
[INFO] |  |  +- org.apache.tomcat.embed:tomcat-embed-el:jar:8.5.20:compile
[INFO] |  |  \- org.apache.tomcat.embed:tomcat-embed-websocket:jar:8.5.20:compile
[INFO] |  +- org.hibernate:hibernate-validator:jar:5.3.5.Final:compile
[INFO] |  |  +- javax.validation:validation-api:jar:1.1.0.Final:compile
[INFO] |  |  \- com.fasterxml:classmate:jar:1.3.4:compile
[INFO] |  +- com.fasterxml.jackson.core:jackson-databind:jar:2.8.10:compile
[INFO] |  |  +- com.fasterxml.jackson.core:jackson-annotations:jar:2.8.0:compile
[INFO] |  |  \- com.fasterxml.jackson.core:jackson-core:jar:2.8.10:compile
[INFO] |  +- org.springframework:spring-web:jar:4.3.11.RELEASE:compile
[INFO] |  \- org.springframework:spring-webmvc:jar:4.3.11.RELEASE:compile
[INFO] |     \- org.springframework:spring-expression:jar:4.3.11.RELEASE:compile
```


## 4、开发功能
### 4.1 定义实体类 Book

如你所见，Book类就是简单的Java对象，其中有些描述书的属性，还有必要的访问方法。
@Entity注解表明它是一个JPA实体，id属性加了@Id和@GeneratedValue注解，说明这个字段
是实体的唯一标识，并且这个字段的值是自动生成的。
```
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by weijie_huang on 2017/9/20.
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String reader;
    private String isbn;
    private String title;
    private String author;
    private String description;
}
```

### 4.2 定义仓库接口 ReadRepository

通过扩展JpaRepository，ReadingListRepository直接继承了18个执行常用持久化操作
的方法。JpaRepository是个泛型接口，有两个参数：仓库操作的领域对象类型，及其ID属性的
类型。此外，我还增加了一个findByReader()方法，可以根据读者的用户名来查找阅读列表。
```
import Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by weijie_huang on 2017/9/20.
 */
public interface ReadRepository extends JpaRepository<Book,Long> {
    List<Book> findByReader(String reader);
}
```

### 4.3 定义控制层 ReadController

在定义好了应用程序的实体类，持久化接口后。我们还需要创建一个MVC 控制器来处理HTTP请求。

```
import ReadRepository;
import Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Created by weijie_huang on 2017/9/20.
 */
@Controller
public class ReadController {
    @Autowired
    private ReadRepository readRepository;


    @RequestMapping(value="/{reader}", method= RequestMethod.GET)
    public String readersBooks(
            @PathVariable("reader") String reader,
            Model model) {
        List<Book> readingList =
                readRepository.findByReader(reader);
        if (readingList != null) {
            model.addAttribute("books", readingList);
        }
        return "readingList";
    }

    @RequestMapping(value="/{reader}", method=RequestMethod.POST)
    public String addToReadingList(
            @PathVariable("reader") String reader, Book book) {
        book.setReader(reader);
        readRepository.save(book);
        return "redirect:/{reader}";
    }

}
```


使用了@Controller注解，这样组件扫描会自动将其注册为
Spring应用程序上下文里的一个Bean。通过@Autowired 将仓库接口注入到控制类中。


### 4.4 启动服务

在开发完成后，我们去到Application 类下，启动main 方法。即可将应用程序启动，然后进入到下述页面（html 文件不细述，可通过查看源码进行了解）。可以看到，我们的服务已经成功启动。
![image](E:/电子书/图片/333.png)

![image](E:/电子书/图片/456.png)

