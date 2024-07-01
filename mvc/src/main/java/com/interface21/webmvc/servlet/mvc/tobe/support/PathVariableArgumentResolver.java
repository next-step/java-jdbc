package com.interface21.webmvc.servlet.mvc.tobe.support;

import com.interface21.core.MethodParameter;
import com.interface21.web.bind.annotation.PathVariable;
import com.interface21.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

public class PathVariableArgumentResolver extends AbstractAnnotationArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return supportAnnotation(methodParameter, PathVariable.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, HttpServletRequest request, HttpServletResponse response) {
        PathVariable pathVariable = getAnnotation(methodParameter, PathVariable.class);
        String pattern = getPattern(methodParameter);
        String key = getPathVariableKey(pathVariable, methodParameter.getParameterName());
        return PathPatternUtil.getUriValue(pattern, request.getRequestURI(), key);
    }

    private String getPathVariableKey(PathVariable pathVariable, String parameterName) {
        return StringUtils.isNotBlank(pathVariable.name()) ? pathVariable.name()
                : StringUtils.isNotBlank(pathVariable.value()) ? pathVariable.value()
                : parameterName;
    }

    private String getPattern(MethodParameter methodParameter) {
        Method method = methodParameter.getMethod();
        if (method.isAnnotationPresent(RequestMapping.class)) {
            final RequestMapping annotation = method.getAnnotation(RequestMapping.class);
            return annotation.value();
        }

        throw new IllegalStateException(method.getName() + " doesn't have RequestMapping annotation");
    }
}
