package cloud.topcode.open.pay.plugin.wechatpay.form;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Jon
 * url: <a href="https://jon.wiki">Jon's blog</a>
 * url: <a href="https://topcode.cloud">topcode.cloud</a>
 */
@Data
public class FormCode implements Serializable {
    private String code;
}
