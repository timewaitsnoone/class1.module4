# 约定优于配置设计范式及Spring Boot源码剖析

#### 作业要求：

自定义简易版SpringBoot，实现SpringBoot MVC及内嵌Tomcat启动、DispatcherServlet注册和组件扫描功能。

- 程序通过main方法启动，可以自动启动tomcat服务器

- 可以自动创建和加载DispatcherServlet组件到ServletContext中

- 可以自动通过@ComponentScan扫描Controller等组件

- Controller组件可以处理浏览器请求，返回响应结果  

  #### 作业实现过程：

  （1）创建maven工程，导入以下依赖：

  ```
  <properties>
         <java.version>1.8</java.version>
     </properties>
  
     <dependencies>
         <dependency>
             <groupId>org.springframework</groupId>
             <artifactId>spring-web</artifactId>
             <version>5.0.8.RELEASE</version>
         </dependency>
         <dependency>
             <groupId>org.apache.tomcat.embed</groupId>
             <artifactId>tomcat-embed-core</artifactId>
             <version>8.5.32</version>
         </dependency>
         <dependency>
             <groupId>org.springframework</groupId>
             <artifactId>spring-context</artifactId>
             <version>5.0.8.RELEASE</version>
         </dependency>
         <dependency>
             <groupId>org.springframework</groupId>
             <artifactId>spring-webmvc</artifactId>
             <version>5.0.8.RELEASE</version>
         </dependency>
     </dependencies>
  
     <build>
         <plugins>
             <plugin>
                 <groupId>org.springframework.boot</groupId>
                 <artifactId>spring-boot-maven-plugin</artifactId>
             </plugin>
         </plugins>
     </build>
  ```

  

  （2）创建SpringApplication 类，编写run方法（方法中要求完成tomcat的创建及启动）  

  ```
  public class SpringApplication {
  
      public static void run() {
          try {
              // 创建Tomcat服务器
              Tomcat tomcat = new Tomcat();
              // 指定端口号
              tomcat.setPort(8080);
              // 指定静态资源路径
              // 必须指定资源路径，否则Tomcat无法扫描ServletContainerInitializer
              StandardContext ctx = (StandardContext) tomcat.addWebapp("/",new File("src/main").getAbsolutePath());
              // 禁止重新载入
              ctx.setReloadable(false);
              // class文件地址
              File file = new File("target/classes");
              // 创建webRoot
              WebResourceRoot resource = new StandardRoot(ctx);
              // Tomcat内部读取class执行
              resource.addJarResources(new DirResourceSet(resource,"/WEB-INF/classes",file.getAbsolutePath(),"/"));
              // 启动Tomcat
              tomcat.start();
              System.out.println("Tomcat服务启动成功...");
              // 异步接收请求
              tomcat.getServer().await();
          } catch (Exception e) {
              e.printStackTrace();
          }
  
      }
  
  }
  ```

  （3）创建spring的配置类 AppConfig该类上要通过@ComponentScan来进行包扫描  

  ```
  @Configuration
  @ComponentScan("com.lagou")
  public class AppConfig {
  }
  ```

  （4）创建MyWebApplicationInitializer实现WebApplicationInitializer接口，重写onstartup方法（WebApplicationInitializer实现web.xml的配置）

  ```
  
  /**
   * 自定义初始化器
   * 实现Web，xml配置
   * 完成AppConfig的注册，并基于java代码的方式初始化DispatcherServlet
   */
  public class MyWebApplicationInitializer implements WebApplicationInitializer {
      @Override
      public void onStartup(ServletContext servletContext) {
  
          // Load Spring web application configuration
          //通过注解的方式初始化Spring的上下文
          AnnotationConfigWebApplicationContext ac = new AnnotationConfigWebApplicationContext();
          //注册spring的配置类（替代传统项目中xml的configuration）
          ac.register(AppConfig.class);
          ac.refresh();
  
          // Create and register the DispatcherServlet
          //基于java代码的方式初始化DispatcherServlet
          DispatcherServlet servlet = new DispatcherServlet(ac);
  
          ServletRegistration.Dynamic registration = servletContext.addServlet("app", servlet);
          registration.setLoadOnStartup(1);
          registration.addMapping("/app/*");
  
          System.out.println("MyWebApplicationInitializer...  finished");
      }
  
  }
  ```

  （5）创建MySpringServletContainerInitializer，实现ServletContainerInitializer接口，重写onstartup方法，方法中调用第4步中MyWebApplicationInitializer的onstartup方法（参考SpringServletContainerInitializer源码）

  ```
  @HandlesTypes(WebApplicationInitializer.class)
  public class MySpringServletContainerInitializer implements ServletContainerInitializer {
  
      @Override
      public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {
          // 拿到所有的WebApplicationInitializer，并调用onStartup方法
          for (Class<?> aClass : set) {
              try {
                  if (MyWebApplicationInitializer.class.equals(aClass)) {
                      WebApplicationInitializer initializer = (WebApplicationInitializer)aClass.newInstance();
                      initializer.onStartup(servletContext);
                  }
              } catch (Exception e) {
                  e.printStackTrace();
                  continue;
              }
          }
  
          System.out.println("MySpringServletContainerInitializer...  finished");
      }
  }
  ```

  （6）创建文件：META-INF/services/javax.servlet.ServletContainerInitializer，在该文件中配置ServletContainerInitializer的实现类MySpringServletContainerInitializer  
  `com.lagou.config.MySpringServletContainerInitializer`

  （7）编写一个Controller测试类及目标方法，响应输出“hello”即可  

  ```
  @RestController
  public class TestController {
  
      @RequestMapping("/test")
      public String test () {
          return "hello";
      }
  
  }
  ```

  （8）编写一个启动类MyRunBoot，通过执行main方法启动服务

  ```
  public class MyRunBoot {
  
      public static void main(String[] args) {
          SpringApplication.run();
      }
  }
  ```

  （9）通过浏览器对目标方法进行方法

  ![image-20210516175706964](C:\Users\11159\AppData\Roaming\Typora\typora-user-images\image-20210516175706964.png) 

  ###   疑问：

  自定义的 MySpringServletContainerInitializer 和Spring提供的实现SpringServletContainerInitializer都会加载，如何避免？

  

