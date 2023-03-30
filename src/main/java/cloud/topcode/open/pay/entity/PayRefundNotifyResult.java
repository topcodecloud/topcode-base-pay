package cloud.topcode.open.pay.entity;

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
public class PayRefundNotifyResult implements Serializable {
    /**
     * 通道
     */
    private PayChannel payChannel;
    /**
     * 商户订单号
     */
    private String outTradeNo;
    /**
     * 商户退单号
     */
    private String outRefundNo;
    /**
     * 订单金额（分）
     */
    private int totalAmount;
    /**
     * 退款金额（分）
     */
    private int refundAmount;
    /**
     * 订单金额（小数）
     */
    private String totalAmountMoney;
    /**
     * 退款金额（小数）
     */
    private String refundAmountMoney;
}
