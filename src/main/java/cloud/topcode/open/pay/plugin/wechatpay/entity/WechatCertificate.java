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
public class WechatCertificate implements Serializable {
    /**
     * 平台证书序列号
     */
    @JSONField(name = "serial_no")
    private String serialNo;

    /**
     * 平台证书有效时间
     */
    @JSONField(name = "effective_time")
    private String effectiveTime;

    /**
     * 平台证书过期时间
     */
    @JSONField(name = "expire_time")
    private String expireTime;

    /**
     * 加密证书
     */
    @JSONField(name = "encrypt_certificate")
    private WechatEncryptCertificate wechatEncryptCertificate;
}
