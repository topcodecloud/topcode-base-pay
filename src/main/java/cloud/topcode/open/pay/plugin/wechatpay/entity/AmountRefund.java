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
public class AmountRefund implements Serializable {
    /**
     * 退款金额
     * int
     * 必
     */
    private int refund;
    /**
     * 退款币种
     * string[1,16]
     * 是：CNY
     */
    private String currency;

    /**
     * 原订单金额
     * long
     * 必
     */
    private int total;

    // todo 要增加 from

}
