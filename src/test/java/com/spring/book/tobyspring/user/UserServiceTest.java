package com.spring.book.tobyspring.user;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static com.spring.book.tobyspring.user.UserService.MIN_LOG_COUNT_FOR_SILVER;
import static com.spring.book.tobyspring.user.UserService.MIN_RECOMMEND_FOR_GOLD;

import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDaoJdbc dao;

    List<User> users;

    @Before
    public void setup() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(
            DaoFactory.class);
        dao = applicationContext.getBean("userDao", UserDaoJdbc.class);

        users = Arrays.asList(
            new User("id1", "name1", "pass1", Level.BASIC, MIN_LOG_COUNT_FOR_SILVER - 1, 0),
            new User("id2", "name2", "pass2", Level.BASIC, MIN_LOG_COUNT_FOR_SILVER, 0),
            new User("id3", "name3", "pass3", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD - 1),
            new User("id4", "name4", "pass4", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD),
            new User("id5", "name5", "pass5", Level.GOLD, 100, Integer.MAX_VALUE)
        );
    }

    @Test
    public void upgradeLevels() {
        dao.deleteAll();

        for (User user : users) {
            dao.add(user);
        }

        userService.upgradeLevels();

        checkLevel(users.get(0), false);
        checkLevel(users.get(1), true);
        checkLevel(users.get(2), false);
        checkLevel(users.get(3), true);
        checkLevel(users.get(4), false);
    }

    private void checkLevel(User user, boolean upgraded) {
        User userUpdate = dao.get(user.getId());
        if (upgraded) {
            assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
        } else {
            assertThat(userUpdate.getLevel(), is(user.getLevel()));
        }
    }

    @Test
    public void add() {
        dao.deleteAll();

        User userWithLevel = users.get(4);
        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null);

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        User userWithLevelRead = dao.get(userWithLevel.getId());
        User userWithoutLevelRead = dao.get(userWithoutLevel.getId());

        assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
        assertThat(userWithoutLevelRead.getLevel(), is(Level.BASIC));
    }

}