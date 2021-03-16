//package com.spring.book.tobyspring.user.service;
//
//import com.spring.book.tobyspring.user.User;
//import lombok.RequiredArgsConstructor;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.TransactionStatus;
//import org.springframework.transaction.support.DefaultTransactionDefinition;
//
//@RequiredArgsConstructor
//public class UserServiceTx implements UserService {
//
//    private final UserService userService;
//
//    private final PlatformTransactionManager transactionManager;
//
//    @Override
//    public void add(User user) {
//        userService.add(user);
//    }
//
//    @Override
//    public void upgradeLevels() {
//        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
//        try {
//            userService.upgradeLevels();
//            transactionManager.commit(status);
//        } catch (RuntimeException e) {
//            transactionManager.rollback(status);
//            throw e;
//        }
//    }
//
//}
