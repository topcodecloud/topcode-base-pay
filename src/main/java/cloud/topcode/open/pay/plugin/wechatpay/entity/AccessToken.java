package cloud.topcode.open.pay.plugin.wechatpay.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class AccessToken implements Serializable {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("expires_in")
    private Long expiresIn;
    @JsonProperty("refresh_token")
    private String refreshToken;
    private String openid;
    private String scope;
    @JsonProperty("is_snapshotuser")
    private Integer isSnapshotuser;
    private String errcode;
    private String errmsg;
}
