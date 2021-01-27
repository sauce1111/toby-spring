package com.spring.book.tobyspring.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDaoJdbcDeleteAll extends UserDaoJdbc {

    protected PreparedStatement makeStatement(Connection c) throws SQLException {
        return c.prepareStatement("delete from users");
    }
}
