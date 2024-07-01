package camp.nextstep.support.context;

import camp.nextstep.config.MyConfiguration;
import camp.nextstep.support.jdbc.init.DatabasePopulatorUtils;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class ContextLoaderListener implements ServletContextListener {

    @Override
    public void contextInitialized(final ServletContextEvent sce) {
        final var myConfiguration = new MyConfiguration();
        DatabasePopulatorUtils.execute(myConfiguration.dataSource());
    }
}
