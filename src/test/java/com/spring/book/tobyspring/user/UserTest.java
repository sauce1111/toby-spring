package com.spring.book.tobyspring.user;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserTest {

    User user;

    @Before
    public void setup() {
        user = new User();
    }

    @Test
    public void upgradeLevel() {
        Level[] levels = Level.values();

        for (Level level : levels) {
            if (level.nextLevel() == null) {
                continue;
            }

            user.setLevel(level);
            user.upgradeLevel();

            assertThat(user.getLevel(), is(level.nextLevel()));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotUpgradeLevel() {
        Level[] levels = Level.values();
        for (Level level : levels) {
            if (level.nextLevel() != null) {
                continue;
            }

            user.setLevel(level);
            user.upgradeLevel();
        }
    }

}