package com.spring.book.tobyspring.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

@Configuration
public class DaoFactory {

    @Bean
    public UserDaoJdbc userDao() {
        UserDaoJdbc userDaoJdbc = new UserDaoJdbc() {
            protected PreparedStatement makeStatement(Connection c) throws SQLException {
                return null;
            }
        };
        userDaoJdbc.setDataSource(dataSource());

        return userDaoJdbc;
    }

    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

        dataSource.setDriverClass(com.mysql.cj.jdbc.Driver.class);
        dataSource.setUrl("jdbc:mysql://localhost/toby?characterEncoding=UTF-8&serverTimezone=Asia/Seoul");
        dataSource.setUsername("root");
        dataSource.setPassword("1234");

        return dataSource;
    }

}
