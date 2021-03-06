package com.yunjing.eurekaclient2.feign.hystrix;

import com.yunjing.eurekaclient2.feign.remote.Client1Remote;
import org.springframework.stereotype.Component;

/**
 * @ClassName Client1RemoteHystrix
 * @Description 回调类
 * @Author scyking
 * @Date 2019/1/21 16:23
 * @Version 1.0
 */
@Component
public class Client1RemoteHystrix implements Client1Remote {
    @Override
    public String test() {
        return "Client1Remote, this message send failed !";
    }
}
