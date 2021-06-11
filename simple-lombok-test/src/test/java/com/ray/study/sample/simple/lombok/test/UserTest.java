package com.ray.study.sample.simple.lombok.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * description
 *
 * @author r.shi 2021/5/27 17:55
 */
public class UserTest {

    @Test
    public void testUser() {
        User user = new User();
        user.setId(1L);
        user.setName("tom");
        System.out.println(user.getName());
        Assertions.assertEquals("tom", user.getName());
    }

}