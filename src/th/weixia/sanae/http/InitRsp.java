package th.weixia.sanae.http;

import org.apache.http.HttpResponse;

import java.util.Date;

import th.weixia.common.http.rsp.BaseJsonResponseMsg;
import th.weixia.common.log.WxLog;

/**
 * Created by sy on 16.10.13.
 */
public class InitRsp extends BaseJsonResponseMsg {

    private String sessionId;

    private long sessionTimeout;

    @Override
    public void init(HttpResponse httpresponse) {
        super.init(httpresponse);
        if (isOK()) {
            try {
                sessionId = jso.getString("sessionid");
                //                      当前时间                      服务器设置的有效时间（分钟）     本地提前时间    转为毫秒
                sessionTimeout = new Date().getTime() + ((Integer.valueOf(jso.getString("timeout")) - 3) * 60 * 1000);

            } catch (Exception e) {
                WxLog.e("InitRsp", e.getMessage(), e);
                resultcode = "";
            }
        }
    }

    public String getSessionId() {
        return sessionId;
    }

    public Long getSessionTimeout() {
        return sessionTimeout;
    }
}
