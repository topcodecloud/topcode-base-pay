package cloud.topcode.open.pay.plugin.wechatpay.form;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Jon
 * url: <a href="https://jon.wiki">Jon's blog</a>
 * url: <a href="https://topcode.cloud">topcode.cloud</a>
 */
@Data
public class FormPreOrder implements Serializable {
    private int goodsId;
    private int num;
    private String openid;
}

