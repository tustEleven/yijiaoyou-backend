package com.zhaoyi.yijiaoyou.Interceptor;


import com.zhaoyi.yijiaoyou.common.BaseContext;
import com.zhaoyi.yijiaoyou.common.ErrorCode;
import com.zhaoyi.yijiaoyou.exception.BusinessException;
import com.zhaoyi.yijiaoyou.utils.JwtUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    private static final String signKey = "zhaoyi";
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String jwt = request.getHeader("token");
        try{
            Map<String, Object> claims = JwtUtils.parseJwt(jwt,signKey);
            Long id = Long.valueOf(claims.get("id").toString());
            BaseContext.setCurrentId(id);
            return true;
        }catch (Exception e){
            throw new BusinessException(ErrorCode.LOGIN_VERIFICATION_FAILED);
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
