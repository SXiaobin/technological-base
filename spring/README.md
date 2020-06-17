# Spring Boot 基础

## Spring Boot 工程结构解析

```cmd
-.mvn
--wrapper
---maven-wrapper.jar
---maven-wrapper.properties
---MavenWrapperDownloader.java

-mvnw

-mvnw.cmd
```

在 maven 版本和其插件版本不兼容的情况下，用以切换 maven 版本以解决兼容性问题。Maven Wrapper 非 maven 官方产品，所以如需使用，需要另外安装。一般项目中如果不使用，可以直接删除。



```java
// ./src/main/java/.../Application.java
@SpringBootApplication
public class Application {

    public static void main(String... args) {
        new SpringApplicationBuilder()
                .bannerMode(Banner.Mode.CONSOLE)
                .sources(Application.class)
                .run(args);
    }
}
```

@SpringBootApplication 为组合注解：@SpringBootApplication -> @SpringBootConfiguration -> @Configuration

所以通过 @SpringBootApplication 我们将 Application 标记为了一个 JavaConfig 即可以充当 Spring 配置文件。



```properties
# ./src/main/resources/application.properties

# Spring Settings
spring.jersey.type=filter
spring.application.name=is-psipenta
spring.datasource.username=is_psipenta
spring.datasource.password=ep0rKlPMOWPJEfjaVj/JwQ==
# Database Settings
flyway.enabled=true
flyway.locations=classpath:db/migration/{vendor}
flyway.baseline-on-migrate=true
flyway.baseline-version=1
flyway.table=IS_PSIPENTA_VERSION
flyway.placeholders.table.name=OPERATION_CALL_EVENT
flyway.placeholders.table.schema=OPERATION_CALL_EVENT
```

Spring Boot 配置文件。



```cmd
./target/is-psipenta.jar
./target/is-psipenta.jar-original
```

maven 打包时将项目产生的原始 jar 包（重命名为了 is-psipenta.jar-original）进行了结构重组、重新封装，生成了./target/is-psipenta.jar 其中包括了 spring boot 类、Tomcat、引用的第三方类库等各种资源。

其来源源自于pom.xml（或其 parent pom）中引入的maven 插件：

```xml
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
        <version>2.0.5.RELEASE</version>
        <executions>
          <execution>
            <goals>
              <goal>repackage</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
```



# How-Tos

## How to execute method on startup in Spring

@PostConstruct & @EventListener(ApplicationReadyEvent.class)

## How to use Annotation @ImportResource in Spring

标签 @ImportResource 可以加载资源文件，并根据资源文件的配置，使用资源文件中的参数构造 Spring Bean。通过这种方式，我们可以将某个Spring Component 具体实例化几个对象以及每个对象初始参数为何的权柄交由具体项目自有配置。

# Troubleshooting

## ${problem description}

### Cause
### Solution
# Authors

* **Su, Xiaobin** *- Developer*

