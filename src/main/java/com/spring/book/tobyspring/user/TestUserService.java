//package com.spring.book.tobyspring.user;
//
//import com.spring.book.tobyspring.user.service.UserService;
//
//class TestUserService extends UserService {
//
//    private String id;
//
//    public TestUserService(String id) {
//        this.id = id;
//    }
//
//    static class TestUserServiceException extends RuntimeException {}
//
//    protected void upgradeLevel(User user) {
//        if (user.getId().equals(this.id)) {
//            throw new TestUserServiceException();
//        }
//
//        super.upgradeLevel(user);
//    }
//
//}
