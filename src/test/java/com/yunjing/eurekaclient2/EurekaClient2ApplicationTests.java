package com.yunjing.eurekaclient2;

import com.yunjing.eurekaclient2.provider.IMessageProvider;
import com.yunjing.eurekaclient2.web.entity.DictConstant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EurekaClient2ApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Resource
    private IMessageProvider messageProvider;

    @Test
    public void testSend() {
        DictConstant dictConstant = new DictConstant();
        dictConstant.setId(1); // 继承的属性值不发送
        dictConstant.setValue("hello world !");
        this.messageProvider.send(dictConstant); // 消息发送
    }

}

