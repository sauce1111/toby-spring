package com.spring.book.tobyspring.message;

import com.spring.book.tobyspring.connection.ConnectionMaker;

public class MessageDao {

    private ConnectionMaker connectionMaker;

    public MessageDao(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }

}
