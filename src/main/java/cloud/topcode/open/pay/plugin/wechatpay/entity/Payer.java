package cloud.topcode.open.pay.plugin.wechatpay.entity;

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
public class Payer implements Serializable {
    /**
     * 用户在直连商户appid下的唯一标识。 下单前需获取到用户的Openid
     * string[1,128]
     */
    private String openid;
}

