package cloud.topcode.open.pay.plugin.wechatpay.util;

import cloud.topcode.open.pay.plugin.wechatpay.constant.WechatConstant;
import cloud.topcode.open.pay.plugin.wechatpay.constant.WechatMessage;
import cloud.topcode.open.pay.plugin.wechatpay.entity.WechatCertificate;
import cloud.topcode.open.pay.plugin.wechatpay.entity.WechatEncryptCertificate;
import cloud.topcode.open.pay.util.BaseUtils;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jon
 * url: <a href="https://jon.wiki">Jon's blog</a>
 * url: <a href="https://topcode.cloud">topcode.cloud</a>
 */
public class WechatUtils {

    /**
     * 从证书文件中读取证书读取系列号
     *
     * @return String
     * @throws Exception
     */
    public static String getSerialNumber(String certPath,String mchid) throws Exception {
        return getCertificate(certPath, mchid).getSerialNumber().toString(16).toUpperCase();
    }

    /**
     * 从证书文件中读取证书读取私钥
     * @param certPath 证书路径
     * @param mchid 商户号
     * @return PrivateKey
     * @throws Exception
     */
    public static PrivateKey getPrivateKeyByFileName(String certPath,String mchid) throws Exception {
        KeyStore ks = getKeyStore(certPath, mchid);
        PrivateKey privateKey = (PrivateKey) ks.getKey(WechatConstant.KEY_ALIAS, mchid.toCharArray());
        return privateKey;
    }


    /**
     * 从证书文件中获取KeyStore
     * @param certPath 证书路径
     * @param mchid 商户号
     * @return KeyStore
     * @throws KeyStoreException
     * @throws CertificateException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    private static KeyStore getKeyStore(String certPath,String mchid) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        BufferedInputStream bis = new BufferedInputStream(BaseUtils.getStream(certPath));
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(bis, mchid.toCharArray());
        return ks;
    }

    /**
     * 从证书文件中获取KeyAlias
     *
     * @param certPath 证书路径
     * @param mchid 商户号
     * @return String
     * @throws KeyStoreException
     * @throws CertificateException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    private static String getKeyAlias(String certPath,String mchid) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        BufferedInputStream bis = new BufferedInputStream(BaseUtils.getStream(certPath));
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(bis, mchid.toCharArray());
        String keyAlias = null;
        Enumeration<String> aliases = ks.aliases();
        if (aliases.hasMoreElements()) {
            keyAlias = aliases.nextElement();
        }
        return keyAlias;
    }

    /***
     * 从证书文件中读取证书
     * @return X509Certificate
     * @throws Exception
     */
    public static X509Certificate getCertificate(String certPath,String mchid) throws Exception {
        KeyStore ks = getKeyStore(certPath,mchid);
        Certificate cert = ks.getCertificate(WechatConstant.KEY_ALIAS);
        return (X509Certificate) cert;
    }

    /**
     * 从私钥文件读取私钥
     *
     * @param filename
     * @return PrivateKey
     * @throws IOException
     */
    public static PrivateKey getPrivateKeyByFileName(String filename) throws IOException {
        String content = new String(BaseUtils.getBytes(filename), StandardCharsets.UTF_8);
        try {
            String privateKey = content.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            KeyFactory kf = KeyFactory.getInstance(WechatConstant.SIGN_TYPE_RSA);
            return kf.generatePrivate(
                    new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(WechatMessage.WECHAT_EXCEPTION_RSA_ERROR, e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(WechatMessage.WECHAT_EXCEPTION_KEY_SPEC_ERROR);
        }
    }

    /**
     * 获取平台证书Map
     *
     * @param mchid 商户号
     * @param serialNo 证书系列号
     * @param wechatPrivateKey 私钥
     * @param wechatV3key v3key
     * @return map
     * @throws ParseException
     * @throws CertificateException
     */
    public static Map<String, X509Certificate> refreshCertificate(String mchid,String serialNo,PrivateKey wechatPrivateKey,String wechatV3key ) throws Exception {
        JSONObject jsonObject = WechatHttp.httpGet(WechatConstant.URL_CERTIFICATES, "", JSONObject.class,mchid,serialNo,wechatPrivateKey);
        List<WechatCertificate> certificateList = JSON.parseArray(jsonObject.getString("data"), WechatCertificate.class);
        WechatCertificate newestCertificate = null;
        Date newestTime = null;
        for (WechatCertificate certificate : certificateList) {
            SimpleDateFormat formatter = new SimpleDateFormat(WechatConstant.DATE_FORMAT_WITH_3339);
            if (newestTime == null) {
                newestCertificate = certificate;
                newestTime = formatter.parse(certificate.getEffectiveTime());
            } else {
                Date effectiveTime = formatter.parse(certificate.getEffectiveTime());
                if (effectiveTime.getTime() > newestTime.getTime()) {
                    newestCertificate = certificate;
                }
            }
        }
        WechatEncryptCertificate encryptCertificate = newestCertificate.getWechatEncryptCertificate();
        String publicKey = decryptToString(encryptCertificate.getAssociatedData(), encryptCertificate.getNonce(),
                encryptCertificate.getCiphertext(),wechatV3key);
        CertificateFactory cf = CertificateFactory.getInstance(WechatConstant.CERTIFICATE_TYPE);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(publicKey.getBytes(StandardCharsets.UTF_8));
        X509Certificate certificate = null;
        try {
            certificate = (X509Certificate) cf.generateCertificate(inputStream);
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        Map<String, X509Certificate> certificateMap = new ConcurrentHashMap<>();
        certificateMap.clear();
        String serialNumber = newestCertificate.getSerialNo();
        certificateMap.put(serialNumber, certificate);
        return certificateMap;
    }


    /**
     * 使用v3key进行AES解密
     *
     * @param associatedData associatedData
     * @param nonce nonce
     * @param ciphertext ciphertext
     * @param wechatV3key v3key
     * @return 解密字符串
     * @throws GeneralSecurityException
     */
    public static String decryptToString(String associatedData, String nonce, String ciphertext,String wechatV3key) throws Exception {
        try {
            byte[] associatedDataByte = associatedData.getBytes(StandardCharsets.UTF_8);
            byte[] nonceByte = nonce.getBytes(StandardCharsets.UTF_8);
            byte[] apiV3Key = wechatV3key.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec key = new SecretKeySpec(apiV3Key, WechatConstant.AES_NAME);
            GCMParameterSpec spec = new GCMParameterSpec(WechatConstant.GCM_LENGTH, nonceByte);
            Cipher cipher = Cipher.getInstance(WechatConstant.AES_SETTING);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);
            cipher.updateAAD(associatedDataByte);
            return new String(cipher.doFinal(Base64.getDecoder().decode(ciphertext)), StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new IllegalStateException(e);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new IllegalArgumentException(e);
        } catch (Exception e) {
            throw new Exception(WechatMessage.WECHAT_EXCEPTION_AES_FAIL);
        }
    }
}