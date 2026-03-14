package com.gwc.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class UserLoginInterceptor implements HandlerInterceptor {
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //完成后,清理掉id
        UserContext.clear();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //拿到token
        String token = request.getHeader("Authorization");
        //token净化
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            //解析token
            if (JwtUtils.validateToken(token)) {
                UserContext.setUserId(JwtUtils.getUserId(token));
                return true;
            }
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"message\":\"未登录或token失效\"}");
        return false;
    }
}
