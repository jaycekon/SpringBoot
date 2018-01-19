package com.jaycekon.demo.mapper;

import com.jaycekon.demo.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Mybatis 测试工具类
 *
 * 2018/1/19 10:03
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserMapperTest {
    @Autowired
    private UserMapper mapper;

    @Test
    public void testSelect() {
        User user = mapper.selectByIdCard("440182199512042311");
        Assert.assertNotNull(user);
    }
}
