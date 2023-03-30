package cloud.topcode.open.pay.plugin.wechatpay.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Jon
 * url: <a href="https://jon.wiki">Jon's blog</a>
 * url: <a href="https://topcode.cloud">topcode.cloud</a>
 */
@Data
public class BaseReturn implements Serializable {
    private String code;
    private String message;
    private int statusCode;
}
