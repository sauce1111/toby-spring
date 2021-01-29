package com.spring.book.tobyspring.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;

public class JdbcContext {

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void workWithStatementStrategy(StatementStrategy statementStrategy) throws SQLException {
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = dataSource.getConnection();
            ps = statementStrategy.makePreparedStatement(connection);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            if (ps != null) { try { ps.close(); } catch (SQLException e) { } }
            if (connection != null) { try { connection.close(); } catch (SQLException e) { } }
        }
    }

    public void executeSql(final String query) throws SQLException {
        workWithStatementStrategy(
            connection -> connection.prepareStatement(query)
        );
    }

    public void executeSqlWithParams(final String query, String... params) throws SQLException {
        workWithStatementStrategy(
            connection -> {
                PreparedStatement ps = connection.prepareStatement(query);

                for (int i = 0; i < params.length; i++) {
                    ps.setString(i + 1, params[i]);
                }

                return ps;
            }
        );
    }

}
