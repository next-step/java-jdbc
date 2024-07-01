package com.interface21.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class BeanInstantiationException extends RuntimeException {

    private final Class<?> beanClass;

    private final Constructor<?> constructor;

    private final Method constructingMethod;

    /**
     * Create a new BeanInstantiationException.
     * @param beanClass the offending bean class
     * @param msg the detail message
     */
    public BeanInstantiationException(Class<?> beanClass, String msg) {
        this(beanClass, msg, null);
    }

    /**
     * Create a new BeanInstantiationException.
     * @param beanClass the offending bean class
     * @param msg the detail message
     * @param cause the root cause
     */
    public BeanInstantiationException(Class<?> beanClass, String msg, Throwable cause) {
        super("Failed to instantiate [" + beanClass.getName() + "]: " + msg, cause);
        this.beanClass = beanClass;
        this.constructor = null;
        this.constructingMethod = null;
    }

    /**
     * Create a new BeanInstantiationException.
     * @param constructor the offending constructor
     * @param msg the detail message
     * @param cause the root cause
     * @since 4.3
     */
    public BeanInstantiationException(Constructor<?> constructor, String msg, Throwable cause) {
        super("Failed to instantiate [" + constructor.getDeclaringClass().getName() + "]: " + msg, cause);
        this.beanClass = constructor.getDeclaringClass();
        this.constructor = constructor;
        this.constructingMethod = null;
    }

    /**
     * Create a new BeanInstantiationException.
     * @param constructingMethod the delegate for bean construction purposes
     * (typically, but not necessarily, a static factory method)
     * @param msg the detail message
     * @param cause the root cause
     * @since 4.3
     */
    public BeanInstantiationException(Method constructingMethod, String msg, Throwable cause) {
        super("Failed to instantiate [" + constructingMethod.getReturnType().getName() + "]: " + msg, cause);
        this.beanClass = constructingMethod.getReturnType();
        this.constructor = null;
        this.constructingMethod = constructingMethod;
    }


    /**
     * Return the offending bean class (never {@code null}).
     * @return the class that was to be instantiated
     */
    public Class<?> getBeanClass() {
        return this.beanClass;
    }

    /**
     * Return the offending constructor, if known.
     * @return the constructor in use, or {@code null} in case of a
     * factory method or in case of default instantiation
     * @since 4.3
     */
    public Constructor<?> getConstructor() {
        return this.constructor;
    }

    public Method getConstructingMethod() {
        return this.constructingMethod;
    }

}
