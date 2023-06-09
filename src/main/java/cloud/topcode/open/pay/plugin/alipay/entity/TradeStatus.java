package cloud.topcode.open.pay.plugin.alipay.entity;

/**
 * @author Jon
 * url: <a href="https://jon.wiki">Jon's blog</a>
 * url: <a href="https://topcode.cloud">topcode.cloud</a>
 */
public enum TradeStatus {

    /**
     * 付款成功
     */
    TRADE_SUCCESS(1, "付款成功"),
    /**
     * 交易完成
     */
    TRADE_FINISHED(2, "交易完成"),
    /**
     * 等待支付
     */
    WAIT_BUYER_PAY(3, "等待支付"),
    /**
     * 交易关闭
     */
    TRADE_CLOSED(4, "交易关闭");
    private Integer code;
    private String name;

    private TradeStatus(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
