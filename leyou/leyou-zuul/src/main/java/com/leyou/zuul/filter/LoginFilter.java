package com.leyou.zuul.filter;

import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.zuul.config.FilterProperties;
import com.leyou.zuul.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class LoginFilter extends ZuulFilter {

    @Autowired
    JwtProperties jwtProperties;

    @Autowired
    FilterProperties filterProperties;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 10;
    }

    @Override
    public boolean shouldFilter() {
        //获取白名单
        List<String> allowPaths = filterProperties.getAllowPaths();

        //初始化运行上下文
        RequestContext context = RequestContext.getCurrentContext();
        //获取request对象
        HttpServletRequest request = context.getRequest();
        //获取请求路径
        String url = request.getRequestURL().toString();

        for (String allowPath : allowPaths) {
            if (StringUtils.contains(url, allowPath)) {
                return false;
            }

        }
        return true;
    }

    @Override
    public Object run() throws ZuulException {


        //初始化运行上下文
        RequestContext context = RequestContext.getCurrentContext();
        //获取request对象
        HttpServletRequest request = context.getRequest();

        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());

        if (StringUtils.isBlank(token)) {
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }

        try {
            JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }
        return null;
    }
}
