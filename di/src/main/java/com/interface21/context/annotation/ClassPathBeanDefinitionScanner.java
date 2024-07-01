package com.interface21.context.annotation;

import com.interface21.beans.factory.support.BeanDefinitionRegistry;
import com.interface21.beans.factory.support.GenericBeanDefinition;
import com.interface21.context.stereotype.Component;
import com.interface21.context.stereotype.Controller;
import com.interface21.context.stereotype.Repository;
import com.interface21.context.stereotype.Service;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

public class ClassPathBeanDefinitionScanner {

    private final BeanDefinitionRegistry beanDefinitionRegistry;

    public ClassPathBeanDefinitionScanner(final BeanDefinitionRegistry beanDefinitionRegistry) {
        this.beanDefinitionRegistry = beanDefinitionRegistry;
    }

    @SuppressWarnings("unchecked")
    public void doScan(Object... basePackages) {
        Reflections reflections = new Reflections(basePackages);
        Set<Class<?>> beanClasses = getTypesAnnotatedWith(reflections, Controller.class, Service.class,
            Repository.class, Component.class);
        for (Class<?> clazz : beanClasses) {
            beanDefinitionRegistry.registerBeanDefinition(clazz, new GenericBeanDefinition(clazz));
        }
    }

    @SuppressWarnings("unchecked")
    private Set<Class<?>> getTypesAnnotatedWith(Reflections reflections, Class<? extends Annotation>... annotations) {
        Set<Class<?>> preInstantiatedBeans = new HashSet<>();
        for (Class<? extends Annotation> annotation : annotations) {
            preInstantiatedBeans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return preInstantiatedBeans;
    }
}
