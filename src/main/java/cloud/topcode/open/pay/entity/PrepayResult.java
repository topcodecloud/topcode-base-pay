package cloud.topcode.open.pay.entity;

import cloud.topcode.open.pay.plugin.wechatpay.entity.MicroParam;
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
public class PrepayResult implements Serializable {

    /**
     * 支付宝和微信公众号扫码的url
     */
    private String url;
    /**
     * 预支付的中文信息
     */
    private String msg;
    /**
     * 微信小程序独有
     */
    private MicroParam param;
}
