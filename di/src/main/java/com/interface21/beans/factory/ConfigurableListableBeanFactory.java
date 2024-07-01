package com.interface21.beans.factory;

public interface ConfigurableListableBeanFactory extends BeanFactory {
    void preInstantiateSingletons();
}
