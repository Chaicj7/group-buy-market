package com.chaicj.test.infrastructure.dao;

import com.chaicj.infrastructure.dao.GroupBuyDiscountDao;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class GroupBuyDiscountDaoTest {

    @Resource
    private GroupBuyDiscountDao groupBuyDiscountDao;

    @Test
    public void queryAll() {
        log.info("queryAll: {}", groupBuyDiscountDao.queryAll());
    }
}
