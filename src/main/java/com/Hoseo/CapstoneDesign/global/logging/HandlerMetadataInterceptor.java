package com.Hoseo.CapstoneDesign.global.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.Hoseo.CapstoneDesign.global.logging.support.LoggingConstants.HANDLER_CLASS_NAME;
import static com.Hoseo.CapstoneDesign.global.logging.support.LoggingConstants.HANDLER_METHOD_NAME;

@Component
public class HandlerMetadataInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod handlerMethod) {
            request.setAttribute(HANDLER_CLASS_NAME, handlerMethod.getBeanType().getSimpleName());
            request.setAttribute(HANDLER_METHOD_NAME, handlerMethod.getMethod().getName());
        }
        return true;
    }
}