package com.interface21.webmvc.servlet.mvc.tobe.support;

import com.interface21.core.MethodParameter;
import com.interface21.core.util.ReflectionUtils;
import com.interface21.core.util.StringUtils;
import com.interface21.web.method.support.HandlerMethodArgumentResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.*;
import java.util.Arrays;


public class ModelArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        if (methodParameter.hasAnnotation()) {
            return false;
        }
        return !isSimpleType(methodParameter.getType());
    }

    private boolean isSimpleType(Class<?> clazz) {
        return ClassUtils.isPrimitiveOrWrapper(clazz)
                || CharSequence.class.isAssignableFrom(clazz);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, HttpServletRequest request, HttpServletResponse response) {
        try {
            return resolveArgumentInternal(methodParameter, request, response);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(methodParameter.getType() + " Constructor access failed", e);
        } catch (InstantiationException e) {
            throw new IllegalStateException(methodParameter.getType() + " Instantiation failed", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(methodParameter.getType() + " target invoke failed", e);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(methodParameter.getType() + " method not found", e);
        }
    }

    private Object resolveArgumentInternal(MethodParameter methodParameter, HttpServletRequest request, HttpServletResponse response) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        Class<?> clazz = methodParameter.getType();
        Object argument = getDefaultInstance(clazz, request);

        for (Field field : clazz.getDeclaredFields()) {
            populateArgument(argument, clazz, field, request);
        }

        return argument;
    }

    private void populateArgument(Object target, Class<?> clazz, Field field, HttpServletRequest request) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        final String setterMethod = "set" + StringUtils.upperFirstChar(field.getName());

        if (ReflectionUtils.hasFieldMethod(clazz, setterMethod, field.getType())) {
            final Method method = clazz.getDeclaredMethod(setterMethod, field.getType());
            method.invoke(target, ReflectionUtils.convertStringValue(request.getParameter(field.getName()), field.getType()));
        }
    }

    private <T> T getDefaultInstance(Class<T> clazz, HttpServletRequest request) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        for (Constructor constructor : clazz.getConstructors()) {
            final String[] parameterNames = Arrays.stream(constructor.getParameters())
                .map(Parameter::getName)
                .toArray(String[]::new);

            final Class[] parameterTypes = constructor.getParameterTypes();
            Object[] args = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                args[i] = parameterTypes[i].cast(request.getParameter(parameterNames[i]));
            }

            final Object arg = constructor.newInstance(args);

            return clazz.cast(arg);
        }

        throw new IllegalStateException("[" + clazz.getName() + "] supported constructor is empty");
    }

}
