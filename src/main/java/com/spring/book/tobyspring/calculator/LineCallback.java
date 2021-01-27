package com.spring.book.tobyspring.calculator;

public interface LineCallback<T> {

    T doSomethingWithLine(String line, T value);

}
