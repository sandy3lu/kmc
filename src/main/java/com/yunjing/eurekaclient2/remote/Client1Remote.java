package com.yunjing.eurekaclient2.remote;

import com.yunjing.eurekaclient2.hystrix.Client1RemoteHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 服务远程调用
 *
 * <p>
 *
 * @FeignClient name值为注册中心注册服务名，fallback为请求失败回调类
 * </p>
 */
@FeignClient(name = "service-provider", fallback = Client1RemoteHystrix.class)
public interface Client1Remote {

    /**
     * 请求形式（地址、参数、返回值类型）需与接口提供方保持一致，具有映射关系
     *
     * @return
     */
    @GetMapping("/test")
    String test();
}
