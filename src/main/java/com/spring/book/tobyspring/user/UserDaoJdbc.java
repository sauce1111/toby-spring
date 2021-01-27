package com.spring.book.tobyspring.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class UserDaoJdbc implements UserDao {

    private RowMapper<User> userMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));

            return user;
        }
    };

    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void add(final User user) {
//        // 로컬 클래스
//        class AddStatement implements StatementStrategy {
//            @Override
//            public PreparedStatement makePreparedStatement(Connection connection)
//                throws SQLException {
//                PreparedStatement ps = connection.
//                    prepareStatement("insert into users (id, name, password) values (?, ?, ?)");
//
//                ps.setString(1, user.getId());
//                ps.setString(2, user.getName());
//                ps.setString(3, user.getPassword());
//
//                return ps;
//            }
//        }

        // 익명 내부 클래스
//        this.jdbcContext.workWithStatementStrategy(
//            new StatementStrategy() {
//                @Override
//                public PreparedStatement makePreparedStatement(Connection connection)
//                    throws SQLException {
//                    PreparedStatement ps = connection.
//                        prepareStatement("insert into users (id, name, password) values (?, ?, ?)");
//
//                    ps.setString(1, user.getId());
//                    ps.setString(2, user.getName());
//                    ps.setString(3, user.getPassword());
//
//                    return ps;
//                }
//            }
//        );

        // 콜백 분리
//        this.jdbcContext.executeSqlWithParams("insert into users (id, name, password) values (?, ?, ?)",
//            user.getId(), user.getName(), user.getPassword());

        // jdbcTemplate 사용
        this.jdbcTemplate.update("insert into users (id, name, password) values (?, ?, ?)",
            user.getId(), user.getName(), user.getPassword());
    }

    public User get(String id) {

        return this.jdbcTemplate.queryForObject("select * from users where id = ?",
            new Object[]{id},
            this.userMapper
        );

    }

    public void deleteAll() {
//        // 익명 내부 클래스
//        this.jdbcContext.workWithStatementStrategy(
//            new StatementStrategy() {
//                @Override
//                public PreparedStatement makePreparedStatement(Connection connection)
//                    throws SQLException {
//                    return connection.prepareStatement("delete from users");
//                }
//            }
//        );

        // 콜백의 재사용을 위한 분리
//        this.jdbcContext.executeSql("delete from users");

        // jdbcTemplate 사용
        this.jdbcTemplate.update("delete from users");
    }

    public int getCount() {
        return this.jdbcTemplate.queryForObject("select count(*) from users", Integer.class);
    }

    public List<User> getAll() {
        return this.jdbcTemplate.query("select * from users order by id", this.userMapper);
    }

}
