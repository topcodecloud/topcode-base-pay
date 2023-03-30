package cloud.topcode.open.pay.plugin.wechatpay;

import cloud.topcode.open.pay.entity.BaseOrder;
import cloud.topcode.open.pay.entity.PayType;
import cloud.topcode.open.pay.plugin.wechatpay.constant.WechatConstant;
import cloud.topcode.open.pay.plugin.wechatpay.constant.WechatMessage;
import cloud.topcode.open.pay.plugin.wechatpay.entity.*;
import cloud.topcode.open.pay.plugin.wechatpay.util.WechatHttp;
import cloud.topcode.open.pay.plugin.wechatpay.util.WechatUtils;
import cloud.topcode.open.pay.util.BaseUtils;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;

/**
 * @author Jon
 * url: <a href="https://jon.wiki">Jon's blog</a>
 * url: <a href="https://topcode.cloud">topcode.cloud</a>
 */
@Component
@Slf4j
public class WechatHandler {

    /**
     * 付款流程
     * 公众号支付第一步
     * 生成公众号JSAPI获取code的URL，通过微信浏览器打开URL，等待微信回调返回code
     *
     * @param state 可不传
     * @param appId appId
     * @param wechatCodeReturnUrl codeUrl
     * @return url
     */
    public String createJsapiCodeUrl(String state,String appId,String wechatCodeReturnUrl) throws UnsupportedEncodingException {
        if (StringUtils.isEmpty(state)) {
            state = "STATE";
        }
        String url = URLEncoder.encode(
                wechatCodeReturnUrl,
                StandardCharsets.UTF_8.toString()
        );
        String fullUrl = WechatConstant.URL_JSAPI_CODE.replace("{APPID}", appId)
                .replace("{REDIRECT_URI}", url)
                .replace("{SCOPE}", "snsapi_base")
                .replace("{STATE}", state);
        return fullUrl;
    }


    /**
     * 小程序支付，使用code获得openId
     *
     * @param code 来自小程序前端传过来的值
     * @param appId appid
     * @param mchid 商户号
     * @param appSecret 秘钥
     * @param serialNo 证书号
     * @param wechatPrivateKey 私钥
     * @return openId等信息
     * @throws Exception
     */
    public Code2SessionResult microPayGetOpenId(String code,String appId,String mchid,String appSecret,String serialNo,PrivateKey wechatPrivateKey) throws Exception {
//        System.out.println("code:" + code);
//        System.out.println("appid:" + WechatConstant.wechatMicroAppid);
//        System.out.println("secret:" + WechatConstant.wechatMicroSecret);
        String url = WechatConstant.URL_CODE2SESSION.replace("{APPID}", appId)
                .replace("{SECRET}", appSecret)
                .replace("{JSCODE}", code);

        Code2SessionResult r = WechatHttp.httpGet(url, "", Code2SessionResult.class,mchid,serialNo, wechatPrivateKey);
//        System.out.println(r.toString());
        return r;
    }

    /**
     * 付款流程：公众号，服务端发起并获得付款参数 1. 获得prepay_id
     * 公众号支付第三步，生成客户端需要的参数 2. 组装前端要的参数
     * 再由客户端将参数发送给微信  3. 传给前端，再由前端发给微信服务器
     *
     * @param payType 支付类型
     * @param order   订单
     * @param code    微信返回的code
     * @param appId appid
     * @param mchid 商户号
     * @param appSecret 秘钥
     * @param serialNo 证书号
     * @param wechatPrivateKey 私钥
     * @param wechatPayNotifyUrl 支付回调URL
     * @return ClientPayParam
     * @throws Exception
     */
    public WechatClientPayParam prePayJsapiMp(PayType payType, BaseOrder order, String code,
                                              String appId,String mchid,String appSecret,String serialNo,PrivateKey wechatPrivateKey,String wechatPayNotifyUrl
                                              ) throws Exception {
        System.out.println("这个是公众号");
        AccessToken accessToken = getAccessToken(code,appId,mchid,appSecret,serialNo,wechatPrivateKey);
        Payer payer = new Payer().setOpenid(accessToken.getOpenid());
        return createClientPayParam(payType, order, payer,appId,mchid,serialNo,wechatPrivateKey,wechatPayNotifyUrl);
    }

    /**
     * 付款流程：小程序，服务端发起并获得参数 1. 获得prepay_id
     * 小程序支付第三步，生成客户端需要的参数 2. 组装前端要的参数
     * 再由客户端将参数发送给微信 3. 传给前端，再由前端发给微信服务器
     *
     * @param payType 支付类型
     * @param order   订单
     * @param openId  支付者openId（通过微信客户端获取）
     * @param appId appId
     * @param mchid 商户号
     * @param serialNo 证书号
     * @param wechatPrivateKey 私钥
     * @param wechatPayNotifyUrl 支付回调URL
     * @return ClientPayParam
     * @throws Exception
     */
    public WechatClientPayParam prePayJsapiMicro(PayType payType, BaseOrder order, String openId,
                                                 String appId,String mchid,String serialNo,PrivateKey wechatPrivateKey,String wechatPayNotifyUrl
                                                 ) throws Exception {
        System.out.println("这个是小程序");
        Payer payer = new Payer().setOpenid(openId);
        return createClientPayParam(payType, order, payer,appId,mchid,serialNo,wechatPrivateKey,wechatPayNotifyUrl);
    }

    /**
     * 付款流程：通知处理
     * 处理支付成功通知(解密)
     *
     * @param notice 微信发起的通知
     * @param wechatV3key v3key
     * @return 通知体
     */
    public PayNotice payNotify(PayNotice notice, String wechatV3key) throws Exception {
        PayNoticeResourceOrigin origin = null;
        if (notice != null) {
            if (notice.getEventType().equals(WechatConstant.ORDER_NOTICE_SUCCESS)) {
                String s = WechatUtils.decryptToString(notice.getResource().getAssociatedData(),
                        notice.getResource().getNonce(), notice.getResource().getCiphertext(),wechatV3key);
                origin = JSONObject.parseObject(s, PayNoticeResourceOrigin.class);
                notice.setOrigin(origin);
            }
        }
        return notice;
    }

    /**
     * 退款流程：发起退款
     * 发起退款并获得退款结果
     *
     * @param outTradeNo  商户订单号
     * @param outRefundNo 商户退单单号
     * @param reason      退款原因
     * @param refund      退款金额
     * @param total       订单金额
     * @param mchid 商户号
     * @param serialNo 证书号
     * @param wechatPrivateKey 私钥
     * @param wechatRefundNotifyUrl 退款回调地址
     * @return 退款返回参数
     */
    public RefundCreateReturn payRefund(String outTradeNo, String outRefundNo, String reason, int refund, int total,
    String mchid,String serialNo,PrivateKey wechatPrivateKey,String wechatRefundNotifyUrl
    ) throws Exception {
        RefundCreate refundCreate = createRefundParam(outTradeNo, outRefundNo, reason, refund, total,wechatRefundNotifyUrl);
        String s = JSON.toJSONString(refundCreate);
        RefundCreateReturn r = WechatHttp.httpPost(WechatConstant.URL_JSAPI_REFUND, s, RefundCreateReturn.class,mchid,serialNo,wechatPrivateKey);
        if (r.getStatusCode() != WechatConstant.STATUS_CODE_OK) {
            r.setOutTradeNo(outTradeNo);
            r.getAmount().setRefund(refund);
            r.getAmount().setTotal(total);
            r.setOutRefundNo(refundCreate.getOutRefundNo());
        } else {
            r.setStatusCode(0);
            r.setMessage(WechatConstant.SUCCESS);
        }
        return r;
    }

    /**
     * 退款流程：处理退款通知
     * 处理退款通知（并解密）
     *
     * @param notice 退款通知
     * @param wechatV3key v3key
     * @return 解密
     */
    public RefundNotice payRefundNotify(RefundNotice notice,String wechatV3key) throws Exception {
        RefundNoticeResourceOrigin origin = null;
        if (notice != null) {
            if (notice.getEventType().equals(WechatConstant.REFUND_NOTICE_SUCCESS)) {
                String s = WechatUtils.decryptToString(notice.getResource().getAssociatedData(),
                        notice.getResource().getNonce(), notice.getResource().getCiphertext(),wechatV3key);
                origin = JSONObject.parseObject(s, RefundNoticeResourceOrigin.class);
                notice.setOrigin(origin);
            }
        }
        return notice;
    }

    /**
     * 关闭流程
     * 关闭订单
     *
     * @param outTradeNo 订单编号
     * @param mchid 商户号
     * @param serialNo 证书号
     * @param wechatPrivateKey 私钥
     * @return 关闭情况
     */
    public Object payClose(String outTradeNo,String mchid,String serialNo,PrivateKey wechatPrivateKey) throws Exception {
        if (StringUtils.isNotEmpty(outTradeNo)) {
            String url = WechatConstant.URL_ORDER_CLOSE.replace("{out_trade_no}", outTradeNo);
            PayClose close = new PayClose().setMchid(mchid);
            Object o = WechatHttp.httpPost(url, JSON.toJSONString(close), Object.class,mchid,serialNo,wechatPrivateKey);
            return o;
        }
        throw new Exception(WechatMessage.WECHAT_ERROR_MESSAGE_MISSING_OUT_TRADE_NO);
    }

    /**
     * 查询流程：查询支付
     * 查询订单
     *
     * @param outTradeNo 订单编号
     * @param mchid 商户号
     * @param serialNo 证书号
     * @param wechatPrivateKey 私钥
     * @return 查询结果
     */
    public PayQueryReturn payQuery(String outTradeNo,String mchid,String serialNo,PrivateKey wechatPrivateKey) throws Exception {
        if (StringUtils.isNotEmpty(outTradeNo)) {
            String url = WechatConstant.URL_ORDER_QUERY
                    .replace("{out_trade_no}", outTradeNo)
                    .replace("{mchid}", mchid);
            PayQueryReturn payQueryReturn = WechatHttp.httpGet(url, "", PayQueryReturn.class,mchid,serialNo,wechatPrivateKey);
            return payQueryReturn;
        }
        throw new Exception(WechatMessage.WECHAT_ERROR_MESSAGE_MISSING_OUT_TRADE_NO);
    }

    /**
     * 查询流程：查询退款
     * 查询退款
     *
     * @param outRefundNo 退款单号
     * @param mchid 商户号
     * @param serialNo 证书号
     * @param wechatPrivateKey 私钥
     * @return 退款内容
     */
    public RefundQueryReturn payRefundQuery(String outRefundNo,String mchid,String serialNo,PrivateKey wechatPrivateKey) throws Exception {
        if (StringUtils.isNotEmpty(outRefundNo)) {
            String url = WechatConstant.URL_REFUND_QUERY
                    .replace("{out_refund_no}", outRefundNo);
            RefundQueryReturn orderQueryReturn = WechatHttp.httpGet(url, "", RefundQueryReturn.class,mchid,serialNo,wechatPrivateKey);
            return orderQueryReturn;
        }
        throw new Exception(WechatMessage.WECHAT_ERROR_MESSAGE_MISSING_REFUND_NO);
    }

    /**
     * 付款流程，参数封装
     * 封装JSAPI订单信息（小程序，公众号）
     *
     * @param payType   支付类型
     * @param baseOrder 订单信息
     * @param payer     payer
     * @param appId appId
     * @param mchid 商户号
     * @param wechatPayNotifyUrl 支付回调地址
     * @return JSAPI订单详情
     */
    private Pay4Jsapi createJsapiOrderParams(PayType payType, BaseOrder baseOrder, Payer payer,String appId,String mchid,String wechatPayNotifyUrl) {
        AmountOrder amount = new AmountOrder()
                .setCurrency(WechatConstant.CURRENCY)
                .setTotal(baseOrder.getAmount());
        Pay4Jsapi order = new Pay4Jsapi()
                .setAppid(appId)
                .setMchid(mchid)
                .setOutTradeNo(baseOrder.getOutTradeNo())
                .setDescription(baseOrder.getSubject())
                .setNotifyUrl(wechatPayNotifyUrl)
                .setAmount(amount)
                .setPayer(payer);
        if (StringUtils.isNotEmpty(baseOrder.getOtherParams())) {
            order.setAttach(baseOrder.getOtherParams());
        }
        if (StringUtils.isNotEmpty(baseOrder.getTimeExpire())) {
            order.setTimeExpire(baseOrder.getTimeExpire());
        }
//        if(baseOrder.getBaseOrderWechat()!=null){
//            BaseOrderWechat baseOrderWechat = baseOrder.getBaseOrderWechat();
//            order.setInvoice(baseOrderWechat.isInvoice());
//            if(StringUtils.isNotEmpty(baseOrderWechat.getGoodsTag())){
//                order.setGoodsTag(baseOrderWechat.getGoodsTag());
//            }
//            if(StringUtils.isNotEmpty(baseOrderWechat.getDescription())){
//                order.setDescription(baseOrderWechat.getDescription());
//            }
//        }
        return order;
    }

    /**
     * 退款流程，参数封装
     * 生成退款信息
     *
     * @param outTradeNo 商户订单号
     * @param outRefundNo 退款订单号
     * @param reason 退款原因
     * @param refund 退款（分）
     * @param total 总金额（分）
     * @param wechatRefundNotifyUrl 退款回调地址
     * @return RefundCreate
     */
    private RefundCreate createRefundParam(String outTradeNo, String outRefundNo, String reason, int refund, int total, String wechatRefundNotifyUrl) {
        AmountRefund amount = new AmountRefund()
                .setRefund(refund)
                .setTotal(total)
                .setCurrency(WechatConstant.CURRENCY);
        RefundCreate refundCreate = new RefundCreate()
                .setOutRefundNo(outRefundNo)
                .setAmount(amount)
                .setNotifyUrl(wechatRefundNotifyUrl)
                .setOutTradeNo(outTradeNo)
                .setReason(reason)
                .setFundsAccount(WechatConstant.REFUND_ACCOUNT);
        return refundCreate;
    }


    /**
     * 付款流程
     * 公众号支付第二步
     * 通过code获取AccessToken
     *
     * @param code 第一步获取的code
     * @param appid appid
     * @param appSecret app secret
     * @param mchid 商户号
     * @param serialNo 证书系列号
     * @param wechatPrivateKey 微信私钥
     * @return AccessToken
     */
    private AccessToken getAccessToken(String code,String appid,String mchid,String appSecret, String serialNo, PrivateKey wechatPrivateKey) throws Exception {
        String accessUrl = WechatConstant.URL_JSAPI_ACCESS_TOKEN.replace("{APPID}", appid)
                .replace("{SECRET}", appSecret)
                .replace("{CODE}", code);
        JSONObject object = WechatHttp.httpGet(accessUrl, "", JSONObject.class,mchid,serialNo,wechatPrivateKey);
        AccessToken accessToken = new AccessToken();
        accessToken.setAccessToken(object.getString("access_token"))
                .setExpiresIn(object.getLong("expires_in"))
                .setRefreshToken(object.getString("refresh_token"))
                .setOpenid(object.getString("openid"))
                .setScope(object.getString("scope"))
                .setIsSnapshotuser(object.getInteger("is_snapshotuser"))
                .setErrcode(object.getString("errcode"))
                .setErrmsg(object.getString("errmsg"));
        return accessToken;
    }

    /**
     * 付款流程：参数组装
     * 公众号支付第一步
     * 创建JSAPI客户端必须的参数
     * 付款流程：发起JSAPI预支付（向微信发送订单信息），获得返回的prepay_id后封装客户端支付参数包
     * @param payType   支付类型
     * @param baseOrder 基础订单
     * @param payer     支付者
     * @param appId appid
     * @param mchid 商户号
     * @param serialNo 证书系列号
     * @param wechatPrivateKey 私钥
     * @param wechatPayNotifyUrl 支付回调地址
     * @return 客户端需要的参数
     * @throws Exception
     */
    private WechatClientPayParam createClientPayParam(PayType payType, BaseOrder baseOrder, Payer payer,String appId,String mchid, String serialNo, PrivateKey wechatPrivateKey, String wechatPayNotifyUrl) throws Exception {
//        String appId = getAppId(payType);
        Pay4Jsapi pay4Jsapi = createJsapiOrderParams(payType, baseOrder, payer,appId,mchid,wechatPayNotifyUrl);
        String s = JSON.toJSONString(pay4Jsapi);
        Pay4JsapiReturn r = WechatHttp.httpPost(WechatConstant.URL_JSAPI_ORDER, s, Pay4JsapiReturn.class,mchid,serialNo,wechatPrivateKey);
        WechatClientPayParam param = new WechatClientPayParam()
                .setAppid(appId)
                .setPackages("prepay_id=" + r.getPrepayId())
                .setTimeStamp(BaseUtils.getCurrentTimeStamp())
                .setNonceStr(BaseUtils.getNonceStr())
                .setSignType(WechatConstant.SIGN_TYPE);
        String jsapiSign = WechatHttp.getJsapiSign(param.getNonceStr(), param.getPackages(),appId, param.getTimeStamp(),wechatPrivateKey);
        param.setPaySign(jsapiSign);
        return param;
    }

}
