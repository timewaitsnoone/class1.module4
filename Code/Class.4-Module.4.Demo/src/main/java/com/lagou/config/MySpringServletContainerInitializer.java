package com.lagou.config;

import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import java.util.Set;

/**
 * servlet容器初始化器
 * 调用MyWebApplicationInitializer的onstartup方法
 * Servlet 3.0+容器启动时将自动扫描类路径
 * 查找实现Spring的Webapplicationinitializer接口的所有实现
 * 将其放进一个Set集合中，提供给ServletContainerInitializer中onStartup方法的第一个参数。
 */

//HandlesTypes注解的作用是将注解指定的Class对象作为参数传递到onStartup（）方法中
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
