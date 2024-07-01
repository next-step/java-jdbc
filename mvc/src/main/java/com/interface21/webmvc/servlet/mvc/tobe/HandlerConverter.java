package com.interface21.webmvc.servlet.mvc.tobe;

import com.interface21.web.bind.annotation.RequestMapping;
import com.interface21.web.bind.annotation.RequestMethod;
import com.interface21.web.method.support.HandlerMethodArgumentResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class HandlerConverter {

    private static final Logger log = LoggerFactory.getLogger(HandlerConverter.class);

    private List<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList<>();

    public void setArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        this.argumentResolvers.addAll(argumentResolvers);
    }

    public void addArgumentResolver(HandlerMethodArgumentResolver argumentResolver) {
        this.argumentResolvers.add(argumentResolver);
    }

    public Map<HandlerKey, HandlerExecution> convert(Map<Class<?>, Object> controllers) {
        Map<HandlerKey, HandlerExecution> handlers = new HashMap<>();
        Set<Class<?>> controllerClazz = controllers.keySet();
        for (Class<?> controller : controllerClazz) {
            Object target = controllers.get(controller);
            addHandlerExecution(handlers, target, controller.getMethods());
        }

        return handlers;
    }

    private void addHandlerExecution(Map<HandlerKey, HandlerExecution> handlerExecutions,
                                     final Object target,
                                     Method[] methods) {
        Arrays.stream(methods)
            .filter(method -> method.isAnnotationPresent(RequestMapping.class))
            .forEach(method -> {
                final var requestMapping = method.getAnnotation(RequestMapping.class);
                handlerExecutions.putAll(createHandlerExecutions(target, method, requestMapping));
                log.debug("register handlerExecution : url is {}, request method : {}, method is {}",
                    requestMapping.value(), requestMapping.method(), method);
            });
    }

    private Map<HandlerKey, HandlerExecution> createHandlerExecutions(final Object target, final Method method, final RequestMapping requestMapping) {
        return mapHandlerKeys(requestMapping.value(), requestMapping.method())
            .stream()
            .collect(Collectors.toMap(
                handlerKey -> handlerKey,
                handlerKey -> new HandlerExecution(argumentResolvers, target, method)
            ));
    }

    private List<HandlerKey> mapHandlerKeys(final String value, final RequestMethod[] originalMethods) {
        var targetMethods = originalMethods;
        if (targetMethods.length == 0) {
            targetMethods = RequestMethod.values();
        }
        return Arrays.stream(targetMethods)
            .map(method -> new HandlerKey(value, method))
            .collect(Collectors.toList());
    }
}
