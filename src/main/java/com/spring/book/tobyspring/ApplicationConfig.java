package com.spring.book.tobyspring;

import com.spring.book.tobyspring.connection.ConnectionMaker;
import com.spring.book.tobyspring.connection.DConnectionMaker;
import com.spring.book.tobyspring.message.MessageFactoryBean;
import com.spring.book.tobyspring.mail.DummyMailService;
import com.spring.book.tobyspring.user.proxy.TransactionAdvice;
import com.spring.book.tobyspring.user.repository.UserDao;
import com.spring.book.tobyspring.user.repository.UserDaoJdbc;
import com.spring.book.tobyspring.user.service.UserServiceImpl;
import javax.sql.DataSource;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
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
    public UserDao userDao() {
        return new UserDaoJdbc(dataSource);
    }

    @Bean
    public ConnectionMaker connectionMaker() {
        return new DConnectionMaker();
    }

    @Bean
    public UserServiceImpl userServiceImpl() {
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

//    @Bean
//    public TxProxyFactoryBean userService() throws ClassNotFoundException {
//        TxProxyFactoryBean txProxyFactoryBean = new TxProxyFactoryBean();
//        txProxyFactoryBean.setTarget(userServiceImpl());
//        txProxyFactoryBean.setTransactionManager(platformTransactionManager());
//        txProxyFactoryBean.setPattern("upgradeLevels");
//        txProxyFactoryBean.setServiceInterface(UserService.class);
//
//        return txProxyFactoryBean;
//    }

    @Bean
    public TransactionAdvice transactionAdvice () {
        TransactionAdvice transactionAdvice = new TransactionAdvice();
        transactionAdvice.setTransactionManager(platformTransactionManager());

        return transactionAdvice;
    }

    @Bean
    public NameMatchMethodPointcut nameMatchMethodPointcut(){
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("upgrade*");

        return pointcut;
    }

    @Bean
    public DefaultPointcutAdvisor transactionAdvisor(){
        DefaultPointcutAdvisor defaultPointcutAdvisor = new DefaultPointcutAdvisor();
        defaultPointcutAdvisor.setAdvice(transactionAdvice());
        defaultPointcutAdvisor.setPointcut(nameMatchMethodPointcut());

        return defaultPointcutAdvisor;
    }

    @Bean
    public ProxyFactoryBean userService() {
        String[] interceptorNames = { "transactionAdvisor" };
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setTarget(transactionAdvice());
        proxyFactoryBean.setInterceptorNames(interceptorNames);

        return proxyFactoryBean;
    }

}
