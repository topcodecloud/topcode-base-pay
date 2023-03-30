package cloud.topcode.open.pay.entity;

import lombok.Getter;

/**
 * @author Jon
 * url: <a href="https://jon.wiki">Jon's blog</a>
 * url: <a href="https://topcode.cloud">topcode.cloud</a>
 */
@Getter
public enum PayChannel {
    /**
     * 支付宝
     */
    ALIPAY(1, "ALIPAY"),
    /**
     * 微信支付
     */
    WECHAT(2, "WECHAT");
    private Integer id;
    private String name;

    private PayChannel(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public static PayChannel getChannel(Integer id) {
        for (PayChannel c : PayChannel.values()) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }
}