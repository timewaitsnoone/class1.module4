package com.lagou.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring配置类
 * 通过@ComponentScan来进行包扫描
 */
@Configuration
@ComponentScan("com.lagou")
public class AppConfig {
}
