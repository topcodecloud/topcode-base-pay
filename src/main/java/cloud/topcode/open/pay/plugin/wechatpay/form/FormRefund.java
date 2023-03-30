package cloud.topcode.open.pay.plugin.wechatpay.form;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Jon
 * url: <a href="https://jon.wiki">Jon's blog</a>
 * url: <a href="https://topcode.cloud">topcode.cloud</a>
 */
@Data
public class FormRefund implements Serializable {
    /**
     * 商户订单号
     */
    private String outTradeNo;
    /**
     * 退款原因
     */
    private String reason;
}
