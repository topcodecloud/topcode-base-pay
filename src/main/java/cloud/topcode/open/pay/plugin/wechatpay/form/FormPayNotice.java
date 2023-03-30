package cloud.topcode.open.pay.plugin.wechatpay.form;


import cloud.topcode.open.pay.plugin.wechatpay.entity.PayNotice;
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
public class FormPayNotice extends PayNotice implements Serializable {

}