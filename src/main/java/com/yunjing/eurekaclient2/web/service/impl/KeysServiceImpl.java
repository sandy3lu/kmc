package com.yunjing.eurekaclient2.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunjing.eurekaclient2.common.base.ResultInfo;
import com.yunjing.eurekaclient2.web.entity.DictConstant;
import com.yunjing.eurekaclient2.web.entity.Keys;
import com.yunjing.eurekaclient2.web.mapper.KeysMapper;
import com.yunjing.eurekaclient2.web.service.KeysService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.security.*;
import java.util.Base64;
import java.util.List;

/**
 * <p>
 * key管理表 服务实现类
 * </p>
 *
 * @author scyking-auto
 * @since 2019-01-28
 */
@Service
public class KeysServiceImpl extends ServiceImpl<KeysMapper, Keys> implements KeysService {

    @Value("${user.define.crypto.useToken}")
    public String useToken;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Keys[] saveKey(String userID,String algorithmID,String save) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, CryptoException {

        switch (algorithmID){

            case "RSA2048":
                return generateRSA( userID,save, 2048);
            case "SM2":
                return generateSM2(userID,save);
            case "AES":

                return resultInfo;

            case "SM4":

                return resultInfo;
            default:
                return null;
        }




    }

    @Override
    public Keys getKey(String userID, int keyID) {
        QueryWrapper<Keys> wrapper = new QueryWrapper<>();
        wrapper.eq("id",keyID);
        Keys key = this.getOne(wrapper);
        // List<Keys> list = this.list(wrapper);
        if(key == null){
            return null;
        }
        if(key.getUserId().equals(userID)){
            return key;
        }else{
            return null;
        }

    }

    @Override
    public boolean deleteKey(String userID, int keyID) {
        Keys key = getKey(userID,keyID);
        if(key != null){
            return this.removeById(keyID);

        }
        return false;
    }

    @Override
    public String generateRandom(int length) {

        if (useToken.toLowerCase().contains("false")) {
            byte[]  b = "abc123".getBytes();
            SecureRandom random = new SecureRandom(b);
            byte[] result = new byte[length];
            random.nextBytes(result);
            String s = Base64.getEncoder().encodeToString(result);
            return s;

        }else {
            //TODO: token
            return "000000";
        }

    }


    private Keys[] generateSM2(String userID,String save) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, CryptoException {
        if (useToken.toLowerCase().contains("false")) {
            KeyPairGenerator g = KeyPairGenerator.getInstance("EC", "BC");
            g.initialize(new ECNamedCurveGenParameterSpec("sm2p256v1"));
            KeyPair p = g.generateKeyPair();
            PrivateKey privKey = p.getPrivate();
            PublicKey pubKey = p.getPublic();
            // save into database
            byte[] privdata = privKey.getEncoded();
            byte[] pubdata = pubKey.getEncoded();

            ValueOperations<String, BCECPrivateKey> operations = redisTemplate.opsForValue();
            BCECPrivateKey pk = operations.get("pms.kmc.obj.key");
            ECParameterSpec localECParameterSpec = pk.getParameters();
            ECDomainParameters localECDomainParameters = new ECDomainParameters(localECParameterSpec.getCurve(),
                    localECParameterSpec.getG(), localECParameterSpec.getN());
            ECPrivateKeyParameters param = new ECPrivateKeyParameters(pk.getS(),localECDomainParameters);
            SM2Signer sm2Signer = new SM2Signer();
            sm2Signer.init(true,param);
            sm2Signer.update(privdata,0,privdata.length);
            byte[] privsig = sm2Signer.generateSignature();

            sm2Signer.init(true,param);
            sm2Signer.update(pubdata,0,pubdata.length);
            byte[] pubsig = sm2Signer.generateSignature();

            // public key into db
            Keys key = new Keys();
            key.setContent(ByteUtils.toHexString(pubdata));
            key.setUserId(userID);
            key.setKeyType("SM2Pub");
            key.setRelatedKey(0);
            key.setSignature(ByteUtils.toHexString(pubsig));
            this.save(key);

            Keys pkey = new Keys();
            pkey.setContent(ByteUtils.toHexString(privdata));
            key.setUserId(userID);
            pkey.setKeyType("SM2Priv");
            pkey.setRelatedKey(key.getId());
            pkey.setSignature(ByteUtils.toHexString(privsig));
            this.save(pkey);

            Keys[] result = new Keys[2];
            result[0] = pkey;
            result[1] = key;
            return result;
        }else{
            // TODO: use token
            return null;
        }
    }


    private Keys[] generateRSA(String userID,String save, int keysize) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, CryptoException {

        KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
        kpGen.initialize(keysize, new SecureRandom());
        KeyPair p = kpGen.generateKeyPair();

        PrivateKey privKey = p.getPrivate();
        PublicKey pubKey = p.getPublic();
            // save into database
        byte[] privdata = privKey.getEncoded();
        byte[] pubdata = pubKey.getEncoded();

            ValueOperations<String, BCECPrivateKey> operations = redisTemplate.opsForValue();
            BCECPrivateKey pk = operations.get("pms.kmc.obj.key");
            ECParameterSpec localECParameterSpec = pk.getParameters();
            ECDomainParameters localECDomainParameters = new ECDomainParameters(localECParameterSpec.getCurve(),
                    localECParameterSpec.getG(), localECParameterSpec.getN());
            ECPrivateKeyParameters param = new ECPrivateKeyParameters(pk.getS(),localECDomainParameters);
            SM2Signer sm2Signer = new SM2Signer();
            sm2Signer.init(true,param);
            sm2Signer.update(privdata,0,privdata.length);
            byte[] privsig = sm2Signer.generateSignature();

            sm2Signer.init(true,param);
            sm2Signer.update(pubdata,0,pubdata.length);
            byte[] pubsig = sm2Signer.generateSignature();

            // public key into db
            Keys key = new Keys();
            key.setContent(ByteUtils.toHexString(pubdata));
            key.setUserId(userID);
            key.setKeyType("RSA2048Pub");
            key.setRelatedKey(0);
            key.setSignature(ByteUtils.toHexString(pubsig));
            this.save(key);

            Keys pkey = new Keys();
            pkey.setContent(ByteUtils.toHexString(privdata));
            key.setUserId(userID);
            pkey.setKeyType("RSA2048Priv");
            pkey.setRelatedKey(key.getId());
            pkey.setSignature(ByteUtils.toHexString(privsig));
            this.save(pkey);

            Keys[] result = new Keys[2];
            result[0] = pkey;
            result[1] = key;
            return result;

    }
}
