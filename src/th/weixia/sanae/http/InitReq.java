package th.weixia.sanae.http;

import th.weixia.common.log.WxLog;
import th.weixia.sanae.utils.WxStringUtil;

/**
 * Created by sy on 16.10.13.
 */
public class InitReq extends BaseCryptRequest {

    private String token;
    private String packageName;
    private String appSign;
    private String sec;

    public void setToken(String token) {
        this.token = token;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setAppSign(String appSign) {
        this.appSign = appSign;
    }

    public void setSec(String sec) {
        this.sec = sec;
    }

    public InitReq(String url) {
        super(url, "mobileark.secret.init", "1.0");
    }

    @Override
    public String getHttpReqBody() {
        super.getHttpReqBody();
        try {
            json.put("token", token);
            json.put("type", "2"); //1.ios 2、android 3、pc
            json.put("appid", packageName);
            json.put("appsign", appSign);
            json.put("sec", sec);
        } catch (Exception e) {
            WxLog.e("initreq", e.getMessage(), e);
        }
        return WxStringUtil.json2Normal(json.toString());
    }
}