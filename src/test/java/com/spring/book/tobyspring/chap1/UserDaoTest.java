package com.spring.book.tobyspring.chap1;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import com.spring.book.tobyspring.user.DaoFactory;
import com.spring.book.tobyspring.user.User;
import com.spring.book.tobyspring.user.UserDaoJdbc;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import javax.sql.DataSource;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

class UserDaoTest {

    private User user1;
    private User user2;
    private User user3;

    @Autowired
    UserDaoJdbc userDaoJdbc;

    @Autowired
    DataSource dataSource;

    @BeforeEach
    public void setUp() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(
            DaoFactory.class);
        userDaoJdbc = applicationContext.getBean("userDao", UserDaoJdbc.class);
        dataSource = applicationContext.getBean("dataSource", DataSource.class);

        user1 = new User("id1", "name1", "pass1");
        user2 = new User("id2", "name2", "pass2");
        user3 = new User("aid3", "name3", "pass3");
    }

    @AfterEach
    void tearDown() throws SQLException, ClassNotFoundException {
        userDaoJdbc.deleteAll();
    }

    @Test
    @DisplayName("새로운 User를 추가한다.")
    void add() throws SQLException {
        User user1 = new User("sauce", "test", "pass1");
        User user2 = new User("sauce2", "test2", "pass2");

        userDaoJdbc.add(user1);
        userDaoJdbc.add(user2);
        Assertions.assertThat(userDaoJdbc.getCount()).isEqualTo(2);

        User userGet1 = userDaoJdbc.get(user1.getId());
        Assertions.assertThat(userGet1.getName()).isEqualTo((user1.getName()));
        Assertions.assertThat(userGet1.getPassword()).isEqualTo(user1.getPassword());

        User userGet2 = userDaoJdbc.get(user2.getId());
        Assertions.assertThat(userGet2.getName()).isEqualTo((user2.getName()));
        Assertions.assertThat(userGet2.getPassword()).isEqualTo(user2.getPassword());
    }

    @Test
    @DisplayName("총 User가 몇 명인지 조회한다.")
    void count() throws SQLException {
        userDaoJdbc.deleteAll();
        Assertions.assertThat(userDaoJdbc.getCount()).isEqualTo(0);

        User user1 = new User("sspark", "test1", "pass1");
        userDaoJdbc.add(user1);
        Assertions.assertThat(userDaoJdbc.getCount()).isEqualTo(1);

        User user2 = new User("sspark2", "test2", "pass2");
        userDaoJdbc.add(user2);
        Assertions.assertThat(userDaoJdbc.getCount()).isEqualTo(2);

        User user3 = new User("sspark3", "test3", "pass3");
        userDaoJdbc.add(user3);
        Assertions.assertThat(userDaoJdbc.getCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("존재하지 않는 User id로 조회시 예외가 발생한다.")
    void getUserFailure() throws SQLException {
        userDaoJdbc.deleteAll();
        Assertions.assertThat(userDaoJdbc.getCount()).isEqualTo(0);

        assertThatThrownBy(() -> userDaoJdbc.get("unknown_id"))
            .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    void getAll() throws SQLException {
        userDaoJdbc.deleteAll();

        List<User> users0 = userDaoJdbc.getAll();
        Assertions.assertThat(users0.size()).isEqualTo(0);

        userDaoJdbc.add(user1);
        List<User> users1 = userDaoJdbc.getAll();
        Assertions.assertThat(users1.size()).isEqualTo(1);

        userDaoJdbc.add(user2);
        List<User> users2 = userDaoJdbc.getAll();
        Assertions.assertThat(users2.size()).isEqualTo(2);

        checkSameUser(user1, users2.get(0));
        checkSameUser(user2, users2.get(1));

        userDaoJdbc.add(user3);
        List<User> users3 = userDaoJdbc.getAll();
        Assertions.assertThat(users3.size()).isEqualTo(3);

        checkSameUser(user3, users3.get(0));
        checkSameUser(user1, users3.get(1));
        checkSameUser(user2, users3.get(2));

    }

    private void checkSameUser(User user1, User user2) {
        assertThat(user1.getId(), is(user2.getId()));
        assertThat(user1.getName(), is(user2.getName()));
        assertThat(user1.getPassword(), is(user2.getPassword()));
    }

    @Test
    void duplicateKey() {
        userDaoJdbc.deleteAll();
        userDaoJdbc.add(user1);
        org.junit.jupiter.api.Assertions.assertThrows(DuplicateKeyException.class, () -> {
            userDaoJdbc.add(user1);
        });
    }

    @Test
    void sqlExceptionTranslate() {
        userDaoJdbc.deleteAll();

        try {
            userDaoJdbc.add(user1);
            userDaoJdbc.add(user1);
        } catch (DuplicateKeyException exception) {
            SQLException sqlException = (SQLException)exception.getRootCause();
            SQLExceptionTranslator sqlExceptionTranslator = new SQLErrorCodeSQLExceptionTranslator(this.dataSource);
            assertThat(sqlExceptionTranslator.translate(null, null, sqlException),
                is(DuplicateKeyException.class));
        }
    }

}
