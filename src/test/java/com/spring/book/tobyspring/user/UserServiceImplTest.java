package com.spring.book.tobyspring.user;

import static com.spring.book.tobyspring.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static com.spring.book.tobyspring.user.service.UserServiceImpl.MIN_RECOMMEND_FOR_GOLD;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import com.spring.book.tobyspring.factorybean.TxProxyFactoryBean;
import com.spring.book.tobyspring.user.repository.UserDao;
import com.spring.book.tobyspring.user.service.UserService;
import com.spring.book.tobyspring.user.service.UserServiceImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.jws.soap.SOAPBinding.Use;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private MailSender mailSender;

    private List<User> users;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private TxProxyFactoryBean txProxyFactoryBean;

    @Before
    public void setUp() {
        users = Arrays.asList(
            new User("id1", "name1", "pass1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER -1, 0, "test1@gmail.com"),
            new User("id2", "name2", "pass2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "test2@gmail.com"),
            new User("id3", "name3", "pass3", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD-1, "test3@gmail.com"),
            new User("id4", "name4", "pass4", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD, "test4@gmail.com"),
            new User("id5", "name5", "pass5", Level.GOLD, 100, Integer.MAX_VALUE, "test5@gmail.com")
        );
    }

    @Test
    public void bean() {
        assertThat(this.userService, is(notNullValue()));
    }

    @Test
    @DirtiesContext
    public void upgradeLevels() {
        UserServiceImpl userServiceImpl = new UserServiceImpl();
        MockUserDao mockUserDao = new MockUserDao(this.users);

        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

        List<User> updated = mockUserDao.getUpdated();
        assertThat(updated.size(), is(2));
        checkUserAndLevel(updated.get(0), "id2", Level.SILVER);
        checkUserAndLevel(updated.get(1), "id4", Level.GOLD);

        List<String> request = mockMailSender.getRequests();
        assertThat(request.size(), is(2));
        assertThat(request.get(0), is(users.get(1).getEmail()));
        assertThat(request.get(1), is(users.get(3).getEmail()));
    }

    private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
        assertThat(updated.getId(), is(expectedId));
        assertThat(updated.getLevel(), is(expectedLevel));
    }

    @Test
    public void testAdd() {
        userDao.deleteAll();

        User userWithLevel = users.get(4);
        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null);

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

        assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
        assertThat(userWithoutLevelRead.getLevel(), is(Level.BASIC));
    }

    @Test
    @DirtiesContext
    public void upgradeAllOrNothing() throws Exception {
        UserServiceImpl testUserServiceImpl = new TestUserServiceImpl(users.get(3).getId());
        testUserServiceImpl.setUserDao(this.userDao);
        testUserServiceImpl.setMailSender(mailSender);

        TxProxyFactoryBean txProxyFactoryBean = context.getBean("%userService", TxProxyFactoryBean.class);
        txProxyFactoryBean.setTarget(testUserServiceImpl);
        UserService txUserService = (UserService) txProxyFactoryBean.getObject();

        userDao.deleteAll();

        for(User user : users) {
            userDao.add(user);
        }

        try {
            txUserService.upgradeLevels();
            fail("TestUserServiceException expected");
        } catch(TestUserServiceException e){}

        checkLevel(users.get(1),false);
    }

    private void checkLevel(User user, boolean upgraded) {
        User userUpdate = userDao.get(user.getId());

        if (upgraded) {
            assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
        } else {
            assertThat(userUpdate.getLevel(), is(user.getLevel()));
        }
    }

    static class TestUserServiceImpl extends UserServiceImpl {
        private String id;

        private TestUserServiceImpl(String id) {
            super( null, null);
            this.id = id;
        }

        protected void upgradeLevel(User user) {
            if (user.getId().equals(this.id)) {
                throw new TestUserServiceException();
            }
            super.upgradeLevel(user);
        }
    }

    static class TestUserServiceException extends RuntimeException {
    }

    static class MockMailSender implements MailSender {
        private List<String> requests = new ArrayList<>();

        public List<String> getRequests() {
            return requests;
        }

        @Override
        public void send(SimpleMailMessage mailMessage) throws MailException {
            requests.add(mailMessage.getTo()[0]);
        }

        @Override
        public void send(SimpleMailMessage... mailMessage) throws MailException {

        }
    }

    static class MockUserDao implements UserDao {
        private List<User> users;
        private List<User> updated = new ArrayList<>();

        private MockUserDao(List<User> users) {
            this.users = users;
        }

        public List<User> getUpdated() {
            return this.updated;
        }

        public List<User> getAll() {
            return this.users;
        }

        public void update(User user) {
            updated.add(user);
        }

        public void add(User user) { throw new UnsupportedOperationException(); }
        public User get(String id) { throw new UnsupportedOperationException(); }
        public void deleteAll() { throw new UnsupportedOperationException(); }
        public int getCount() { throw new UnsupportedOperationException(); }
    }

}
