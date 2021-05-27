package com.ray.study.sample.simple.lombok.test;

import com.ray.study.sample.simple.lombok.annotation.Data;

/**
 * description
 *
 * @author r.shi 2021/5/27 17:16
 */
@Data
public class User {

    private Long id;

    private String name;

    public static void main(String[] args) {
        User user = new User();
        //user.setId(1L);
        System.out.println(user);
    }

}
