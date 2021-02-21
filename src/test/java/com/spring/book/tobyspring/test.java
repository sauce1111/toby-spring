package com.spring.book.tobyspring;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.spring.book.tobyspring.user.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class test {

    @Test
    public void test() {
        User user = mock(User.class);
        assertTrue(user != null);
    }

    @Test
    public void test1() {
        String expectedName = "testName";
        User user = mock(User.class);
        when(user.getName()).thenReturn("testName");

        assertTrue(expectedName.equals(user.getName()));
    }

    @Test
    public void test2() {
        String testName = "testName";
        User user = mock(User.class);
        doNothing().when(user).setName(anyString());
        user.setName(testName);

        // user.setName() 메소드를 String 을 파라미터로 호출 했는지 확인
//        verify(user).setName(anyString());

        // user.setName() 메소드를 String 을 파라미터로 1번 호출 했는지 확인
//        verify(user, times(1)).setName(anyString());

        // user.getName() 메소드를 호출 하지 않았는지 확인
//        verify(user, never()).getName();

        // user.setName() 메소드를 파라미터 "testName" 으로 호출 하지 않았는지 확인 - 실패
//        verify(user, never()).setName(eq(testName));

        // user.setName() 메소드를 파라미터 "test" 으로 호출 하지 않았는지 확인 - 성공
        verify(user, never()).setName(eq("test"));
    }

}
