package com.spring.book.tobyspring.factorybean;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FactoryBeanTest {

    @Autowired
    private ApplicationContext context;

    @Test
    public void getMessageFromFactoryBean() {
        Object message = context.getBean("message");
        assertThat(message instanceof Message, is(true));
        assertThat(((Message) message).getText(), is("Factory Bean"));
    }

    @Test
    public void getFactoryBean() {
        Object factory = context.getBean("&message");
        assertThat(factory instanceof MessageFactoryBean, is(true));
    }

}
