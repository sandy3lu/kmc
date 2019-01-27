package com.yunjing.eurekaclient2.web.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.yunjing.eurekaclient2.remote.Client1Remote;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName TestController
 * @Description 测试控制器
 * @Author scyking
 * @Date 2019/1/20 14:19
 * @Version 1.0
 */
@RestController
@RequestMapping
@Api("测试接口")
public class TestController {

    @Autowired
    Client1Remote client1Remote;

    @GetMapping("/client1/test")
    @ApiOperation("测试远程调用")
    public Object client1() {
        Object obj = client1Remote.test();
        return obj.toString();
    }

    @GetMapping("/obj")
    @HystrixCommand(fallbackMethod = "getObjectStrFallback")
    public Object getObjectStr() {
        return 1 / 0;
    }

    public String getObjectStrFallback() {
        return "getObjectStr failed !";
    }

}
