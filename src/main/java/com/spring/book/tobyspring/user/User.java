package com.spring.book.tobyspring.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    String id;
    String name;
    String password;

    Level level;
    int login;
    int recommend;

    public void upgradeLevel() {
        Level nextLevel = this.level.nextLevel();
        if (nextLevel == null) {
            throw new IllegalArgumentException(this.level + " 은 업그레이드가 불가능합니다.");
        } else {
            this.level = nextLevel;
        }
    }

}
