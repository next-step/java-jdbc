package com.interface21.webmvc.servlet.mvc.tobe;

import com.interface21.context.ApplicationContext;
import com.interface21.context.stereotype.Controller;
import com.interface21.web.bind.annotation.RequestMethod;
import com.interface21.webmvc.servlet.mvc.HandlerMapping;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class AnnotationHandlerMapping implements HandlerMapping {

    private static final Logger log = LoggerFactory.getLogger(AnnotationHandlerMapping.class);

    private final ApplicationContext applicationContext;
    private final HandlerConverter handlerConverter;
    private final Map<HandlerKey, HandlerExecution> handlerExecutions = new HashMap<>();

    public AnnotationHandlerMapping(final ApplicationContext applicationContext, final HandlerConverter handlerConverter) {
        this.applicationContext = applicationContext;
        this.handlerConverter = handlerConverter;
    }

    public void initialize() {
        Map<Class<?>, Object> controllers = getControllers(applicationContext);
        handlerExecutions.putAll(handlerConverter.convert(controllers));
        log.info("Initialized AnnotationHandlerMapping!");
    }

    private Map<Class<?>, Object> getControllers(ApplicationContext ac) {
        final var controllers = new HashMap<Class<?>, Object>();
        for (Class<?> clazz : ac.getBeanClasses()) {
            final var annotation = clazz.getAnnotation(Controller.class);
            if (annotation != null) {
                controllers.put(clazz, ac.getBean(clazz));
            }
        }
        return controllers;
    }

    public HandlerExecution getHandler(final HttpServletRequest request) {
        final var requestUri = request.getRequestURI();
        final var requestMethod = RequestMethod.valueOf(request.getMethod().toUpperCase());
        log.debug("requestUri : {}, requestMethod : {}", requestUri, requestMethod);
        return getHandlerInternal(new HandlerKey(requestUri, requestMethod));
    }

    private HandlerExecution getHandlerInternal(final HandlerKey requestHandlerKey) {
        for (HandlerKey handlerKey : handlerExecutions.keySet()) {
            if (handlerKey.isMatch(requestHandlerKey)) {
                return handlerExecutions.get(handlerKey);
            }
        }
        return null;
    }
}
