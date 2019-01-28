package com.yunjing.eurekaclient2.web.service;

import com.yunjing.eurekaclient2.web.entity.Keys;
import com.baomidou.mybatisplus.extension.service.IService;
import org.bouncycastle.crypto.CryptoException;

import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * <p>
 * key管理表 服务类
 * </p>
 *
 * @author scyking-auto
 * @since 2019-01-28
 */
public interface KeysService extends IService<Keys> {

    Keys[] saveKey(String userID, String algorithmID, String save) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, CryptoException;

    Keys getKey(String userID, int keyID);

    boolean deleteKey(String userID, int keyID);

    String generateRandom(int length);

}
