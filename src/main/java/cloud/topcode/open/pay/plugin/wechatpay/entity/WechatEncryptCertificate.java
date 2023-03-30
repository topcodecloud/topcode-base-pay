package cloud.topcode.open.pay.plugin.wechatpay.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author Jon
 * url: <a href="https://jon.wiki">Jon's blog</a>
 * url: <a href="https://topcode.cloud">topcode.cloud</a>
 */
@Data
@Accessors(chain = true)
public class WechatEncryptCertificate implements Serializable {
    /**
     * 算法
     */
    private String algorithm;

    /**
     * 随机字符串
     */
    private String nonce;

    /**
     * 相关数据
     */
    @JSONField(name = "associated_data")
    private String associatedData;

    /**
     * 密文
     */
    private String ciphertext;
}

