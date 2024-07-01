package com.interface21.context.support;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;
import samples.DatasourceConfiguration;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

class AnnotationConfigWebApplicationContextTest {

    @Test
    void configuration() {
        final var applicationContext = new AnnotationConfigWebApplicationContext(DatasourceConfiguration.class);
        final var dataSource = applicationContext.getBean(DataSource.class);
        assertThat(dataSource).isInstanceOf(DataSource.class);
        assertThat(dataSource).isInstanceOf(JdbcDataSource.class);
    }
}
