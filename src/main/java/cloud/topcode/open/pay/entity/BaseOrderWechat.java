package cloud.topcode.open.pay.entity;

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
public class BaseOrderWechat implements Serializable {
    /**
     * 微信：单优惠标记
     * 非必须
     */
    private String goodsTag;
    /**
     * 微信：电子发票入口开放标识
     * 非必须
     */
    private boolean invoice = false;
    /**
     * 微信：商品描述
     */
    private String description;

}
