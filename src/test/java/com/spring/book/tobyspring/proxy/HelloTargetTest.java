package com.spring.book.tobyspring.proxy;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Proxy;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

public class HelloTargetTest {

    @Test
    public void simpleProxy() {
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
    public void proxyFactoryBean() {
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setTarget(new HelloTarget());
        proxyFactoryBean.addAdvice(new UppercaseAdvice());

        Hello proxiedHello = (Hello) proxyFactoryBean.getObject();

        assertThat(proxiedHello.sayHello("seonsuh"), is("HELLO SEONSUH"));
        assertThat(proxiedHello.sayHi("seonsuh"), is("HI SEONSUH"));
        assertThat(proxiedHello.sayThankYou("seonsuh"), is("THANKYOU SEONSUH"));
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

    static class HelloTarget implements Hello {
        public String sayHello(String name) {
            return "Hello " + name;
        }

        public String sayHi(String name) {
            return "Hi " + name;
        }

        public String sayThankYou(String name) {
            return "ThankYou " + name;
        }
    }

    @Test
    public void pointcutAdvice() {
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setTarget(new HelloTarget());

        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("sayH*");
        proxyFactoryBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));

        Hello proxiedHello = (Hello) proxyFactoryBean.getObject();

        assertThat(proxiedHello.sayHello("seonsuh"), is("HELLO SEONSUH"));
        assertThat(proxiedHello.sayHi("seonsuh"), is("HI SEONSUH"));
        assertThat(proxiedHello.sayThankYou("seonsuh"), is("ThankYou seonsuh"));
    }

    @Test
    public void classNamePointcutAdvisor() {
        // 포인트컷 준비
        NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut() {
            public ClassFilter getClassFilter() {
                return clazz -> clazz.getSimpleName().startsWith("HelloT");
            }
        };

        classMethodPointcut.setMappedName("sayH*");

        // 테스트
        checkAdviced(new HelloTarget(), classMethodPointcut, true);

        class HelloWorld extends HelloTarget{};
        checkAdviced(new HelloWorld(), classMethodPointcut, false);

        class HelloToby extends HelloTarget{};
        checkAdviced(new HelloToby(), classMethodPointcut, true);
    }

    private void checkAdviced(Object target, Pointcut pointcut, boolean adviced) {
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setTarget(target);
        proxyFactoryBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));

        Hello proxiedHello = (Hello) proxyFactoryBean.getObject();

        if (adviced) {
            assertThat(proxiedHello.sayHello("seonsuh"), is("HELLO SEONSUH"));
            assertThat(proxiedHello.sayHi("seonsuh"), is("HI SEONSUH"));
            assertThat(proxiedHello.sayThankYou("seonsuh"), is("ThankYou seonsuh"));
        } else {
            assertThat(proxiedHello.sayHello("seonsuh"), is("Hello seonsuh"));
            assertThat(proxiedHello.sayHi("seonsuh"), is("Hi seonsuh"));
            assertThat(proxiedHello.sayThankYou("seonsuh"), is("ThankYou seonsuh"));
        }
    }

}
