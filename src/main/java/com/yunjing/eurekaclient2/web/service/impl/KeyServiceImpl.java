package com.yunjing.eurekaclient2.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunjing.eurekaclient2.common.base.CmdExeUtil;
import com.yunjing.eurekaclient2.web.entity.Key;
import com.yunjing.eurekaclient2.web.mapper.KeyMapper;
import com.yunjing.eurekaclient2.web.service.KeyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.custom.gm.SM2P256V1Curve;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
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
public class KeyServiceImpl extends ServiceImpl<KeyMapper, Key> implements KeyService {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${user.define.crypto.useToken}")
    public String useToken;

    @Value("${user.define.crypto.keyfile}")
    public String keyfile;

    @Value("${user.define.crypto.password}")
    public String password;

    @Value("${user.define.crypto.alias}")
    public String alias;

    @Value("${user.define.backup-path}")
    private String backupPath;


    @Value("${spring.datasource.username}")
    private String dbUserName;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${user.define.db}")
    private String dbName;


    private ECPrivateKeyParameters param;
    private SM2Signer sm2Signer;

    @Override
    public Key[] saveKey(String userID, String algorithmID, String save) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, CryptoException, UnrecoverableKeyException, CertificateException, KeyStoreException, IOException {

        switch (algorithmID){

            case RSA:
                return generateRSA( userID,save, 2048);
            case SM2:
                return generateSM2(userID,save);
            case AES:
                return generateAES(userID,save);
            case SM4:
                return generateSM4(userID,save);
            default:
                return null;
        }

    }

    private Key[] generateAES(String userID, String save) throws CryptoException, CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, IOException {

        byte[] keydata = getRandom(16);
        byte[] sig = signContent(keydata);
        Key key = getKeys(userID, keydata, sig, null,KEY_AES, save);
        Key[] keys = new Key[1];
        keys[0] = key;
        return keys;
    }

    private Key[] generateSM4(String userID, String save) throws CryptoException, CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, IOException {

        byte[] keydata = getRandom(16);
        byte[] sig = signContent(keydata);
        Key key = getKeys(userID, keydata, sig, null,KEY_SM4, save);
        Key[] keys = new Key[1];
        keys[0] = key;
        return keys;
    }

    @Override
    public Key getKey(String userID, int keyID) {
        QueryWrapper<Key> wrapper = new QueryWrapper<>();
        wrapper.eq("id",keyID);
        Key key = this.getOne(wrapper);
        // List<Key> list = this.list(wrapper);
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
        Key key = getKey(userID,keyID);
        if(key != null){
            return this.removeById(keyID);

        }
        return false;
    }

    @Override
    public String generateRandom(int length) {

        byte[] result = getRandom(length);
        return ByteUtils.toHexString(result);
    }

    @Override
    public String sm2enc(String publicKey, byte[] data) throws IOException, InvalidCipherTextException {
        if (useToken.toLowerCase().contains("false")) {
            byte[] keydata = ByteUtils.fromHexString(publicKey);
            SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(keydata);
            PublicKey pubkey = BouncyCastleProvider.getPublicKey(subjectPublicKeyInfo);
            BCECPublicKey localECPublicKey = (BCECPublicKey)pubkey;
            ECParameterSpec localECParameterSpec = localECPublicKey.getParameters();
            ECDomainParameters localECDomainParameters = new ECDomainParameters(localECParameterSpec.getCurve(),
                    localECParameterSpec.getG(), localECParameterSpec.getN());
            ECPublicKeyParameters param = new ECPublicKeyParameters(localECPublicKey.getQ(),localECDomainParameters);
            ParametersWithRandom parametersWithRandom = new ParametersWithRandom(param);
            SM2Engine sm2Engine = new SM2Engine();
            sm2Engine.init(true,parametersWithRandom);
            byte[] result = sm2Engine.processBlock(data, 0, data.length);
            return Base64.getUrlEncoder().encodeToString(result);
        }else{
            //TODO: token
        }
        return null;
    }

    @Override
    public String sm2dec(String userID, int keyID, byte[] data) throws InvalidCipherTextException {
        Key key = getKey(userID,keyID);
        if(key == null){
            return null;
        }else{
            if (useToken.toLowerCase().contains("false")) {
                byte[] keydata = ByteUtils.fromHexString(key.getContent());
                BigInteger q = new BigInteger(1,keydata);
                ECCurve curve = new SM2P256V1Curve();
                BigInteger SM2_ECC_N = new BigInteger(
                        "FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFF7203DF6B21C6052B53BBF40939D54123", 16);
                BigInteger SM2_ECC_GX = new BigInteger(
                        "32C4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7", 16);
                BigInteger SM2_ECC_GY = new BigInteger(
                        "BC3736A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52139F0A0", 16);
                ECPoint G_POINT = curve.createPoint(SM2_ECC_GX, SM2_ECC_GY);
                ECDomainParameters ecDomainParameters = new ECDomainParameters(curve,G_POINT,SM2_ECC_N  );
                ECPrivateKeyParameters ecKeyParameters = new ECPrivateKeyParameters(q,ecDomainParameters);
                SM2Engine sm2Engine = new SM2Engine();
                sm2Engine.init(false,ecKeyParameters);
                byte[] result = sm2Engine.processBlock(data,0,data.length);
                return Base64.getUrlEncoder().encodeToString(result);
            }else{
                //TODO: token
                return null;
            }
        }
    }

    @Override
    public boolean check() throws NoSuchProviderException, KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        KeyStore pkcs12 = KeyStore.getInstance("PKCS12", "BC");
        pkcs12.load(new FileInputStream(keyfile), password.toCharArray());
        Certificate[] pubCerts = pkcs12.getCertificateChain(alias);

        ECPublicKeyParameters ecPublicKeyParameters = getEcPublicKeyParameters(pubCerts[0]);
        List<Key> errKey = new ArrayList<>();
        List<Key> uncheckKey = new ArrayList<>();
        List<Key> list = this.list();
        Iterator<Key> iterator = list.iterator();
        while (iterator.hasNext()){
            Key key = iterator.next();
            String content = key.getContent();
            String sig = key.getSignature();
            if((content!= null) && (sig != null) ){
                byte[] tbsign = ByteUtils.fromHexString(content);
                byte[] signature = ByteUtils.fromHexString(sig);
                boolean result = varifySM2Signature(tbsign,signature,ecPublicKeyParameters,false);
                if(!result){
                    errKey.add(key);
                    logger.info("key " + key.getId() + " fail integrity check");
                }
            }else{
                uncheckKey.add(key);
                logger.info("key " + key.getId() + " does not check integrity");
            }
        }


        if(errKey.size()>0){
            return false;
        }else{
            return true;
        }
    }

    private   ECPublicKeyParameters getEcPublicKeyParameters(Certificate cert) {
        PublicKey publicKey =cert.getPublicKey();
        ECPublicKeyParameters param = null;
        if (publicKey instanceof BCECPublicKey)
        {
            BCECPublicKey localECPublicKey = (BCECPublicKey)publicKey;
            ECParameterSpec localECParameterSpec = localECPublicKey.getParameters();
            ECDomainParameters localECDomainParameters = new ECDomainParameters(localECParameterSpec.getCurve(),
                    localECParameterSpec.getG(), localECParameterSpec.getN());
            param = new ECPublicKeyParameters(localECPublicKey.getQ(),localECDomainParameters);
        }
        return param;
    }

    public boolean varifySM2Signature(byte[] tbsign,byte[] signature, ECPublicKeyParameters publicKeyParameters, boolean useToken) {

        if(!useToken)
        {

                SM2Signer signer = new SM2Signer();
                signer.init(false, publicKeyParameters);
                signer.update(tbsign, 0, tbsign.length);
                boolean result = signer.verifySignature(signature);
                return result;

        }else{

            //TODO: use usb token
            return false;
        }

    }

    @Override
    public String backup() throws IOException, InterruptedException {
        File fileDir = new File(backupPath);
        if (!isDirExist(fileDir)) {
            fileDir.mkdir();
        }

        String fileName = fileDir.getAbsolutePath() + "kmc" + formatLocalDateTime(LocalDateTime.now()) + ".sql";

         CmdExeUtil.exeCmd(backup(fileName));


        return fileName;
    }

    private   String formatLocalDateTime(LocalDateTime localDateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return localDateTime.format(dateTimeFormatter);
    }

    private  boolean isDirExist(@NonNull File file) {
        return file.exists() && file.isDirectory();
    }

    private String backup(String filePath) {
        //mysqldump -u username -p dbname table1 table2 ...> BackupName.sql
        StringBuilder sb = new StringBuilder();
        sb.append("mysqldump -u ").append(dbUserName);
        sb.append(" -p").append(dbPassword);
        sb.append(" ").append(dbName);
        sb.append(" ").append("key");
        sb.append(" > ").append(filePath);
        return sb.toString();
    }

    private byte[] getRandom(int length){
        if (useToken.toLowerCase().contains("false")) {
            byte[]  b = "abc123".getBytes();
            SecureRandom random = new SecureRandom(b);
            byte[] result = new byte[length];
            random.nextBytes(result);
            return result;
        }else {
            //TODO: token
            return new byte[length];
        }
    }


    private Key[] generateSM2(String userID, String save) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, CryptoException, UnrecoverableKeyException, CertificateException, KeyStoreException, IOException {
        if (useToken.toLowerCase().contains("false")) {
            KeyPairGenerator g = KeyPairGenerator.getInstance("EC", "BC");
            g.initialize(new ECNamedCurveGenParameterSpec("sm2p256v1"));
            KeyPair p = g.generateKeyPair();
            PrivateKey privKey = p.getPrivate();
            PublicKey pubKey = p.getPublic();
            // save into database
            byte[] privdata = privKey.getEncoded();
            byte[] pubdata = pubKey.getEncoded();

            byte[] privsig = signContent(privdata);
            byte[] pubsig = signContent(pubdata);


            Key key = getKeys(userID, pubdata, pubsig, null,KEY_SM2_PUB, save);
            Key pkey = getKeys(userID, privdata, privsig, key,KEY_SM2_PRIV, save);

            Key[] result = new Key[2];
            result[PRIVATE_KEY] = pkey;
            result[PUBLIC_KEY] = key;
            return result;
        }else{
            // TODO: use token
            return null;
        }
    }


    private Key[] generateRSA(String userID, String save, int keysize) throws NoSuchProviderException, NoSuchAlgorithmException, CryptoException, UnrecoverableKeyException, CertificateException, KeyStoreException, IOException {

        KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
        kpGen.initialize(keysize, new SecureRandom());
        KeyPair p = kpGen.generateKeyPair();
        PrivateKey privKey = p.getPrivate();
        PublicKey pubKey = p.getPublic();
            // save into database
        byte[] privdata = privKey.getEncoded();
        byte[] pubdata = pubKey.getEncoded();
        byte[] privsig = signContent(privdata);
        byte[] pubsig = signContent(pubdata);


        Key key = getKeys(userID, pubdata, pubsig, null,KEY_RSA_PUB, save);
        Key pkey = getKeys(userID, privdata, privsig, key,KEY_RSA_PRIV, save);

        Key[] result = new Key[2];
        result[PRIVATE_KEY] = pkey;
        result[PUBLIC_KEY] = key;
        return result;

    }

    private Key getKeys(String userID, byte[] data, byte[] sig, Key key, String keyType, String saveFlag) {
        Key pkey = new Key();
        pkey.setContent(ByteUtils.toHexString(data));
        pkey.setUserId(userID);
        pkey.setKeyType(keyType);
        if(key!= null) {
            pkey.setRelatedKey(key.getId());
        }else{
            pkey.setRelatedKey(1);
        }

        pkey.setSignature(ByteUtils.toHexString(sig));
        if(saveFlag.toLowerCase().equals("true")) {
            pkey.setCreateTime(LocalDateTime.now());
            this.save(pkey);
        }
        return pkey;
    }


    private byte[] signContent(byte[] content) throws CryptoException, NoSuchProviderException, KeyStoreException, IOException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException {
        if(param == null) {
            KeyStore pkcs12 = KeyStore.getInstance("PKCS12", "BC");
            pkcs12.load(new FileInputStream(keyfile), password.toCharArray());
            BCECPrivateKey pk = (BCECPrivateKey)pkcs12.getKey(alias, null);

            ECParameterSpec localECParameterSpec = pk.getParameters();
            ECDomainParameters localECDomainParameters = new ECDomainParameters(localECParameterSpec.getCurve(),
                    localECParameterSpec.getG(), localECParameterSpec.getN());
            param = new ECPrivateKeyParameters(pk.getS(), localECDomainParameters);
            sm2Signer = new SM2Signer();
        }
        sm2Signer.init(true,param);
        sm2Signer.update(content,0,content.length);
        byte[] sig = sm2Signer.generateSignature();
        return sig;
    }
}
