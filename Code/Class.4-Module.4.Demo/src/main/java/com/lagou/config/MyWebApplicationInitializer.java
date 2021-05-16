package com.lagou.config;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

/**
 * 自定义初始化器
 * 实现Web，xml配置
 * 完成AppConfig的注册，并基于java代码的方式初始化DispatcherServlet
 */
public class MyWebApplicationInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

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
        if (registration != null) {

            registration.setLoadOnStartup(1);
            registration.addMapping("/app/*");
        }


        System.out.println("MyWebApplicationInitializer...  finished");
    }

}

