package com.spring.book.tobyspring.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String id;
    private String name;
    private String password;

    private Level level;
    private int login;
    private int recommend;

    private String email;

    public void upgradeLevel() {
        Level nextLevel = this.level.nextLevel();
        if (nextLevel == null) {
            throw new IllegalArgumentException(this.level + " 은 업그레이드가 불가능합니다.");
        } else {
            this.level = nextLevel;
        }
    }

    @Builder
    public User(String id, String name, String password, Level level, int login, int recommend) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.level = level;
        this.login = login;
        this.recommend = recommend;
    }

}
