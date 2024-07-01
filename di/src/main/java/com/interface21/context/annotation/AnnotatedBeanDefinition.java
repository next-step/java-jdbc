package com.interface21.context.annotation;

import com.interface21.beans.factory.support.GenericBeanDefinition;

import java.lang.reflect.Method;

public class AnnotatedBeanDefinition extends GenericBeanDefinition {

    private final Method method;

    public AnnotatedBeanDefinition(Class<?> clazz, Method method) {
        super(clazz);
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }
}
