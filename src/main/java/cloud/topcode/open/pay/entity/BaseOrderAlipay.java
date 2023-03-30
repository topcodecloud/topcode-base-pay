package cloud.topcode.open.pay.entity;

import com.alibaba.fastjson2.annotation.JSONField;
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
public class BaseOrderAlipay implements Serializable {

    /**
     * 支付宝：针对用户授权接口，获取用户相关数据时，用于标识用户授权关系
     * 非必须
     */
    @JSONField(name = "auth_token")
    private String authToken;
    /**
     * 支付宝：用户付款中途退出返回商户网站的地址
     * 非必须
     */
    @JSONField(name = "quit_url")
    private String quitUrl;
    /**
     * 支付宝：商户传入业务信息，具体值要和支付宝约定，应用于安全，营销等参数直传场景，格式为json格式
     * 非必须
     */
    @JSONField(name = "business_params")
    private String businessParams;
    /**
     * 业务扩展参数
     * 非必须
     */
    @JSONField(name = "extend_params")
    private ExtendParams extendParams;

}

