package com.spring.book.tobyspring.user;

import com.spring.book.tobyspring.user.repository.UserDao;
import com.spring.book.tobyspring.user.service.UserService;
import com.spring.book.tobyspring.user.service.UserServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.mail.MailSender;

@AllArgsConstructor
public class TestUserService extends UserServiceImpl implements UserService {

    private String id;

    public TestUserService(UserDao userDao, MailSender mailSender) {
        super(userDao, mailSender);
    }

    public void setId(String id) {
        this.id = id;
    }

    static class TestUserServiceException extends RuntimeException {}

    protected void upgradeLevel(User user) {
        if (user.getId().equals(id)) {
            throw new TestUserServiceException();
        }

        super.upgradeLevel(user);
    }

}
