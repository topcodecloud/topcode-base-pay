package cloud.topcode.open.pay.plugin.alipay.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Jon
 * url: <a href="https://jon.wiki">Jon's blog</a>
 * url: <a href="https://topcode.cloud">topcode.cloud</a>
 */
@Data
@Accessors(chain = true)
public class AlipayBaseParam {
    private String alipayPublicKey ;
    private Boolean hasDev ;
    private Boolean hasEncrypt ;
    private String urlGateway ;
    private String appid ;
    private String appPrivateKey ;
    private String appPublicKey ;
    private String encryptType ;
    private String encryptKey ;
    private String payNotifyUrl;
    private String payReturnUrl ;
}
