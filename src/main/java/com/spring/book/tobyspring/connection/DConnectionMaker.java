package com.spring.book.tobyspring.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DConnectionMaker implements ConnectionMaker {

    @Override
    public Connection makeConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver.class");

        return DriverManager.getConnection(
            "jdbc:mysql://localhost/toby?characterEncoding=UTF-8&serverTimezone=Asia/Seoul",
            "root",
            "1234");
    }

}
