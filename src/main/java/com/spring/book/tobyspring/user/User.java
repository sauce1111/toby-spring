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

}
