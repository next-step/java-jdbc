package com.interface21.context.annotation;

import com.interface21.beans.factory.support.BeanDefinitionReader;
import com.interface21.beans.factory.support.BeanDefinitionRegistry;
import com.interface21.beans.factory.support.BeanFactoryUtils;
import com.interface21.beans.factory.support.GenericBeanDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Set;

public class AnnotatedBeanDefinitionReader implements BeanDefinitionReader {

    private static final Logger log = LoggerFactory.getLogger(AnnotatedBeanDefinitionReader.class);

    private final BeanDefinitionRegistry beanDefinitionRegistry;

    public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry beanDefinitionRegistry) {
        this.beanDefinitionRegistry = beanDefinitionRegistry;
    }

    @Override
    public void loadBeanDefinitions(Class<?>... annotatedClasses) {
        for (Class<?> annotatedClass : annotatedClasses) {
            registerBean(annotatedClass);
        }
    }

    private void registerBean(Class<?> annotatedClass) {
        beanDefinitionRegistry.registerBeanDefinition(annotatedClass, new GenericBeanDefinition(annotatedClass));
        Set<Method> beanMethods = BeanFactoryUtils.getBeanMethods(annotatedClass, Bean.class);
        for (Method beanMethod : beanMethods) {
            log.debug("@Bean method : {}", beanMethod);
            AnnotatedBeanDefinition abd = new AnnotatedBeanDefinition(beanMethod.getReturnType(), beanMethod);
            beanDefinitionRegistry.registerBeanDefinition(beanMethod.getReturnType(), abd);
        }
    }
}
