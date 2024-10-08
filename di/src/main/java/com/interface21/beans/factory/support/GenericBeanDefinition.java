package com.interface21.beans.factory.support;

import com.interface21.beans.factory.config.BeanDefinition;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class GenericBeanDefinition implements BeanDefinition {

    private final Class<?> beanClazz;
    private final Constructor<?> injectConstructor;
    private final Set<Field> injectFields;

    public GenericBeanDefinition(Class<?> clazz) {
        this.beanClazz = clazz;
        injectConstructor = getInjectConstructor(clazz);
        injectFields = getInjectFields(clazz, injectConstructor);
    }

    private static Constructor<?> getInjectConstructor(Class<?> clazz) {
        return BeanFactoryUtils.getInjectedConstructor(clazz);
    }

    private Set<Field> getInjectFields(Class<?> clazz, Constructor<?> constructor) {
        if (constructor != null) {
            return new HashSet<>();
        }

        Set<Field> injectFields = new HashSet<>();
        Set<Class<?>> injectProperties = getInjectPropertiesType(clazz);
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (injectProperties.contains(field.getType())) {
                injectFields.add(field);
            }
        }
        return injectFields;
    }

    private static Set<Class<?>> getInjectPropertiesType(Class<?> clazz) {
        Set<Class<?>> injectProperties = new HashSet<>();
        Set<Method> injectMethod = BeanFactoryUtils.getInjectedMethods(clazz);
        for (Method method : injectMethod) {
            Class<?>[] paramTypes = method.getParameterTypes();
            if (paramTypes.length != 1) {
                throw new IllegalStateException("DI할 메소드 인자는 하나여야 합니다.");
            }

            injectProperties.add(paramTypes[0]);
        }

        Set<Field> injectField = BeanFactoryUtils.getInjectedFields(clazz);
        for (Field field : injectField) {
            injectProperties.add(field.getType());
        }
        return injectProperties;
    }

    @Override
    public Constructor<?> getInjectConstructor() {
        return injectConstructor;
    }

    @Override
    public Set<Field> getInjectFields() {
        return this.injectFields;
    }

    @Override
    public Class<?> getBeanClass() {
        return this.beanClazz;
    }

    @Override
    public InjectType getResolvedInjectMode() {
        if (injectConstructor != null) {
            return InjectType.INJECT_CONSTRUCTOR;
        }

        if (!injectFields.isEmpty()) {
            return InjectType.INJECT_FIELD;
        }

        return InjectType.INJECT_NO;
    }
}
