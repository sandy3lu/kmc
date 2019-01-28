package com.yunjing.eurekaclient2.web.controller;


import com.yunjing.eurekaclient2.common.base.ResultInfo;
import com.yunjing.eurekaclient2.web.entity.Keys;
import com.yunjing.eurekaclient2.web.service.KeysService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * <p>
 * key管理表 前端控制器
 * </p>
 *
 * @author scyking-auto
 * @since 2019-01-28
 */
@RestController
@RequestMapping("/v1.0")
@Api("key管理")
public class KeysController {

    @Autowired
    KeysService keysService;

    @PostMapping("/key")
    @ApiOperation("产生密钥")
    public ResultInfo generateKey(@RequestParam(name="userID") String userID,@RequestParam(name="algorithmID") String algorithmID,@RequestParam(name="save") String save){
        try {
            Keys[] key = keysService.saveKey(userID, algorithmID, save);
            ResultInfo resultInfo = ResultInfo.ok();
            switch (algorithmID){
                case "SM2":
                case "RSA2048":
                    resultInfo.put("publicKey", key[1].getContent());
                    resultInfo.put("privatekeyID",key[0].getId());
                    return resultInfo;
                case "SM4":
                case "AES":
                    resultInfo.put("key",key[0].getContent());
                    resultInfo.put("keyID",key[0].getId());
                    return resultInfo;
                    default:
                        return ResultInfo.error("not support algorithmID " + algorithmID);
            }

        }catch (Exception e){
            ResultInfo resultInfo = ResultInfo.error(e.getMessage());
            return resultInfo;
        }

    }

    @GetMapping("/key")
    @ApiOperation("获取密钥")
    public ResultInfo getKey(@RequestParam(name="userID") String userID,@RequestParam(name="keyID") int keyID){
        Keys key = keysService.getKey(userID, keyID);
        if(key == null){
            return ResultInfo.error("could not find keyID or userID is not the owner");
        }
        String keytype = key.getKeyType();
        switch (keytype){
            case "SM4":
            case "AES":
                return ResultInfo.ok().put("key",key.getContent());
            case "SM2Pub":
            case "RSA2048Pub":
                return ResultInfo.ok().put("publicKey",key.getContent());
            case "SM2Priv":
            case "RSA2048Priv":
                ResultInfo resultInfo = ResultInfo.ok();
                resultInfo.put("privateKey",key.getContent());
                Keys pubkey = keysService.getKey(userID, key.getRelatedKey());
                if(pubkey == null){
                    return ResultInfo.error("could not find publickey for keyID " + keyID);
                }
                resultInfo.put("publicKey",pubkey.getContent());
                return resultInfo;
                default:
                    return ResultInfo.error("not support keytype " + keytype);
        }

    }

    @DeleteMapping("/key")
    @ApiOperation("删除密钥")
    public ResultInfo deleteKey(@RequestParam(name="userID") String userID,@RequestParam(name="keyID") int keyID){
        boolean result = keysService.deleteKey(userID, keyID);
        if(result){
            return ResultInfo.ok();
        }
        return ResultInfo.error("failed");
    }

    @PostMapping("/random")
    @ApiOperation("产生随机数")
    public ResultInfo generateRandom(@RequestParam(name="length") int length){
        String result = keysService.generateRandom(length);

        return ResultInfo.ok().put("data",result);
    }

}
