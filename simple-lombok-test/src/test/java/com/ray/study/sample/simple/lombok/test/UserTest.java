package com.ray.study.sample.simple.lombok.test;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * description
 *
 * @author r.shi 2021/5/27 17:55
 */
public class UserTest {

    @Test
    public void testUser(){
        User user = new User();
        user.setId(1L);
        user.setName("tom");
        System.out.println(user.getName());
    }

}