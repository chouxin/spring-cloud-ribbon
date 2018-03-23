# Spring Cloud（二） Ribbon

**Spring Cloud Ribbon**[Ribbon参考及原理](https://blog.csdn.net/Jeson0725/article/details/70058910)

 - **环境及工具**
 - **maven 3.5.3**
 - **jdk1.8**
 - **Intellij Idea**
 - **spring boot 2.0**

使用Idea创建项目
 - **pom.xml文件** 
```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId>cn.qx</groupId>
	<artifactId>spring-cloud-ribbon</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<packaging>jar</packaging>

	<name>spring-cloud-ribbon</name>
	<description>Demo project for Spring Boot</description>

	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.0.0.RELEASE</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>

	<properties>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
		<java.version>1.8</java.version>
		<spring-cloud.version>Finchley.M8</spring-cloud.version>
	</properties>

	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-openfeign</artifactId>
		</dependency>

		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>

	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-dependencies</artifactId>
				<version>${spring-cloud.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

	<repositories>
		<repository>
			<id>spring-milestones</id>
			<name>Spring Milestones</name>
			<url>https://repo.spring.io/milestone</url>
			<snapshots>
				<enabled>false</enabled>
			</snapshots>
		</repository>
	</repositories>
</project>

```

 - **application.yml**

```
spring:
  application:
    name: ribbon-test
server:
  port: 9006

ribbon:
  # Max number of retries on the same server (excluding the first try)
  MaxAutoRetries: 1
  # Max number of next servers to retry (excluding the first server)
  MaxAutoRetriesNextServer: 1
  # Whether all operations can be retried for this client
  OkToRetryOnAllOperations: true
  # Interval to refresh the server list from the source
  ServerListRefreshInterval: 2000
  # Connect timeout used by Apache HttpClient
  ConnectTimeout: 3000
  # Read timeout used by Apache HttpClient
  ReadTimeout: 3000
  # Initial list of servers, can be changed via Archaius dynamic property at runtime
  #listOfServers: www.microsoft.com:80,www.yahoo.com:80,www.google.com:80
  #EnablePrimeConnections: true
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8001/eureka/
```

 - 主要项目结构
 ![Ribbon-Test项目结构](https://img-blog.csdn.net/20180323142907918?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Nob3UzNDIxNzU4Njc=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
 - 关键代码块
 - SpringCloudRibbonApplication
 

```
@EnableDiscoveryClient
@SpringBootApplication
public class SpringCloudRibbonApplication {
	/**
	 * @see
	 * RestTemplate实例化
	 * @Bean注入到spring容器，
	 * 通过@LoadBalanced开启均衡负载能力
	 * @return restTemplate
	 */
	@Bean
	@LoadBalanced
	public static RestTemplate restTemplate(){
		return new RestTemplate();
	}


	public static void main(String[] args) {
		SpringApplication.run(SpringCloudRibbonApplication.class, args);
	}
}
```

 - **RibboController 为web端提供接口**
 

```
@RestController
public class RibbonController {
    @Autowired
    RibbonService ribbonService;
    @RequestMapping("/ribbon/{id}")
    public User findUser(@PathVariable Long id){
        System.out.println("----------------------------------进入ribbon------findUser---------------------------------------------");
        return ribbonService.findUserById(id);
    }

    @GetMapping("/ribbon/{id}/{name}")
    public User findUserByIdAndName(@PathVariable Long id,@PathVariable String name){
        System.out.println("----------------------------------进入ribbon---findUserByIdAndName---------------------------------------");
        return ribbonService.findUserByIdAndName(id,name);
    }
}
```
 - RibbonService 
 - **注意：** http://demo-clent/  这里的demo-client为服务提供端的spring.application. name = demo-clent;必须用服务名开头，后面跟上接口名以及参数;***否则会报：java.lang.IllegalStateException: No instances available for ********异常***

```
@Service
public class RibbonService {
    @Autowired
    RestTemplate restTemplate;
    /**
     * @see findUserById 单个参数
     * @param id
     * @return
     */
    public User findUserById(Long id){
        return restTemplate.getForObject("http://demo-clent/"+ id ,User.class);
    }
    /**
     * @see findUserByIdAndName 多个参数,getForObject可以换位其它的方法，比如postForObject
     * @param id
     * @param name
     * @return
     */
    public User findUserByIdAndName(Long id,String name){
        return restTemplate.getForObject("http://demo-clent/findUserByIdAndName/"+ id + "/" + name ,User.class);
    }
}
```

 1. **接下来是服务提供者相关内容：**
 2. 服务提供者的名称为： spring.application. name = demo-clent，可以多个，ribbon提供了均衡负载，直接访问即可
 3. 都注册到同一个注册中心Eureka-Server上
 4. 服务提供者项目结构图

![demo-client项目结构图](https://img-blog.csdn.net/20180323144454949?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Nob3UzNDIxNzU4Njc=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

 - 主要代码RibbonController

```
@RestController
public class RibbonController {

    @GetMapping("{id}")
    public User findUserById(@PathVariable Long id ){
        User u = new User();
        u.setId(id);
        u.setAge(100);
        u.setUsername("demo-client1--长寿人");
        return u;
    }
    @GetMapping("/findUserByIdAndName/{id}/{name}")
    public User findUserByIdAndName(@PathVariable Long id, @PathVariable String  name ){
        User u = new User();
        u.setId(id);
        u.setAge(110);
        u.setUsername("demo-client1--不死人： "+ name);
        return u;
    }
}
```
***测试Ribbon***

浏览器输入 

http://localhost:9006/ribbon/25/小明
前面加了@LoadBalanced  注解，ribbon会开启均衡负载；结果如下

![访问的到demo-client1服务提供者](https://img-blog.csdn.net/20180323145439128?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Nob3UzNDIxNzU4Njc=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

![访问到demo-client2服务提供者](https://img-blog.csdn.net/20180323145508650?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Nob3UzNDIxNzU4Njc=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

到此完成ribbon的访问测试代码；

[GitHub完整ribbon代码示例](https://github.com/chouxin/spring-cloud-ribbon)
[GitHub服务提供者代码示例](https://github.com/chouxin/spring-cloud-dashboard-trubine)

 - 遇到的问题：
 - 问题：java.lang.IllegalStateException: No instances available for *异常*
 - 解决：  http://demo-clent/ 这里的demo-client为服务提供端的spring.application. name = demo-clent;必须用服务名开头，后面跟上接口名以及参数;否则会报上述一次


