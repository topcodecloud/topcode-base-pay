package cloud.topcode.open.pay.entity;

import cloud.topcode.open.pay.plugin.wechatpay.entity.PayQueryReturn;
import com.alipay.api.response.AlipayTradeQueryResponse;
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
public class PayQueryResult implements Serializable {

    /**
     * 通道
     */
    private PayChannel payChannel;

    /**
     * 订单号
     */
    private String outTradeNo;
    /**
     * 付款渠道订单号
     */
    private String tradeNo;
    /**
     * 订单状态
     */
    private PayStatus payStatus;
    /**
     * 总金额（分）
     */
    private int totalAmount;
    /**
     * 总金额（小数模式）
     */
    private String totalAmountMoney;
    /**
     * 客户付款金额（分）
     * 总金额 - 付款金额 =其他优惠抵扣金额
     */
    private int payAmount;
    /**
     * 客户付款金额（小数格式）
     */
    private String payAmountMoney;
    /**
     * 付款时间
     */
    private String successTime;
    /**
     * 支付宝结果
     */
    private AlipayTradeQueryResponse alipayResult;
    /**
     * 微信结果
     */
    private PayQueryReturn wechatResult;
}
