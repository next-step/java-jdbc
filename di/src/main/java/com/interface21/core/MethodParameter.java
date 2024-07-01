package com.interface21.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class MethodParameter {

    private final Method method;
    private final Class<?> type;
    private final Annotation[] annotations;
    private final String parameterName;

    public MethodParameter(Method method, Class<?> parameterType, Annotation[] parameterAnnotation, String parameterName) {
        this.method = method;
        this.type = parameterType;
        this.annotations = parameterAnnotation;
        this.parameterName = parameterName;
    }

    public Method getMethod() {
        return method;
    }

    public Class<?> getType() {
        return type;
    }

    public Annotation[] getAnnotations() {
        return annotations;
    }

    public boolean hasAnnotation() {
        return getAnnotations().length > 0;
    }

    public boolean isString() {
        return type == String.class;
    }

    public boolean isInteger() {
        return type == int.class || type == Integer.class;
    }

    public String getParameterName() {
        return parameterName;
    }
}
