package com.spring.book.tobyspring.user;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import com.spring.book.tobyspring.user.repository.UserDaoJdbc;
import java.util.List;
import javax.sql.DataSource;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;

class UserDaoTest {

    private User user1;
    private User user2;
    private User user3;

    @Autowired
    UserDaoJdbc dao;

    @Autowired
    DataSource dataSource;

    @BeforeEach
    public void setUp() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(
            DaoFactory.class);
        dao = applicationContext.getBean("userDao", UserDaoJdbc.class);
        dataSource = applicationContext.getBean("dataSource", DataSource.class);

        user1 = new User("id1", "name1", "pass1", Level.BASIC, 1, 0);
        user2 = new User("id2", "name2", "pass2", Level.SILVER, 55, 10);
        user3 = new User("aid3", "name3", "pass3", Level.GOLD, 100, 40);
    }

    @AfterEach
    void tearDown() {
        dao.deleteAll();
    }

    @Test
    @DisplayName("새로운 User를 추가한다.")
    void addAndGet() {
        dao.add(user1);
        dao.add(user2);
        Assertions.assertThat(dao.getCount()).isEqualTo(2);

        User userGet1 = dao.get(user1.getId());
        checkSameUser(userGet1, user1);

        User userGet2 = dao.get(user2.getId());
        checkSameUser(userGet2, user2);
    }

    @Test
    @DisplayName("총 User가 몇 명인지 조회한다.")
    void count() {
        dao.deleteAll();
        Assertions.assertThat(dao.getCount()).isEqualTo(0);

        dao.add(user1);
        Assertions.assertThat(dao.getCount()).isEqualTo(1);

        dao.add(user2);
        Assertions.assertThat(dao.getCount()).isEqualTo(2);

        dao.add(user3);
        Assertions.assertThat(dao.getCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("존재하지 않는 User id로 조회시 예외가 발생한다.")
    void getUserFailure() {
        dao.deleteAll();
        Assertions.assertThat(dao.getCount()).isEqualTo(0);

        assertThatThrownBy(() -> dao.get("unknown_id"))
            .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    void getAll() {
        dao.deleteAll();

        List<User> users0 = dao.getAll();
        Assertions.assertThat(users0.size()).isEqualTo(0);

        dao.add(user1);
        List<User> users1 = dao.getAll();
        Assertions.assertThat(users1.size()).isEqualTo(1);

        dao.add(user2);
        List<User> users2 = dao.getAll();
        Assertions.assertThat(users2.size()).isEqualTo(2);

        checkSameUser(user1, users2.get(0));
        checkSameUser(user2, users2.get(1));

        dao.add(user3);
        List<User> users3 = dao.getAll();
        Assertions.assertThat(users3.size()).isEqualTo(3);

        checkSameUser(user3, users3.get(0));
        checkSameUser(user1, users3.get(1));
        checkSameUser(user2, users3.get(2));

    }

    private void checkSameUser(User user1, User user2) {
        assertThat(user1.getId(), is(user2.getId()));
        assertThat(user1.getName(), is(user2.getName()));
        assertThat(user1.getPassword(), is(user2.getPassword()));
        assertThat(user1.getLevel(), is(user2.getLevel()));
        assertThat(user1.getLogin(), is(user2.getLogin()));
        assertThat(user1.getRecommend(), is(user2.getRecommend()));
    }

    @Test
    void duplicateKey() {
        dao.deleteAll();
        dao.add(user1);
        org.junit.jupiter.api.Assertions.assertThrows(DuplicateKeyException.class, () -> {
            dao.add(user1);
        });
    }

    @Test
    @DisplayName("사용자 정보 수정")
    void update() {
        dao.deleteAll();
        dao.add(user1);
        dao.add(user2);

        user1.setName("수정명");
        user1.setPassword("spring6");
        user1.setLevel(Level.GOLD);
        user1.setLogin(1000);
        user1.setRecommend(999);

        dao.update(user1);

        User updateUser1 = dao.get(user1.getId());
        checkSameUser(user1, updateUser1);
        User updateUser2 = dao.get(user2.getId());
        checkSameUser(user2, updateUser2);
    }

//    @Test
//    void sqlExceptionTranslate() {
//        dao.deleteAll();
//
//        try {
//            dao.add(user1);
//            dao.add(user1);
//        } catch (DuplicateKeyException exception) {
//            SQLException sqlException = (SQLException)exception.getRootCause();
//            SQLExceptionTranslator sqlExceptionTranslator = new SQLErrorCodeSQLExceptionTranslator(this.dataSource);
//            assertThat(sqlExceptionTranslator.translate(null, null, sqlException),
//                is(DuplicateKeyException.class));
//        }
//    }

}
