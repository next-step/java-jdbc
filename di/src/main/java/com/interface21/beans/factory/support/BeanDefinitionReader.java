package com.interface21.beans.factory.support;

public interface BeanDefinitionReader {
    void loadBeanDefinitions(Class<?>... annotatedClasses);
}
