package cloud.topcode.open.pay.form;

import cloud.topcode.open.pay.entity.PayChannel;
import cloud.topcode.open.pay.entity.PayType;
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
public class PayRefundQueryForm implements Serializable {
    /**
     * 支付通道
     */
    @NotNull
    private PayChannel payChannel;
    /**
     * 支付类型
     */
    @NotNull
    private PayType payType;
    /**
     * 商户退单号
     */
    @NotNull
    private String outRefundNo;
    /**
     * 商户单号
     */
    @NotNull
    private String outTradeNo;
}
