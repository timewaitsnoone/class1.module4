package com.lagou.boot;

import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;

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
