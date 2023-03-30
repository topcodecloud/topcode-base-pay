package cloud.topcode.open.pay.plugin.wechatpay.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.security.PrivateKey;

/**
 * @author Jon
 * url: <a href="https://jon.wiki">Jon's blog</a>
 * url: <a href="https://topcode.cloud">topcode.cloud</a>
 */
@Data
@Accessors(chain = true)
public class WechatBaseParam implements Serializable {
    private String appId;
    private String mchid;
    private String appSecret;
    private String certPath;
    private String certKey;
    private String v3key;
    private String serialNo;
    private PrivateKey wechatPrivateKey;
    private String wechatPayNotifyUrl;
    private String wechatRefundNotifyUrl;
    private String wechatCodeReturnUrl;

}
