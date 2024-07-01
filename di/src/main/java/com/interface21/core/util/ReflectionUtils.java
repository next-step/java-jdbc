package com.interface21.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public abstract class ReflectionUtils {

    private static final Logger log = LoggerFactory.getLogger(ReflectionUtils.class);

    public static <T> Constructor<T> accessibleConstructor(Class<T> clazz, Class<?>... parameterTypes)
            throws NoSuchMethodException {

        Constructor<T> ctor = clazz.getDeclaredConstructor(parameterTypes);
        makeAccessible(ctor);
        return ctor;
    }

    public static void makeAccessible(Constructor<?> ctor) {
        if ((!Modifier.isPublic(ctor.getModifiers()) ||
                !Modifier.isPublic(ctor.getDeclaringClass().getModifiers())) && !ctor.isAccessible()) {
            ctor.setAccessible(true);
        }
    }

    public static <T> T newInstance(Class<T> clazz, Object... args) {

        Constructor<?> constructor = getConstructorByArgs(clazz, args);

        if (constructor == null) {
            throw new IllegalArgumentException(clazz.getSimpleName() + " doesn't have args size constructor");
        }

        try {
            return clazz.cast(constructor.newInstance(args));
        } catch (IllegalAccessException e) {
            log.warn("{} constructor access failed", constructor.getName());
        } catch(InvocationTargetException e) {
            log.warn("{} target invalid", clazz.getSimpleName());
        } catch (InstantiationException e) {
            log.warn("{} instantiation failed", clazz.getSimpleName());
        }

        throw new RuntimeException(clazz.getSimpleName() + " instantiation failed");
    }

    public static Constructor<?> getConstructorByArgs(Class<?> clazz, Object... args) {
        for (Constructor<?> candidate : clazz.getConstructors()) {
            if (isMatched(candidate, args)) {
                return candidate;
            }
        }
        return null;
    }

    public static boolean isMatched(Constructor<?> constructor, Object... args) {
        if (constructor.getParameterCount() != args.length) {
            return false;
        }

        final Class<?>[] parameterTypes = constructor.getParameterTypes();
        for (int i = 0; i < args.length; i++) {
            if (parameterTypes[i] != args[i].getClass()) { // todo handle primitive type
                return false;
            }
        }
        return true;
    }

    public static Object convertStringValue(String value, Class<?> clazz) {
        if (clazz == String.class) {
            return clazz.cast(value);
        } else if (clazz == int.class || clazz == Integer.class) {
            return Integer.valueOf(value);
        } else if (clazz == long.class || clazz == Long.class) {
            return Long.valueOf(value);
        } else if (clazz == float.class || clazz == Float.class) {
            return Float.valueOf(value);
        } else if (clazz == double.class || clazz == Double.class) {
            return Double.valueOf(value);
        }

        throw new IllegalArgumentException(clazz.getTypeName() + " is not supported");
    }

    public static boolean hasFieldMethod(Class<?> clazz, String methodName, Class<?> type) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName) && method.getParameterTypes()[0] == type) {
                return true;
            }
        }
        return false;
    }
}
