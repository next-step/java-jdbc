package camp.nextstep.service;

import camp.nextstep.dao.DataAccessException;
import camp.nextstep.domain.User;
import camp.nextstep.jdbc.datasource.DataSourceUtils;
import com.interface21.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class TxUserService implements UserService {

    private final UserService appUserService;
    private final DataSource dataSource;

    @Autowired
    public TxUserService(AppUserService appUserService, DataSource dataSource) {
        this.appUserService = appUserService;
        this.dataSource = dataSource;
    }

    @Override
    public User findByAccount(String account) {
        return appUserService.findByAccount(account);
    }

    @Override
    public User findById(long id) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            connection.setAutoCommit(false);

            User returnValue = appUserService.findById(id);

            connection.commit();

            return returnValue;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new DataAccessException(ex);
            }
            throw new DataAccessException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public void save(User user) {
        appUserService.save(user);
    }

    @Override
    public void changePassword(long id, String newPassword, String createBy) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            connection.setAutoCommit(false);

            appUserService.changePassword(id, newPassword, createBy);

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new DataAccessException(ex);
            }
            throw new DataAccessException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }
}
