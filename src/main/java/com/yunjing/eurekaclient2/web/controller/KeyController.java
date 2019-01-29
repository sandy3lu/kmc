package com.yunjing.eurekaclient2.web.controller;


import com.yunjing.eurekaclient2.common.base.ResultInfo;
import com.yunjing.eurekaclient2.web.entity.Key;
import com.yunjing.eurekaclient2.web.service.KeyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
public class KeyController {

    @Autowired
    KeyService keyService;

//    @Autowired
//    private HttpServletRequest request;
//
//    private String getUserID(HttpServletRequest request){
//        String s = request.getHeader("UserId");
//        if(s == null){
//            throw new RuntimeException("missing UserId in Http Header");
//        }
//        return s;
//    }
//
//    private String getUserType(HttpServletRequest request){
//        String s = request.getHeader("UserType");
//        if(s == null){
//            throw new RuntimeException("missing UserId in Http Header");
//        }
//        return s;
//    }

    @PostMapping("/key")
    @ApiOperation("产生密钥")
    public ResultInfo generateKey(@RequestParam("userId")String userID,@RequestParam(name="algorithmID") String algorithmID,@RequestParam(name="save") String save){

          try {
            Key[] key = keyService.saveKey(userID, algorithmID, save);
            ResultInfo resultInfo = ResultInfo.ok();
            switch (algorithmID){
                case KeyService.SM2:
                case KeyService.RSA:
                    resultInfo.put("publicKey", key[KeyService.PUBLIC_KEY].getContent());
                    resultInfo.put("privatekeyID",key[KeyService.PRIVATE_KEY].getId());
                    return resultInfo;
                case KeyService.AES:
                case KeyService.SM4:
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
    public ResultInfo getKey(@RequestParam("userId")String userID,@RequestParam(name="keyID") int keyID){

        Key key = keyService.getKey(userID, keyID);
        if(key == null){
            return ResultInfo.error("could not find keyID or userID is not the owner");
        }
        String keytype = key.getKeyType();
        switch (keytype){
            case KeyService.KEY_SM4:
            case KeyService.KEY_AES:
                return ResultInfo.ok().put("key",key.getContent());
            case KeyService.KEY_SM2_PUB:
            case KeyService.KEY_RSA_PUB:
                return ResultInfo.ok().put("publicKey",key.getContent());
            case KeyService.KEY_SM2_PRIV:
            case KeyService.KEY_RSA_PRIV:
                ResultInfo resultInfo = ResultInfo.ok();
                resultInfo.put("privateKey",key.getContent());
                Key pubkey = keyService.getKey(userID, key.getRelatedKey());
                if(pubkey == null){
                    return ResultInfo.error("could not find public key for keyID " + keyID);
                }
                resultInfo.put("publicKey",pubkey.getContent());
                return resultInfo;
                default:
                    return ResultInfo.error("not support type " + keytype);
        }

    }

    @DeleteMapping("/key")
    @ApiOperation("删除密钥")
    public ResultInfo deleteKey(@RequestParam("userId")String userID,@RequestParam(name="keyID") int keyID){

        boolean result = keyService.deleteKey(userID, keyID);
        if(result){
            return ResultInfo.ok();
        }
        return ResultInfo.error("failed");
    }

    @PostMapping("/random")
    @ApiOperation("产生随机数")
    public ResultInfo generateRandom(@RequestParam(name="length") int length){
        String result = keyService.generateRandom(length);

        return ResultInfo.ok().put("data",result);
    }


    @PostMapping("/sm2decryption")
    @ApiOperation("SM2私钥解密")
    public ResultInfo sm2dec(@RequestParam("userId")String userID,@RequestParam(name="keyID") int keyID,@RequestParam(name="content") String content){



        byte[] data = Base64.getUrlDecoder().decode(content);
        try{
            String result = keyService.sm2dec(userID,keyID,data);
            return ResultInfo.ok().put("data",result);
        }catch (Exception e){
            return ResultInfo.error("failed " + e.getMessage());
        }
    }

    @PostMapping("/sm2encryption")
    @ApiOperation("SM2公钥加密")
    public ResultInfo sm2enc(@RequestParam(name="publicKey") String publicKey,@RequestParam(name="content") String content){
        byte[] data = Base64.getUrlDecoder().decode(content);
        try {
            String result = keyService.sm2enc(publicKey,data);
            return ResultInfo.ok().put("data",result);
        }catch (Exception e){
            return ResultInfo.error("failed " + e.getMessage());
        }

    }

    @GetMapping("/integrity")
    @ApiOperation("检查密钥库的完整性")
    public ResultInfo checkDataIntegrity(@RequestParam("userType")String userType){
        //String user = getUserType(request);
        if(!userType.toUpperCase().equals("OPERATOR")){
            return ResultInfo.error(" not authorized");
        }
        try {
            boolean result = keyService.check();
            return ResultInfo.ok().put("integrity", result);
        }catch (Exception e){
            e.printStackTrace();
            return ResultInfo.error("failed " + e.getMessage());
        }
    }

    @GetMapping("/backup")
    @ApiOperation("备份密钥库")
    public ResultInfo backup(@RequestParam("userType")String userType){

        if(!userType.toUpperCase().equals("OPERATOR")){
            return ResultInfo.error(" not authorized");
        }
        try {
            String result = keyService.backup();
            return ResultInfo.ok().put("backup", result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultInfo.error("failed " + e.getMessage());
        }

    }

}
