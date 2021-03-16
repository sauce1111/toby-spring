package com.spring.book.tobyspring.learningtest;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.is;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

class ReflectionTest {

    @Test
    void invokeMethod() throws Exception {
        String name = "spring";

        assertThat(name.length(), is(6));

        Method lengthMethod = String.class.getMethod("length");
        assertThat((Integer) lengthMethod.invoke(name), is(6));

        assertThat(name.charAt(0), is('s'));

        Method charAtMethod = String.class.getMethod("charAt", int.class);
        assertThat((Character) charAtMethod.invoke(name, 0), is('s'));
    }
}
