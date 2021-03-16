package com.spring.book.tobyspring;

import com.spring.book.tobyspring.connection.ConnectionMaker;
import com.spring.book.tobyspring.connection.SimpleConnectionMaker;
import com.spring.book.tobyspring.factorybean.MessageFactoryBean;
import com.spring.book.tobyspring.factorybean.TxProxyFactoryBean;
import com.spring.book.tobyspring.mail.DummyMailService;
import com.spring.book.tobyspring.user.repository.UserDaoJdbc;
import com.spring.book.tobyspring.user.repository.UserDao;
import com.spring.book.tobyspring.user.service.UserService;
import com.spring.book.tobyspring.user.service.UserServiceImpl;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.mail.MailSender;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ApplicationConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public UserDao userDao() throws ClassNotFoundException {
        return new UserDaoJdbc(dataSource);
    }

    @Bean
    public ConnectionMaker connectionMaker(){
        return new SimpleConnectionMaker();
    }

    @Bean
    public UserServiceImpl userServiceImpl() throws ClassNotFoundException {
        return new UserServiceImpl(userDao(), mailSender());
    }

    @Bean
    public PlatformTransactionManager platformTransactionManager(){
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public MailSender mailSender(){
        return new DummyMailService();
    }

    @Bean
    public MessageFactoryBean message(){
        MessageFactoryBean messageFactoryBean = new MessageFactoryBean();
        messageFactoryBean.setText("Factory Bean");
        return messageFactoryBean;
    }

    @Bean
    public TxProxyFactoryBean userService() throws ClassNotFoundException {
        TxProxyFactoryBean txProxyFactoryBean = new TxProxyFactoryBean();
        txProxyFactoryBean.setTarget(userServiceImpl());
        txProxyFactoryBean.setTransactionManager(platformTransactionManager());
        txProxyFactoryBean.setPattern("upgradeLevels");
        txProxyFactoryBean.setServiceInterface(UserService.class);

        return txProxyFactoryBean;
    }

}
