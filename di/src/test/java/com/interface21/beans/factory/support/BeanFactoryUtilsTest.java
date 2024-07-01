package com.interface21.beans.factory.support;

import com.interface21.context.annotation.AnnotatedBeanDefinition;
import org.junit.jupiter.api.Test;
import samples.DatasourceConfiguration;

import java.lang.reflect.Method;

class BeanFactoryUtilsTest {

    @Test
    void name() throws NoSuchMethodException {
        final var aClass = DatasourceConfiguration.class;
        final var annotatedBeanDefinition = new AnnotatedBeanDefinition(aClass, aClass.getMethod("dataSource"));
        Method method = annotatedBeanDefinition.getMethod();
        final var o = BeanFactoryUtils.invokeMethod(method, new DatasourceConfiguration(), new Object[0])
            .orElse(null);
        System.out.println(o);
    }
}
