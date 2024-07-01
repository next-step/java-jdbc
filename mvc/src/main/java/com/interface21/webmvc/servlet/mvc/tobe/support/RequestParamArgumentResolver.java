package com.interface21.webmvc.servlet.mvc.tobe.support;

import com.interface21.core.MethodParameter;
import com.interface21.web.bind.annotation.RequestParam;
import com.interface21.webmvc.servlet.mvc.MethodArgumentTypeNotSupportedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

public class RequestParamArgumentResolver extends AbstractAnnotationArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return supportAnnotation(methodParameter, RequestParam.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, HttpServletRequest request, HttpServletResponse response) {
        RequestParam requestParam = getAnnotation(methodParameter, RequestParam.class);
        String key = getRequestParamKey(requestParam, methodParameter.getParameterName());

        Object arg = request.getParameter(key);

        if (methodParameter.isString()) {
            return arg;
        } else if (methodParameter.isInteger()) {
            return Integer.valueOf(arg.toString());
        }

        throw new MethodArgumentTypeNotSupportedException(methodParameter.getType(), arg);
    }

    private String getRequestParamKey(RequestParam requestParam, String parameterName) {
        return StringUtils.isNotBlank(requestParam.name()) ? requestParam.name()
                : StringUtils.isNotBlank(requestParam.value()) ? requestParam.value()
                : parameterName;
    }

}
