package com.yunjing.eurekaclient2.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @ClassName InitApplicationRunner
 * @Description 系统启动初始化操作
 * @Author scyking
 * @Date 2019/1/23 16:32
 * @Version 1.0
 */
@Component
public class InitApplicationRunner implements ApplicationRunner {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("init...");
        // TODO 初始化操作
        logger.info("init end !");
    }
}
