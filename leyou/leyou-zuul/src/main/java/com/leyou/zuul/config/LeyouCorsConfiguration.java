package com.leyou.zuul.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class LeyouCorsConfiguration {

    @Bean
    public CorsFilter corsFilter(){

        //初始化 cors配置对象
        CorsConfiguration configuration = new CorsConfiguration();
        //允许跨域的域名，如果要携带cookie 就无法写 * 。 * ：代表所有域名都能跨域访问
        configuration.addAllowedOrigin("http://manage.leyou.com");
        configuration.addAllowedOrigin("http://www.leyou.com");
        configuration.setAllowCredentials(true);//允许携带cookie
        configuration.addAllowedMethod("*");//代表所有的请求方法 ： get put post delete。。
        configuration.addAllowedHeader("*");//允许携带任何头头信息

        //初始化 cors配置源对象
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**",configuration);

        //返回corsFilter实例， 参数：cors配置源对象
        return  new CorsFilter(configurationSource);
    }

}
