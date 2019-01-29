package com.yunjing.eurekaclient2.web.service;

import com.yunjing.eurekaclient2.web.entity.Key;
import com.baomidou.mybatisplus.extension.service.IService;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.InvalidCipherTextException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * <p>
 * key管理表 服务类
 * </p>
 *
 * @author scyking-auto
 * @since 2019-01-28
 */
public interface KeyService extends IService<Key> {

    String SM4="SM4";
    String SM2="SM2";
    String RSA = "RSA2048";
    String AES = "AES128";

    int PRIVATE_KEY = 0;
    int PUBLIC_KEY  = 1;

    String KEY_SM4="SM4";
    String KEY_SM2_PUB="SM2Pub";
    String KEY_SM2_PRIV="SM2Priv";
    String KEY_RSA_PUB = "RSA2048Pub";
    String KEY_RSA_PRIV = "RSA2048Priv";
    String KEY_AES = "AES128";

    Key[] saveKey(String userID, String algorithmID, String save) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, CryptoException, UnrecoverableKeyException, CertificateException, KeyStoreException, IOException;

    Key getKey(String userID, int keyID);

    boolean deleteKey(String userID, int keyID);

    String generateRandom(int length);

    String sm2enc(String publicKey, byte[] data) throws IOException, InvalidCipherTextException;


    String sm2dec(String userID, int keyID, byte[] data) throws InvalidCipherTextException, IOException;

    boolean check() throws NoSuchProviderException, KeyStoreException, IOException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException;

    String backup() throws IOException, InterruptedException;
}
