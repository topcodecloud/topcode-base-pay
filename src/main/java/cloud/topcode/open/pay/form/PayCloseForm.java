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
public class PayCloseForm implements Serializable {
    /**
     * 支付通道
     */
    @NotNull
    private PayChannel payChannel;
    /**
     * 用户订单号
     */
    @NotNull
    private String outTradeNo;
}
