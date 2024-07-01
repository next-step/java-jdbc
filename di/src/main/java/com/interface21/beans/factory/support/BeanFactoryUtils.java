package com.interface21.beans.factory.support;

import com.interface21.beans.factory.annotation.Autowired;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;

public class BeanFactoryUtils {

    private static final Logger log = LoggerFactory.getLogger(BeanFactoryUtils.class);

    @SuppressWarnings({ "unchecked" })
    public static Set<Method> getInjectedMethods(Class<?> clazz) {
        return ReflectionUtils.getAllMethods(clazz, ReflectionUtils.withAnnotation(Autowired.class), ReflectionUtils.withReturnType(void.class));
    }

    @SuppressWarnings({ "unchecked" })
    public static Set<Method> getBeanMethods(Class<?> clazz, Class<? extends Annotation> annotation) {
        return ReflectionUtils.getAllMethods(clazz, ReflectionUtils.withAnnotation(annotation));
    }

    @SuppressWarnings({ "unchecked" })
    public static Set<Field> getInjectedFields(Class<?> clazz) {
        return ReflectionUtils.getAllFields(clazz, ReflectionUtils.withAnnotation(Autowired.class));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Set<Constructor> getInjectedConstructors(Class<?> clazz) {
        return ReflectionUtils.getAllConstructors(clazz, ReflectionUtils.withAnnotation(Autowired.class));
    }

    public static Optional<Object> invokeMethod(Method method, Object bean, Object[] args) {
        try {
            return Optional.ofNullable(method.invoke(bean, args));
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * 인자로 전달하는 클래스의 생성자 중 @Inject 애노테이션이 설정되어 있는 생성자를 반환
     * 
     * @Inject 애노테이션이 설정되어 있는 생성자는 클래스당 하나로 가정한다.
     * @param clazz
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Constructor<?> getInjectedConstructor(Class<?> clazz) {
        Set<Constructor> injectedConstructors = ReflectionUtils.getAllConstructors(clazz, ReflectionUtils.withAnnotation(Autowired.class));
        if (injectedConstructors.isEmpty()) {
            return null;
        }
        return injectedConstructors.iterator().next();
    }

    /**
     * 인자로 전달되는 클래스의 구현 클래스. 만약 인자로 전달되는 Class가 인터페이스가 아니면 전달되는 인자가 구현 클래스,
     * 인터페이스인 경우 BeanFactory가 관리하는 모든 클래스 중에 인터페이스를 구현하는 클래스를 찾아 반환
     * 
     * @param injectedClazz
     * @param preInstanticateBeans
     * @return
     */
    public static Optional<Class<?>> findConcreteClass(Class<?> injectedClazz, Set<Class<?>> preInstanticateBeans) {
        if (!injectedClazz.isInterface()) {
            return Optional.of(injectedClazz);
        }

        for (Class<?> clazz : preInstanticateBeans) {
            Set<Class<?>> interfaces = Set.of(clazz.getInterfaces());
            if (interfaces.contains(injectedClazz)) {
                return Optional.of(clazz);
            }
        }

        return Optional.empty();
    }
}
