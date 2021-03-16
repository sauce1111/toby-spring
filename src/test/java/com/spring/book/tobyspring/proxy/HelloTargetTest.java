package com.spring.book.tobyspring.proxy;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.is;

import java.lang.reflect.Proxy;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactoryBean;

class HelloTargetTest {

    @Test
    void simpleProxy() {
//        Hello proxiedHello = new HelloUppercase(new HelloTarget());

        // 다이나믹 프록시 생성
        Hello proxiedHello = (Hello) Proxy.newProxyInstance(
            getClass().getClassLoader(),
            new Class[] {Hello.class},
            new UppercaseHandler(new HelloTargetTest())
        );

        assertThat(proxiedHello.sayHello("seonsuh"), is("HELLOSEONSUH"));
        assertThat(proxiedHello.sayHi("seonsuh"), is("HISEONSUH"));
        assertThat(proxiedHello.sayThankYou("seonsuh"), is("THANKYOUSEONSUH"));
    }

    @Test
    void proxyFactoryBean() {
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setTarget(new HelloTargetTest());
        proxyFactoryBean.addAdvice(new UppercaseAdvice());

        Hello proxiedHello = (Hello) proxyFactoryBean.getObject();

        assertThat(proxiedHello.sayHello("seonsuh"), is("HELLOSEONSUH"));
        assertThat(proxiedHello.sayHi("seonsuh"), is("HISEONSUH"));
        assertThat(proxiedHello.sayThankYou("seonsuh"), is("THANKYOUSEONSUH"));
    }

    static class UppercaseAdvice implements MethodInterceptor {

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            String ret = (String) invocation.proceed();

            return ret.toUpperCase();
        }

    }

    static interface Hello {
        String sayHello(String name);
        String sayHi(String name);
        String sayThankYou(String name);
    }

}
