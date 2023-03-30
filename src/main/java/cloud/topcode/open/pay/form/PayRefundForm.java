package cloud.topcode.open.pay.form;

import cloud.topcode.open.pay.entity.PayChannel;
import lombok.Data;
import lombok.experimental.Accessors;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Jon
 * url: <a href="https://jon.wiki">Jon's blog</a>
 * url: <a href="https://topcode.cloud">topcode.cloud</a>
 */
@Data
@Accessors(chain = true)
public class PayRefundForm implements Serializable {
    /**
     * 支付通道
     */
    @NotNull
    private PayChannel payChannel;
    /**
     * 商户订单号
     */
    @NotNull
    private String outTradeNo;
    /**
     * 退款单号，不指定则系统生成
     * 对应支付宝的 out_request_no
     * 对应微信的 out_refund_no
     */
    private String outRefundNo;
    /**
     * 退款金额（分）
     */
    @NotNull
    private int refundAmount;
    /**
     * 订单总金额（分）
     */
    @NotNull
    private int orderAmount;
    /**
     * 退款原因
     */
    private String refundReason;

}
