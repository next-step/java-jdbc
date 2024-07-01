package com.interface21.webmvc.servlet.mvc.tobe.support;

import com.interface21.core.MethodParameter;
import com.interface21.web.method.support.HandlerMethodArgumentResolver;

import java.util.Arrays;

public abstract class AbstractAnnotationArgumentResolver implements HandlerMethodArgumentResolver {

    protected boolean supportAnnotation(MethodParameter methodParameter, Class<?> annotation) {
        return Arrays.stream(methodParameter.getAnnotations())
                .anyMatch(ann -> ann.annotationType() == annotation);
    }

    protected <T> T getAnnotation(MethodParameter methodParameter, Class<T> annotationClazz) {
        return Arrays.stream(methodParameter.getAnnotations())
                .filter(ann -> ann.annotationType() == annotationClazz)
                .findAny()
                .map(annotationClazz::cast)
                .orElseThrow(IllegalArgumentException::new);
    }
}
